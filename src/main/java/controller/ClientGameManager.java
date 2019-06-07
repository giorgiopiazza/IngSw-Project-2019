package controller;

import enumerations.*;
import exceptions.player.ClientRoundManagerException;
import exceptions.player.PlayerNotFoundException;
import model.Game;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.player.Player;
import model.player.UserPlayer;
import network.client.Client;
import network.client.ClientUpdateListener;
import network.client.ClientUpdater;
import network.message.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class ClientGameManager implements ClientGameManagerListener, ClientUpdateListener, Runnable {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private final Object gameSerializedLock = new Object(); // handles GameSerialized parallelism

    private ClientRoundManager roundManager; //manage the rounds of this client
    private GameSerialized gameSerialized;

    private String username;
    private PlayerColor playerColor;

    private String firstPlayer;
    private String turnOwner;
    private boolean turnOwnerChanged = false;

    private String frenzyActivator = null;

    private boolean firstTurn;
    private boolean yourTurn;

    private boolean isBotPresent;
    private boolean wantMoveBot;

    public ClientGameManager() {
        this.firstTurn = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                queue.take().run();
            } catch (InterruptedException e) {
                Logger.getGlobal().severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    protected void startUpdater(Client client) {
        new ClientUpdater(client, this);
    }

    private void startGame() {
        // TODO:  terminator present
        roundManager = new ClientRoundManager(getPlayer(), false);

        if (firstTurn) { // first round
            if (firstPlayer.equals(username)) { // first player to play
                yourTurn = true;
            }

            firstPlayerCommunication(firstPlayer);
            firstTurn = false;
        }

        newTurn();
    }

    private void newTurn() {
        if (yourTurn) {
            makeMove();
        } else {
            notYourTurn();
        }
    }

    private void makeMove() {
        // TODO: player is dead
        switch (roundManager.getUserPlayerState()) {
            case SPAWN:
                roundManager.beginRound();
                spawn();
                botMoveRequest();
                break;

            case BEGIN:
                roundManager.beginRound();
                botMoveRequest();
                break;

            case FIRST_ACTION:
            case SECOND_ACTION:
                firstSecondAction();
                botMoveRequest();
                break;

            case BOT_ACTION:
                // TODO: move of terminator
                break;

            case RELOAD:
                askReload();
                break;
            case END:
                // TODO: end round
                roundManager.endRound();
                break;

            default:
                throw new ClientRoundManagerException("Cannot be here");
        }
    }

    /**
     * Causes the user to perform all the moves it can make in this stage of this round
     */
    private void firstSecondAction() {
        switch (askAction()) {
            case MOVE:
                move();
                break;

            case MOVE_AND_PICK:
                moveAndPick();
                break;

            case SHOOT:
                shoot();
                break;

            case ADRENALINE_PICK:
                // TODO
                break;

            case ADRENALINE_SHOOT:
                // TODO
                break;

            case FRENZY_MOVE:
                // TODO
                break;

            case FRENZY_PICK:
                // TODO
                break;

            case FRENZY_SHOOT:
                // TODO
                break;

            case LIGHT_FRENZY_PICK:
                // TODO
                break;

            case LIGHT_FRENZY_SHOOT:
                // TODO
                break;

            default:
                throw new ClientRoundManagerException("cannot be here");
        }
    }

    private void botMoveRequest() {
        if (roundManager.isBotPresent() && !roundManager.hasBotMoved()) {
            wantMoveBot = askBotMove();
        } else {
            wantMoveBot = false;
        }
    }

    @Override
    public void onUpdate(Message message) {
        switch (message.getContent()) {
            case RESPONSE:
                Response response = (Response) message;
                if (response.getStatus().equals(MessageStatus.ERROR)) {
                    responseError(response.getMessage());
                } else {
                    roundManager.nextMove(wantMoveBot);
                }

                queue.add(this::makeMove);

                if (yourTurn && turnOwnerChanged) { // Use to wait the response before calling newTurn()
                    turnOwnerChanged = false;
                    yourTurn = false;

                    queue.add(this::newTurn);
                }
                break;

            case GAME_STATE:
                GameStateMessage stateMessage = (GameStateMessage) message;

                checkFrenzyMode(stateMessage);

                synchronized (gameSerializedLock) {
                    gameSerialized = stateMessage.getGameSerialized();

                    queue.add(() -> gameStateUpdate(gameSerialized));
                }

                checkTurnChange(stateMessage);
                break;

            case READY:
                GameStartMessage gameStartMessage = (GameStartMessage) message;
                synchronized (gameSerializedLock) {
                    firstPlayer = gameStartMessage.getFirstPlayer();
                    turnOwner = gameStartMessage.getFirstPlayer();

                    isBotPresent = gameSerialized.isBotPresent();

                    queue.add(this::startGame);
                }
                break;

            case LAST_RESPONSE:
                WinnersResponse winnersList = (WinnersResponse) message;
                synchronized (gameSerializedLock) {
                    queue.add(() -> notifyGameEnd(winnersList.getWinners()));
                }
                break;

            case DISCONNECTION:
                break;

            default:
        }

        Logger.getGlobal().log(Level.INFO, "{0}", message);
    }

    private void checkTurnChange(GameStateMessage stateMessage) {
        if (!firstTurn) {
            if (!stateMessage.getTurnOwner().equals(turnOwner)) {
                turnOwner = stateMessage.getTurnOwner();
                turnOwnerChanged = true;
            }

            if (!yourTurn) { // If you are not the turn owner you don't need to wait a response
                turnOwnerChanged = false;

                if (turnOwner.equals(username)) {
                    yourTurn = true;
                }

                queue.add(this::newTurn);
            }
        }
    }

    private void checkFrenzyMode(GameStateMessage stateMessage) {
        if (frenzyActivator == null && stateMessage.getGameSerialized().getCurrentState() == GameState.FINAL_FRENZY) {
            frenzyActivator = stateMessage.getTurnOwner();
        }
    }

    protected UserPlayerState getUserPlayerState() {
        return roundManager.getUserPlayerState();
    }

    protected List<PossibleAction> getPossibleActions() {
        if (roundManager.getGameClientState() == GameClientState.NORMAL)
            return roundManager.possibleActions();
        else {
            return roundManager.possibleFinalFrenzyActions(getPlayers().stream().map(Player::getUsername).collect(Collectors.toList()), frenzyActivator);
        }
    }

    private List<Player> getPlayers() {
        List<Player> players;

        synchronized (gameSerializedLock) {
            players = new ArrayList<>(gameSerialized.getPlayers());
        }

        return players;
    }

    protected GameSerialized getGameSerialized() {
        synchronized (gameSerializedLock) {
            return gameSerialized;
        }
    }

    public List<PowerupCard> getPowerups() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPowerups();
        }
    }

    public List<WeaponCard> getPlayerWeapons(String username) {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPlayerWeapons(username);
        }
    }

    public UserPlayer getPlayer() {
        return (UserPlayer) getPlayerByName(username);
    }

    public Player getPlayerByName(String username) {
        synchronized (gameSerializedLock) {
            Player player;

            if (username.equals(Game.BOT)) {
                if (isBotPresent) {
                    return gameSerialized.getBot();
                } else {
                    player = null;
                }
            } else {
                player = gameSerialized.getPlayers().stream().filter(p -> p.getUsername().equals(username)).findFirst().orElse(null);
            }

            if (player == null) throw new PlayerNotFoundException("player not found, cannot continue with the game");
            return player;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    protected PlayerColor getPlayerColor() {
        return playerColor;
    }

    protected void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }
}
