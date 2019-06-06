package controller;

import enumerations.MessageStatus;
import enumerations.PlayerColor;
import enumerations.PossibleAction;
import enumerations.UserPlayerState;
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
import utility.LobbyTimer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ClientGameManager implements ClientGameManagerListener, ClientUpdateListener {
    private final Object gameSerializedLock = new Object(); // handles GameSerialized parallelism
    private final Object waiter = new Object();     // need to wait for action

    private ClientRoundManager roundManager; //manage the rounds of this client

    private GameSerialized gameSerialized;


    private String username;
    private PlayerColor playerColor;

    private boolean started;
    private boolean finished;
    private List<Player> winners;
    private Player firstPlayer; //the first player to play
    private boolean firstRound;
    private boolean yourRound; //set to true when receive message that is my round
    private boolean botPresent;

    public ClientGameManager() {
        this.firstRound = true;
    }

    public void startWaiter(Client client, ClientUpdateListener clientUpdateListener) {
        new ClientUpdater(client, clientUpdateListener, waiter);


        Timer timer = new Timer();
        TimerTask timerTask = new LobbyTimer(() -> {
            boolean start;

            synchronized (gameSerializedLock) {
                start = this.started;
            }

            if (start) {
                timer.cancel();
                startGame();
            }
        });
        timer.schedule(timerTask, 1000, 1000);
    }

    private void startGame() {
        // TODO:  terminator present
        roundManager = new ClientRoundManager(getPlayer(), false);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (firstRound) { // first round
                    if (firstPlayer.getUsername().equals(username)) { // first player to play
                        yourRound = true;
                    }

                    firstPlayerCommunication(firstPlayer.getUsername());
                    firstRound = false;
                }

                if (yourRound) { // if the first round
                    playRound();
                } else {
                    waitTurn();

                    // wait while ClientUpdater receive something
                    synchronized (waiter) {
                        waiter.wait();
                    }
                }
            } catch (InterruptedException e) {
                Logger.getGlobal().severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Play the entire round of this player
     */
    private void playRound() {
        do {
            makeMove();
        } while (roundManager.roundEnded());
    }

    private void makeMove() {
        // TODO: player is dead
        switch (roundManager.getUserPlayerState()) {
            case SPAWN:
                roundManager.beginRound();
                spawn();
                handleNextAction();
                break;

            case BEGIN:
                handleNextAction();
                break;

            case FIRST_ACTION:
            case SECOND_ACTION:
                firstSecondAction();
                handleNextAction();
                break;

            case TERMINATOR_ACTION:
                // TODO: move of terminator
                handleNextAction();
                break;

            case RELOAD:
                // TODO: Ask Reload
                handleNextAction();
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
    public void firstSecondAction() {
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

    private void handleNextAction() {
        if (roundManager.isBotPresent() && !roundManager.hasBotMoved()) {
            roundManager.nextMove(askBotMove());
        } else {
            roundManager.nextMove();
        }
    }

    @Override
    public void onUpdate(List<Message> messages) {
        for (Message message : messages) {
            switch (message.getContent()) {
                case RESPONSE:
                    Response response = (Response) message;
                    if (response.getStatus().equals(MessageStatus.ERROR)) {
                        // TODO: torno indietro con la macchina a stati
                        // Sei uno stronzo
                    } else {
                        // TODO: vado avanti con la macchina a stati
                    }
                    break;

                case GAME_STATE:
                    GameStateMessage stateMessage = (GameStateMessage) message;
                    synchronized (gameSerializedLock) {
                        // TODO: CONTROLLO CAMBIO TURN OWNER
                        gameSerialized = stateMessage.getGameSerialized();
                        gameStateUpdate(gameSerialized);
                    }
                    break;

                case READY:
                    GameStartMessage gameStartMessage = (GameStartMessage) message;
                    synchronized (gameSerializedLock) {
                        firstPlayer = getPlayerByName(gameStartMessage.getFirstPlayer());
                        botPresent = gameSerialized.isBotPresent();
                        started = true;
                    }
                    break;

                case LAST_RESPONSE:
                    WinnersResponse winnersList = (WinnersResponse) message;
                    synchronized (gameSerializedLock) {
                        this.finished = true;
                        this.winners = winnersList.getWinners();
                    }
                    break;

                case DISCONNECTION:
                    break;

                default:
            }

            Logger.getGlobal().log(Level.INFO, "{0}", message);
        }
    }

    public UserPlayerState getUserPlayerState() {
        return roundManager.getUserPlayerState();
    }

    public List<PossibleAction> getPossibleActions() {
        return roundManager.possibleActions();
    }

    public GameSerialized getGameSerialized() {
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
                if (botPresent) {
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

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }
}
