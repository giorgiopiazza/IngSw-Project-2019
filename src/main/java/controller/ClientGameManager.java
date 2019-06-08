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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class ClientGameManager implements ClientGameManagerListener, ClientUpdateListener, Runnable {
    public static final String TAGBACK_GRANADE = "TAGBACK_GRANADE";
    public static final String TELEPORTER = "TELEPORTER";
    public static final String NEWTON = "NEWTON";
    public static final String TARGETING_SCOPE = "TARGETING_SCOPE";

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private final Object gameSerializedLock = new Object(); // handles GameSerialized parallelism

    private Client client;
    private ClientRoundManager roundManager; // manage the rounds of this client
    private GameSerialized gameSerialized;

    private String username;
    private PlayerColor playerColor;

    private String firstPlayer;
    private String turnOwner;
    private boolean turnOwnerChanged = false;

    private boolean firstTurn;
    private boolean yourTurn;

    private boolean isBotPresent;

    private boolean noChangeStateRequest; // Identify a request that doesn't have to change the player state

    public ClientGameManager() {
        this.firstTurn = true;
        this.noChangeStateRequest = false;
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
        this.client = client;

        new ClientUpdater(client, this);
    }

    private void startGame() {
        roundManager = new ClientRoundManager(isBotPresent);

        if (firstTurn) { // First round
            if (firstPlayer.equals(username)) { // First player to play
                yourTurn = true;

                if (isBotPresent) {
                    roundManager.botSpawn();
                }
            }

            firstPlayerCommunication(firstPlayer);
            firstTurn = false;
        }

        newTurn();
    }

    private void newTurn() {
        if (yourTurn) {
            roundManager.beginRound();
            makeMove();
        } else {
            notYourTurn();
        }
    }

    /**
     * Causes the user to perform all the moves it can make in this stage of this round
     */
    private void makeMove() {
        switch (askAction()) {
            case SPAWN_BOT:
                // TODO
                break;

            case CHOOSE_SPAWN:
            case CHOOSE_RESPAWN:
                spawn();
                break;

            case POWER_UP:
                powerup();
                break;

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

            case BOT_ACTION:
                botAction();
                break;

            case RELOAD:
                reload();
                break;

            case PASS_TURN:
                passTurn();
                break;

            default:
                throw new ClientRoundManagerException("cannot be here");
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

                    if (noChangeStateRequest) {
                        noChangeStateRequest = false;
                    } else {
                        roundManager.nextState();
                    }
                }

                if (roundManager.getUserPlayerState() != UserPlayerState.END) {
                    queue.add(this::makeMove);
                } else {
                    queue.add(roundManager::endRound);
                }

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
                DisconnectionMessage disconnectionMessage = (DisconnectionMessage) message;

                onPlayerDisconnect(disconnectionMessage.getUsername());
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

                checkDeath();

                queue.add(this::newTurn);
            }
        }
    }

    private void checkDeath() {
        if (getPlayer().isDead()) {
            roundManager.death();
        }
    }

    private void checkFrenzyMode(GameStateMessage stateMessage) {
        if (stateMessage.getGameSerialized().getCurrentState() == GameState.FINAL_FRENZY
                && roundManager.getGameClientState() != GameClientState.FINAL_FRENZY) {

            roundManager.setFinalFrenzy();

            List<String> players = getPlayers().stream().map(Player::getUsername).collect(Collectors.toList());

            int activatorIndex = players.indexOf(stateMessage.getTurnOwner());
            int playerIndex = players.indexOf(getUsername());

            roundManager.setSecondFrenzyAction(playerIndex > activatorIndex);
        }
    }

    protected List<PossibleAction> getPossibleActions() {
        switch (roundManager.getUserPlayerState()) {
            case BOT_SPAWN:
                return List.of(PossibleAction.SPAWN_BOT);

            case SPAWN:
                return List.of(PossibleAction.CHOOSE_SPAWN);

            case FIRST_ACTION:
            case SECOND_ACTION:
            case FIRST_FRENZY_ACTION:
            case SECOND_FRENZY_ACTION:
                return getGameActions();

            case BOT_ACTION:
                return List.of(PossibleAction.BOT_ACTION);

            case ENDING_PHASE:
                return getEndingActions();

            case DEAD:
                return List.of(PossibleAction.CHOOSE_RESPAWN);

            default:
                throw new ClientRoundManagerException("Cannot be here: " + roundManager.getUserPlayerState().name());
        }
    }

    private List<PossibleAction> getGameActions() {
        List<PossibleAction> actions;

        if (roundManager.getGameClientState() == GameClientState.NORMAL)
            actions = possibleActions();
        else {
            actions = possibleFinalFrenzyActions();
        }

        if (getPowerups().stream().anyMatch(p -> p.getName().equals(TELEPORTER) || p.getName().equals(NEWTON))) {
            actions.add(PossibleAction.POWER_UP);
        }

        if (roundManager.isBotPresent() && !roundManager.hasBotMoved()) {
            actions.add(PossibleAction.BOT_ACTION);
        }

        return actions;
    }

    /**
     * This method return the possible actions that the player can be in this round.
     * If in the list is present the PossibleAction.RELOAD, this action is not counted and another can be performed.
     * If in the list is present the PossibleAction.BOT_ACTION, means that the next move can be the terminator one,
     * if the round is in the UserPlayerState.SECOND_ACTION state, then the next move is necessarily the terminator one.
     *
     * @return a list with the possible actions that the player can perform in this round
     */
    private List<PossibleAction> possibleActions() {
        List<PossibleAction> actions = new ArrayList<>();
        PlayerBoardState boardState = getPlayer().getPlayerBoard().getBoardState();

        actions.add(PossibleAction.MOVE);

        switch (boardState) {
            case NORMAL:
                actions.add(PossibleAction.MOVE_AND_PICK);
                actions.add(PossibleAction.SHOOT);
                break;

            case FIRST_ADRENALINE:
                actions.add(PossibleAction.ADRENALINE_PICK);
                actions.add(PossibleAction.SHOOT);
                break;

            case SECOND_ADRENALINE:
                actions.add(PossibleAction.ADRENALINE_PICK);
                actions.add(PossibleAction.ADRENALINE_SHOOT);
                break;
        }

        return actions;
    }

    /**
     * Returns the final frenzy actions based on who activated the frenzy mode and the position of the player
     * in the game turn
     *
     * @return the list of possible possibleFinalFrenzyActions for {@code that} player
     */
    private List<PossibleAction> possibleFinalFrenzyActions() {
        List<PossibleAction> actions = new ArrayList<>();

        if (roundManager.isDoubleActionFrenzy()) {
            actions.add(PossibleAction.FRENZY_MOVE);
            actions.add(PossibleAction.FRENZY_SHOOT);
            actions.add(PossibleAction.FRENZY_PICK);
        } else {
            actions.add(PossibleAction.LIGHT_FRENZY_SHOOT);
            actions.add(PossibleAction.LIGHT_FRENZY_PICK);
        }

        return actions;
    }

    private List<PossibleAction> getEndingActions() {
        List<PossibleAction> actions = new ArrayList<>();

        if (getPowerups().stream().anyMatch(p -> p.getName().equals(TELEPORTER) || p.getName().equals(NEWTON))) {
            actions.add(PossibleAction.POWER_UP);
        }
        actions.add(PossibleAction.RELOAD);
        actions.add(PossibleAction.PASS_TURN);

        return actions;
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

    protected boolean sendRequest(Message message) {
        checkChangeStateRequest(message);

        try {
            client.sendMessage(message);
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
            return false;
        }

        return true;
    }

    private void checkChangeStateRequest(Message message) {
        noChangeStateRequest = (roundManager.getUserPlayerState() != UserPlayerState.BOT_ACTION && message.getContent() == MessageContent.BOT_ACTION) ||
                message.getContent() == MessageContent.POWERUP_USAGE;
    }
}
