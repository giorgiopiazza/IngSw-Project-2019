package controller;

import enumerations.*;
import exceptions.AdrenalinaException;
import exceptions.game.InvalidGameStateException;
import exceptions.game.InvalidKillshotNumberException;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.actions.BotAction;
import model.player.*;
import network.message.*;
import network.server.Server;
import utility.InputValidator;
import utility.LobbyTimer;
import utility.TimerRunListener;
import utility.persistency.SaveGame;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * This Class is the Controller that receives Messages, validates them and moves the Game State to make the game evolve
 */
public class GameManager implements TimerRunListener, Serializable {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 5;
    private static final long serialVersionUID = 7587280124972034331L;

    private final transient Server server;
    private PossibleGameState gameState;
    private final Game gameInstance;
    private final GameLobby lobby;
    private transient RoundManager roundManager;
    private ShootParameters shootParameters;

    private final transient int lobbyTimeoutTime;
    private transient Timer lobbyTimer;
    private transient boolean lobbyTimerRunning = false;

    /**
     * Creates an instance of {@link GameManager GameManager} binding the server tha will send messages to him
     *
     * @param server           the Server to be bind
     * @param skullNum         number of skulls in this game
     * @param lobbyTimeoutTime the lobby timeout time in seconds
     */
    public GameManager(Server server, boolean terminator, int skullNum, int lobbyTimeoutTime) {
        this.server = server;
        this.gameState = PossibleGameState.GAME_ROOM;
        this.lobby = new GameLobby(terminator, skullNum);
        this.gameInstance = Game.getInstance();
        this.roundManager = new RoundManager(this);

        this.lobbyTimeoutTime = lobbyTimeoutTime * 1000;
    }

    /**
     * Creates an instance of {@link GameManager GameManager} binding the new server and the GameManager of the game
     * that is going to be reloaded
     *
     * @param server           the Server to be bind
     * @param savedGameManager the saved {@link GameManager GameManager} from which the {@link Game Game} is going to restart
     * @param lobbyTimeoutTime the lobby timeout time in seconds
     */
    public GameManager(Server server, GameManager savedGameManager, int lobbyTimeoutTime) {
        this.server = server;
        this.gameState = savedGameManager.gameState;
        this.lobby = savedGameManager.lobby;
        this.gameInstance = Game.getInstance();
        this.shootParameters = savedGameManager.shootParameters;

        this.lobbyTimeoutTime = lobbyTimeoutTime * 1000;
        this.roundManager = new RoundManager(this);
    }

    /**
     * @return the instance of the {@link Game Game} instance present in the saved {@link GameManager GameManager}
     */
    public Game getGameInstance() {
        return this.gameInstance;
    }

    /**
     * @return the instance of the {@link RoundManager RoundManager}
     */
    public RoundManager getRoundManager() {
        return this.roundManager;
    }

    /**
     * Method that changes the state of the {@link GameManager GameManager} which represents the evolving state of the game
     *
     * @param changeState the new State to be reached
     */
    void changeState(PossibleGameState changeState) {
        gameState = changeState;
    }

    /**
     * Method called at the end of the game, when the last state is reached to handle the declaration of the winners
     * by calculating the last points remaining on the map
     */
    void endGame() {
        WinnersResponse winners;
        handleLastPointsDistribution();
        handleKillShotTrackDistribution();
        winners = declareWinner(initPlayerPoints());
        changeState(PossibleGameState.GAME_ENDED);
        server.sendMessageToAll(winners);
    }

    /**
     * Calculates UserPlayerState based on GameManager machine
     *
     * @param username of the player
     * @return the UserPlayerState in which the player is
     */
    public UserPlayerState getUserPlayerState(String username) {
        UserPlayer userPlayer = (UserPlayer) gameInstance.getPlayerByName(username);

        if (userPlayer == null) {
            return null;
        }

        if (roundManager.getTurnManager().getTurnOwner().equals(userPlayer)) {
            if (gameInstance.isBotPresent() && gameInstance.getBot().getPosition() == null) {
                if (gameInstance.getBot().isDead()) {
                    return UserPlayerState.BOT_RESPAWN;
                } else {
                    return UserPlayerState.BOT_SPAWN;
                }
            } else if (userPlayer.isDead()) {
                return UserPlayerState.DEAD;

            } else {
                return mapStates();
            }

        } else {
            return UserPlayerState.FIRST_ACTION;
        }
    }

    /**
     * @return the {@link UserPlayerState UserPlayerState} corresponding to {@link GameManager GameManager} state
     */
    private UserPlayerState mapStates() {
        switch (gameState) {

            case GAME_STARTED:
                return mapGameStartedWithPlayerState(roundManager.getTurnManager().getTurnOwner().getPlayerState());

            case FINAL_FRENZY:
                return UserPlayerState.FIRST_FRENZY_ACTION;

            case SECOND_ACTION:
                if (gameInstance.getState() == GameState.NORMAL) {
                    return UserPlayerState.SECOND_ACTION;
                } else {
                    return UserPlayerState.SECOND_FRENZY_ACTION;
                }

            case GRANADE_USAGE:
                return UserPlayerState.GRENADE_USAGE;

            case ACTIONS_DONE:
            case FRENZY_ACTIONS_DONE:
                return UserPlayerState.ENDING_PHASE;

            case MANAGE_DEATHS:
                return UserPlayerState.DEAD;

            case SCOPE_USAGE:
                if (!roundManager.getTurnManager().isSecondAction()) {
                    return UserPlayerState.FIRST_SCOPE_USAGE;
                } else {
                    return UserPlayerState.SECOND_SCOPE_USAGE;
                }

            case TERMINATOR_RESPAWN:
                return UserPlayerState.BOT_RESPAWN;

            case MISSING_TERMINATOR_ACTION:
                return UserPlayerState.BOT_ACTION;

            case GAME_ENDED:
                return UserPlayerState.GAME_ENDED;

            default:
                // they called her: THE CRASHING EXCEPTION... always reached, but never catched...
                throw new InvalidGameStateException();
        }
    }

    /**
     * Maps the state of the game manager caring of the state of the turn owner
     *
     * @param playerState the {@link PossiblePlayerState PlayerState} of the turn owner
     * @return the corresponding {@link UserPlayerState UserPlayerState}
     */
    private UserPlayerState mapGameStartedWithPlayerState(PossiblePlayerState playerState) {
        switch (playerState) {
            case FIRST_SPAWN:
                return UserPlayerState.SPAWN;
            case SPAWN_TERMINATOR:
                return UserPlayerState.BOT_SPAWN;
            default:
                return UserPlayerState.FIRST_ACTION;
        }
    }

    /**
     * Main method on which the class is based. It is the method that receives messages, validate them and then execute
     * changing the state of the real model
     *
     * @param receivedMessage Message received by the server from a Client that wants to act
     * @return a {@link Message Message} which contains the result of the received message
     */
    public Message onMessage(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_ENDED) {
            return new Response("GAME ENDED", MessageStatus.ERROR);
        }

        if (!InputValidator.validateInput(receivedMessage) ||
                (gameState != PossibleGameState.GAME_ROOM &&
                        !Game.getInstance().doesPlayerExists(receivedMessage.getSenderUsername()))) {
            return buildInvalidResponse();
        }

        Response tempResponse;

        // on the game setup messages can be received from any player
        if (gameState == PossibleGameState.GAME_ROOM) {
            return firstStateHandler(receivedMessage);
        }

        // if the message received comes from a client that is not the turn owner it is never executed!
        if (!gameInstance.getPlayerByName(receivedMessage.getSenderUsername()).equals(roundManager.getTurnManager().getTurnOwner())) {
            return new Response("Message from a player that is not his turn!", MessageStatus.ERROR);
        }

        // very first round handling
        if (roundManager.getTurnManager().getTurnOwner().getPlayerState() != PossiblePlayerState.PLAYING) {
            return veryFirstRoundHandler(receivedMessage);
        }

        // SPECIAL STATES handling
        tempResponse = specialStatesHandler(receivedMessage);

        if (tempResponse.getStatus() != MessageStatus.NO_RESPONSE) {
            return tempResponse;
        } // else no special States are affected

        switch (receivedMessage.getContent()) {
            case BOT_ACTION:
                return terminatorCheckState(receivedMessage);
            case POWERUP_USAGE:
                return powerupCheckState(receivedMessage);
            case MOVE:
                return moveCheckState(receivedMessage);
            case MOVE_PICK:
                return pickCheckState(receivedMessage);
            case SHOOT:
                return shootCheckState(receivedMessage);
            case RELOAD:
                return reloadCheckState(receivedMessage);
            case PASS_TURN:
                return passCheckState();
            default:    // this must never be reached in a normal Game!
                return new Response("GAME STATE ERROR FOR THIS MESSAGE", MessageStatus.ERROR);
        }
    }

    /**
     * Sub method of the class only used while during the game the {@link Server server} receives disconnection messages from
     * the {@link UserPlayer userPLayers} in the game
     *
     * @param receivedConnectionMessage Message received by the server from a connecting or disconnecting {@link UserPlayer UserPlayer}
     * @return a {@link Message Message} which contains the result of the received message
     */
    public Message onConnectionMessage(Message receivedConnectionMessage) {
        if (gameState == PossibleGameState.GAME_ENDED) {
            return new Response("GAME ENDED", MessageStatus.ERROR);
        }

        if (!InputValidator.validatePlayerUsername(gameInstance.getPlayers(), receivedConnectionMessage)) {
            return new Response("Invalid connection Message", MessageStatus.ERROR);
        }

        if (gameState != PossibleGameState.GAME_ROOM && receivedConnectionMessage.getContent() == MessageContent.GET_IN_LOBBY) {
            if (((LobbyMessage) receivedConnectionMessage).isDisconnection()) {
                return disconnectionHandler((LobbyMessage) receivedConnectionMessage);
            } else {
                return reconnectionHandler((LobbyMessage) receivedConnectionMessage);
            }
        } else {
            throw new InvalidGameStateException();
        }
    }

    /**
     * Method that handels the disconnection of a {@link UserPlayer UserPlayer}.
     * Care, if a player disconnects while using a TAGBACK GRENADE he will not
     * use it by default
     *
     * @param receivedConnectionMessage he {@link LobbyMessage LobbyMessage} received from a player that has disconnected
     * @return a positive or negative {@link Message Message} depending on the fact that the player disconnects or not
     */
    private Message disconnectionHandler(LobbyMessage receivedConnectionMessage) {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();
        boolean gameEnded;

        if (inLobbyPlayers.contains(receivedConnectionMessage)) {
            // if I receive a disconnection message I remove it from the lobby and set the corresponding player state to DISCONNECTED
            inLobbyPlayers.remove(receivedConnectionMessage);
            ((UserPlayer) gameInstance.getPlayerByName(receivedConnectionMessage.getSenderUsername())).setPlayerState(PossiblePlayerState.DISCONNECTED);

            // then I check if in the lobby there are still enough players to continue the game, if not the game ends
            gameEnded = checkStartedLobby();

            if (gameEnded) {
                return new Response("Player disconnected, game has now less then 3 players and then is ending...", MessageStatus.OK);
            } else if (getRoundManager().getTurnManager().getTurnOwner().getUsername().equals(receivedConnectionMessage.getSenderUsername())) {    // if game hasn't ended I check if the disconnected player is the turn owner, if so I change the state, otherwise nothing happens
                if (roundManager.getTurnManager().getTurnOwner().getPossibleActions().contains(PossibleAction.CHOOSE_SPAWN) || roundManager.getTurnManager().getTurnOwner().getPossibleActions().contains(PossibleAction.SPAWN_BOT)) {
                    roundManager.handleRandomSpawn(roundManager.getTurnManager().getTurnOwner().getPosition() == null, gameInstance.isBotPresent() && gameInstance.getBot().getPosition() == null);
                }
                roundManager.handlePassAction();
                return new Response("Turn Owner disconnected, turn is passed to next Player", MessageStatus.OK);
            } else {
                return new Response("Player disconnected from the game", MessageStatus.OK);
            }
        } else {
            return new Response("Disconnection Message from not in lobby Player", MessageStatus.ERROR);
        }
    }

    /**
     * Method that handles the reconnection of a {@link UserPlayer UserPlayer}.
     * Care, if a player reconnects while he would be using a TAGBACK GRENADE
     * he will be reconnected to his next turn because the usage of the powerup
     * will not be used by default
     *
     * @param receivedConnectionMessage the {@link LobbyMessage LobbyMessage} received from a player that wants to reconnect
     * @return a positive or negative {@link Message Message} depending on the fact that the reconnection has or not happened
     */
    private Message reconnectionHandler(LobbyMessage receivedConnectionMessage) {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();

        if (!inLobbyPlayers.contains(receivedConnectionMessage)) {
            // if I receive a reconnection message I add it to the lobby and set the corresponding player state to PLAYING
            lobby.addPlayer(receivedConnectionMessage);
            ((UserPlayer) gameInstance.getPlayerByName(receivedConnectionMessage.getSenderUsername())).setPlayerState(PossiblePlayerState.PLAYING);

            return new ReconnectionMessage(receivedConnectionMessage.getToken(),
                    new GameStateMessage(receivedConnectionMessage.getSenderUsername(),
                            roundManager.getTurnManager().getTurnOwner().getUsername(), false));
        } else {
            return new Response("Reconnection message from already in lobby Player", MessageStatus.ERROR);
        }
    }

    /**
     * @return {@code true} if the lobby is full, otherwise false
     */
    public boolean isLobbyFull() {
        return lobby.isLobbyFull();
    }

    /**
     * This method handles both the extemporary usage of a powerup like: TAGBACK GRANADE or TARGETING SCOPE and the
     * Respawn actions of the {@link Bot Terminator} and {@link UserPlayer UserPlayer}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} in case that one of the cases is matched; default case
     * returns a {@link Response Response} with {@link MessageStatus MessageStatus.NO_RESPONSE} used by the main receiving
     * method {@link #onMessage(Message) onMessage} that will continue handling the {@link Message Message} not handled
     * in this method
     */
    private Response specialStatesHandler(Message receivedMessage) {
        switch (gameState) {
            case GRANADE_USAGE:
                return granadeCheckContent(receivedMessage);
            case SCOPE_USAGE:
                return scopeCheckContent(receivedMessage);
            case TERMINATOR_RESPAWN:
                return checkTerminatorRespawn(receivedMessage);
            case MANAGE_DEATHS:
                return checkPlayerRespawn(receivedMessage);
            case MISSING_TERMINATOR_ACTION:
                return handleTerminatorAsLastAction(receivedMessage);
            default:
                return new Response("Utility Response", MessageStatus.NO_RESPONSE);
        }
    }

    /**
     * This method handles the very first round of a {@link Game Game}; in this state {@link UserPlayer players}, need
     * to spawn before starting acting. Remember that if the {@link Bot Terminator} is present, the first
     * {@link UserPlayer UserPlayer} is the one who spawns it and this must be done before spawning itself
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response veryFirstRoundHandler(Message receivedMessage) {
        switch (receivedMessage.getContent()) {
            case BOT_SPAWN:
                return terminatorSpawnCheckState(receivedMessage);
            case DISCARD_POWERUP:
                return discardPowerupCheckState(receivedMessage);
            default:
                return buildInvalidResponse();
        }
    }

    /**
     * Method used to oblige a player to use the {@link BotAction BotAction} in case he hasn't
     * already performed it
     *
     * @param receivedMessage the {@link Message Message} received that can be both: {@link BotUseRequest UseTerminatorRequest}
     *                        or a {@link PowerupRequest PowerupRequest}
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response handleTerminatorAsLastAction(Message receivedMessage) {
        // only messages to use the terminator action and a powerup can be used!
        switch (receivedMessage.getContent()) {
            case BOT_ACTION:
                return roundManager.handleTerminatorAction((BotUseRequest) receivedMessage, PossibleGameState.MISSING_TERMINATOR_ACTION);
            case POWERUP_USAGE:
                return roundManager.handlePowerupAction((PowerupRequest) receivedMessage);
            default:
                return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes a TAGBACK GRANADE {@link PowerupRequest PowerupRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response granadeCheckContent(Message receivedMessage) {
        if (receivedMessage.getContent() == MessageContent.POWERUP_USAGE || receivedMessage.getContent() == MessageContent.PASS_TURN) {
            return onGrenadeMessage(receivedMessage);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and execute a TARGETING SCOPE {@link PowerupRequest PowerupRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response scopeCheckContent(Message receivedMessage) {
        if (receivedMessage.getContent() == MessageContent.POWERUP_USAGE) {
            return roundManager.handleScopeUsage((PowerupRequest) receivedMessage);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and execute the methods related to the Respawn of the {@link Bot Terminator}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response checkTerminatorRespawn(Message receivedMessage) {
        Bot terminator = (Bot) gameInstance.getBot();
        Response tempResponse;
        if (receivedMessage.getContent() == MessageContent.BOT_SPAWN) {
            tempResponse = roundManager.handleTerminatorRespawn((BotSpawnRequest) receivedMessage);
            if (tempResponse.getStatus() == MessageStatus.OK) {
                // if the Respawn message is validated I can distribute the points of the terminator's playerboard, move the skull from the tracker and then reset his playerboard
                distributePoints(terminator);
                moveSkull(terminator);

                // if the death bot has been overkilled, he marks his overkiller
                if (terminator.getPlayerBoard().getDamageCount() > 11) {
                    getRoundManager().getTurnManager().getTurnOwner().getPlayerBoard().addMark(terminator, 1);
                }

                // then I set back the playerboard to the initial state
                gameInstance.getBot().getPlayerBoard().onDeath();

                return checkFrenzy(tempResponse);
            } else {
                sendPrivateUpdates();
                return tempResponse;
            }
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and execute the methods related to the Respawn of a {@link UserPlayer UserPlayer}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response checkPlayerRespawn(Message receivedMessage) {
        Response tempResponse;
        if (receivedMessage.getContent() == MessageContent.DISCARD_POWERUP) {
            tempResponse = roundManager.handlePlayerRespawn((DiscardPowerupRequest) receivedMessage);
            if (tempResponse.getStatus() == MessageStatus.OK) {
                // if the player respawn is validated I can distribute the points of the terminator's playerboard, move the skull from the tracker and then reset his playerboard
                UserPlayer respawnedPlayer = (UserPlayer) gameInstance.getUserPlayerByUsername(receivedMessage.getSenderUsername());
                distributePoints(respawnedPlayer);
                moveSkull(respawnedPlayer);

                // if the death player has been overkilled, he marks his overkiller
                if (respawnedPlayer.getPlayerBoard().getDamageCount() > 11) {
                    gameInstance.getPlayerByName(respawnedPlayer.getPlayerBoard().getDamages().get(11)).getPlayerBoard().addMark(respawnedPlayer, 1);
                }

                // then I set back the playerboard to the inistial state
                respawnedPlayer.getPlayerBoard().onDeath();

                return checkFrenzy(tempResponse);
            } else {
                sendPrivateUpdates();
                return tempResponse;
            }
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the FirstSpawn of the {@link Bot Terminator}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response terminatorSpawnCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_STARTED && roundManager.getTurnManager().getTurnOwner().getPlayerState() == PossiblePlayerState.SPAWN_TERMINATOR) {
            // remember a player must see the powerups he has drawn before spawning the terminator!
            return roundManager.handleTerminatorFirstSpawn((BotSpawnRequest) receivedMessage);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the Spawn of a {@link UserPlayer UserPlayer} with a
     * {@link DiscardPowerupRequest DiscardPowerupRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response discardPowerupCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_STARTED && roundManager.getTurnManager().getTurnOwner().getPlayerState() == PossiblePlayerState.FIRST_SPAWN) {
            return roundManager.handleFirstSpawn((DiscardPowerupRequest) receivedMessage);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the {@link BotUseRequest TerminatorRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response terminatorCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY
                || gameState == PossibleGameState.ACTIONS_DONE || gameState == PossibleGameState.FRENZY_ACTIONS_DONE) {
            return roundManager.handleTerminatorAction((BotUseRequest) receivedMessage, gameState);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the usage of the powerups: NEWTON or TELEPORTER
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response powerupCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY
                || gameState == PossibleGameState.ACTIONS_DONE || gameState == PossibleGameState.FRENZY_ACTIONS_DONE) {
            return roundManager.handlePowerupAction((PowerupRequest) receivedMessage);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the {@link MoveRequest MoveRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response moveCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY) {
            return roundManager.handleMoveAction((MoveRequest) receivedMessage, handleSecondAction());
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the {@link MovePickRequest PickRequest}
     *
     * @param receivedMessage the {@link Message Message} receivec
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response pickCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY) {
            return roundManager.handlePickAction((MovePickRequest) receivedMessage, handleSecondAction());
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the {@link ShootRequest ShootRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response shootCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY) {
            return roundManager.handleShootAction((ShootRequest) receivedMessage, handleSecondAction());
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the {@link ReloadRequest ReloadRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response reloadCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.ACTIONS_DONE) {
            return roundManager.handleReloadAction((ReloadRequest) receivedMessage);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the {@link PassTurnRequest PassTurnRequest}
     *
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response passCheckState() {
        if (gameState == PossibleGameState.ACTIONS_DONE || gameState == PossibleGameState.FRENZY_ACTIONS_DONE) {
            return roundManager.handlePassAction();
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that handles all the messages that the Game needs to receive to set a starting game
     *
     * @param receivedMessage the MessageReceived from the clients, accepted messages in this state are:
     *                        {@link MessageContent MessaGeContent.GET_IN_LOBBY} and {@link MessageContent MessageContent.LOBBY_VOTE}
     * @return a {@link Message} handled by the server
     */
    private Message firstStateHandler(Message receivedMessage) {
        switch (receivedMessage.getContent()) {
            case GET_IN_LOBBY:
                return lobbyMessageHandler((LobbyMessage) receivedMessage);
            case LOBBY_VOTE:
                return setupMessageHandler((GameVoteMessage) receivedMessage);
            case COLOR:
                return colorRequestHandler();
            default:
                return buildInvalidResponse();
        }
    }

    /**
     * Method that handles the unused color request
     *
     * @return a {@link Message} containing the list of unused colors
     */
    private Message colorRequestHandler() {
        return new ColorResponse(lobby.getUnusedColors());
    }

    /**
     * Method that sets all the informations needed by the Game to get Started. Ath the end if the game is ready to start
     * it is started with the method {@link #startingStateHandler() startingStateHandler}
     */
    private void gameSetupHandler() {
        // first of all I set the terminator presence
        gameInstance.setBot(lobby.getTerminatorPresence());

        // then I can start adding players to the game with the color specified in their Lobby Message
        for (LobbyMessage player : lobby.getInLobbyPlayers()) {
            gameInstance.addPlayer(new UserPlayer(player.getSenderUsername(), player.getChosenColor(), new PlayerBoard()));
        }

        // added the players I can add the terminator, if present
        if (gameInstance.isBotPresent()) {
            gameInstance.buildTerminator();
        }

        // in the end I set the map and the number of Skulls chosen
        try {
            gameInstance.setGameMap(lobby.getFavouriteMap());
        } catch (InvalidMapNumberException e) {
            // never reached here the lobby returns always a valid number
        }

        try {
            gameInstance.setKillShotNum(lobby.getSkullNum());
        } catch (InvalidKillshotNumberException e) {
            // never reached here the lobby returns always a valid number
        }

        // at this point gme should always be ready to start
        if (gameInstance.isGameReadyToStart() && (lobby.getInLobbyPlayers().size() >= MIN_PLAYERS || lobby.getInLobbyPlayers().size() >= MIN_PLAYERS && lobby.getTerminatorPresence())) {
            startingStateHandler();
        }
        // nothing to do here as we said game should always be ready to start at this point
    }

    /**
     * Method that Starts the Game, inits the {@link TurnManager TurnManager} and sets the states to every component that
     * needs one when the game developes from the first state
     */
    private void startingStateHandler() {
        // first I start the game, the turnManager and set the state of the game
        gameInstance.startGame();
        roundManager.initTurnManager();
        changeState(PossibleGameState.GAME_STARTED);

        UserPlayer firstPlayer = roundManager.getTurnManager().getTurnOwner();

        // if the game has the terminator I set the first player state depending on the presence of the terminator
        if (gameInstance.isBotPresent()) {
            firstPlayer.changePlayerState(PossiblePlayerState.SPAWN_TERMINATOR);
        } // else the state remains first spawn and it's ok to start

        // I first need to pick the two powerups for the first player playing
        roundManager.pickTwoPowerups();

        sendPrivateUpdates();
        server.sendMessageToAll(new GameStartMessage(roundManager.getTurnManager().getTurnOwner().getUsername()));
    }

    /**
     * Method that handles the reception of a {@link LobbyMessage LobbyMessage}
     *
     * @param lobbyMessage the {@link LobbyMessage LobbyMessage} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response lobbyMessageHandler(LobbyMessage lobbyMessage) {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();
        ArrayList<PlayerColor> unusedColors = lobby.getUnusedColors();

        // here time expiration has to be verified
        if (lobbyMessage.getContent() == MessageContent.GET_IN_LOBBY && !inLobbyPlayers.contains(lobbyMessage) && !lobbyMessage.isDisconnection()) {
            if (!lobby.isLobbyFull() &&
                    lobbyMessage.getChosenColor() != null && unusedColors.contains(lobbyMessage.getChosenColor())) {
                lobby.addPlayer(lobbyMessage);

                server.sendMessageToAll(new LobbyPlayersResponse(new ArrayList<>(lobby.getInLobbyPlayers().stream().map(LobbyMessage::getSenderUsername).collect(Collectors.toList()))));
                Server.LOGGER.log(Level.INFO, "{0} joined the lobby", lobbyMessage.getSenderUsername());
                timerCheck();
            } else {
                return buildInvalidResponse();
            }
        } else if (lobbyMessage.getContent() == MessageContent.GET_IN_LOBBY && inLobbyPlayers.contains(lobbyMessage) && lobbyMessage.isDisconnection()) {
            inLobbyPlayers.remove(lobbyMessage);
            removeVote(lobbyMessage.getSenderUsername());
            server.sendMessageToAll(new LobbyPlayersResponse(new ArrayList<>(lobby.getInLobbyPlayers().stream().map(LobbyMessage::getSenderUsername).collect(Collectors.toList()))));
            Server.LOGGER.log(Level.INFO, "{0} left the lobby", lobbyMessage.getSenderUsername());
            timerCheck();
            sendPrivateUpdates();
            return new Response("Player removed from Lobby", MessageStatus.OK);
        } else {
            return buildInvalidResponse();
        }

        // then if the game has reached the maximum number of players also considering the terminator presence, it starts
        return checkLobby();
    }


    private void timerCheck() {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();

        if (lobbyTimerRunning) {
            if (inLobbyPlayers.size() < MIN_PLAYERS) {
                lobbyTimer.cancel();
                lobbyTimerRunning = false;
                Server.LOGGER.info("Lobby timer stopped");
            }
        } else {
            if (inLobbyPlayers.size() >= MIN_PLAYERS) {
                lobbyTimer = new Timer();
                lobbyTimer.schedule(new LobbyTimer(this), lobbyTimeoutTime);
                Server.LOGGER.log(Level.INFO, "Lobby timer started ({0} s)", lobbyTimeoutTime / 1000);
                lobbyTimerRunning = true;
            }
        }

    }

    @Override
    public void onTimerRun() {
        Server.LOGGER.info("Lobby timer ended, game is starting");
        gameSetupHandler();
    }

    /**
     * Utility method used by {@link #lobbyMessageHandler(LobbyMessage) lobbyMessageHandler} each time a message is added
     * to the {@link GameLobby Lobby} to control if it has the sufficient informations to start the {@link Game Game}
     *
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response checkLobby() {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();

        if ((lobby.getTerminatorPresence() && inLobbyPlayers.size() == MAX_PLAYERS - 1) ||
                (!lobby.getTerminatorPresence() && inLobbyPlayers.size() == MAX_PLAYERS)) {
            lobbyTimer.cancel();
            lobbyTimerRunning = false;
            gameSetupHandler();
            return new Response("Last player added to lobby, game is starting...", MessageStatus.OK);
        } else {
            return new Response("Player added to lobby", MessageStatus.OK);
        }
    }

    private boolean checkStartedLobby() {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();

        if (inLobbyPlayers.size() < MIN_PLAYERS) {
            endGame();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method used to remove a vote from the lobby when a player disconnects from the game
     *
     * @param disconnectedPlayer String containing the UserName of the disconnected player
     */
    private void removeVote(String disconnectedPlayer) {
        ArrayList<GameVoteMessage> playersVotes = lobby.getVotedPlayers();

        playersVotes.removeIf(voteMessage -> voteMessage.getSenderUsername().equals(disconnectedPlayer));
    }

    public PossibleGameState getGameState() {
        return gameState;
    }

    /**
     * Method that handles the reception of a {@link GameVoteMessage GameVoteMessage} containing informations about the
     * votes of the parameters the Sender wants to play with
     *
     * @param voteMessage the {@link GameVoteMessage GameVoteMessage} received
     * @return a positive or negative {@link Message Response} handled by the server
     */
    private Message setupMessageHandler(GameVoteMessage voteMessage) {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();
        ArrayList<GameVoteMessage> alreadyVotedPlayers = lobby.getVotedPlayers();

        for (LobbyMessage lobbyPlayer : inLobbyPlayers) {
            if (lobbyPlayer.getSenderUsername().equals(voteMessage.getSenderUsername()) && !alreadyVotedPlayers.contains(voteMessage)
                    && voteMessage.getMapVote() > 0 && voteMessage.getMapVote() < 5) {
                lobby.addPlayerVote(voteMessage);
                return new GameVoteResponse("Vote added", MessageStatus.OK);
            }
        }

        return new GameVoteResponse("Vote NOT added player already voted!", MessageStatus.ERROR);
    }

    /**
     * Method used to handle the decision of a player to use or not a TAGBACK GRENADE when damaged.
     *
     * @param receivedMessage the {@link Message Message} received that can be handled if a {@link PowerupRequest PowerupRequest}
     *                        or a {@link PassTurnRequest PassTurnRequest}
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response onGrenadeMessage(Message receivedMessage) {
        switch (receivedMessage.getContent()) {
            case POWERUP_USAGE:
                return roundManager.handleGranadeUsage((PowerupRequest) receivedMessage);
            case PASS_TURN:     // this is a "false" PASS_TURN, turn goes to the next damaged player by the "real" turnOwner
                // implementation then goes directly here

                roundManager.getTurnManager().increaseCount();
                // if the player is the last one to use the granade I set back the state to the previous one and give the turn to the next player
                if (roundManager.getTurnManager().getTurnCount() > roundManager.getTurnManager().getGrenadePossibleUsers().size() - 1) {
                    roundManager.getTurnManager().giveTurn(roundManager.getTurnManager().getMarkedByGrenadePlayer());
                    if (roundManager.getTurnManager().getMarkingTerminator()) {
                        roundManager.afterTerminatorActionHandler(roundManager.getTurnManager().getArrivingGameState());
                        sendPrivateUpdates();
                        return new Response("Granade not used, shooting player is going back to play", MessageStatus.OK);
                    }
                    changeState(roundManager.handleAfterActionState(roundManager.getTurnManager().isSecondAction()));
                    sendPrivateUpdates();
                    return new Response("Granade not used, shooting player is going back to play", MessageStatus.OK);
                }

                sendGrenadePrivateUpdates();
                roundManager.getTurnManager().giveTurn(roundManager.getTurnManager().getGrenadePossibleUsers().get(roundManager.getTurnManager().getTurnCount()));
                return new Response("Granade not used", MessageStatus.OK);
            default:
                return new Response("Invalid Message while in granade state", MessageStatus.ERROR);
        }
    }

    /**
     * Method that handles the Boolean value to be used when performing an action in case it is or not the second action
     * done by the TurnOwner
     *
     * @return the Boolean needed: true -> the action is the second, false -> the first
     */
    private boolean handleSecondAction() {
        switch (gameState) {
            case GAME_STARTED:
                return false;
            case SECOND_ACTION:
                return true;
            case FINAL_FRENZY:
                return !roundManager.getTurnManager().getAfterFrenzy().contains(roundManager.getTurnManager().getTurnOwner());
            default:
                throw new InvalidGameStateException();
        }
    }

    /**
     * Method called at the end of every respawn action to check if the game is going to the final frenzy
     *
     * @param tempResponse the {@link Response Response} built
     * @return the {@link Response Response} passed
     */
    private Response checkFrenzy(Response tempResponse) {
        // if the state changed to FINAL_FRENZY, no other players died and the FRENZY MODE starts
        if (gameState == PossibleGameState.FINAL_FRENZY) {
            gameInstance.setState(GameState.FINAL_FRENZY);
            finalFrenzySetup();
        }

        SaveGame.saveGame(this);
        sendPrivateUpdates();
        return tempResponse;
    }

    /**
     * Method that flips all the PlayerBoards in the FinalFrenzy if needed to all {@link UserPlayer UserPlayers} and also
     * {@link Bot Terminator}
     */
    private void finalFrenzySetup() {
        // boards flipping setup
        if (gameInstance.isBotPresent() && gameInstance.getBot().getPlayerBoard().getDamageCount() == 0) {
            try {
                gameInstance.getBot().getPlayerBoard().flipBoard();
            } catch (AdrenalinaException e) {
                // exceptions thrown can never be reached thanks to the control done
            }
        }

        for (UserPlayer player : gameInstance.getPlayers()) {
            ActionManager.setFrenzyPossibleActions(player, roundManager.getTurnManager());
            if (player.getPlayerBoard().getDamageCount() == 0) {
                try {
                    player.getPlayerBoard().flipBoard();
                } catch (AdrenalinaException e) {
                    // exceptions thrown can never be reached thanks to the control done
                }
            }
        }
    }

    /**
     * Method that distributes points to every player that damaged a death one while he is respawning
     *
     * @param deathPlayer the {@link Player Player} that died and gives points to the ones that damaged him
     */
    private void distributePoints(Player deathPlayer) {
        PlayerBoard deathsPlayerBoard = deathPlayer.getPlayerBoard();
        Integer[] boardPoints = deathsPlayerBoard.getBoardPoints();
        List<String> pointsReceivers = deathsPlayerBoard.getDamages().stream().distinct().collect(Collectors.toList());
        Map<String, DamageCountWrapper> receivers = new HashMap<>();
        Player firstBlooder;

        for (int i = 0; i < pointsReceivers.size(); ++i) {
            int frequency = Collections.frequency(deathsPlayerBoard.getDamages(), pointsReceivers.get(i));
            DamageCountWrapper damageCountWrapper = new DamageCountWrapper(i, frequency);
            receivers.put(pointsReceivers.get(i), damageCountWrapper);
        }

        Map<String, DamageCountWrapper> orderedReceivers = receivers
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        if (!deathsPlayerBoard.isBoardFlipped()) {     // first blood assignment
            firstBlooder = gameInstance.getUserPlayerByUsername(deathsPlayerBoard.getDamages().get(0));
            firstBlooder.addPoints(1);
        }

        int pointsIndex = 0;
        for (Map.Entry entry : orderedReceivers.entrySet()) {
            Player tempReceiver = gameInstance.getUserPlayerByUsername((String) entry.getKey());
            tempReceiver.addPoints(boardPoints[pointsIndex]);
            ++pointsIndex;
        }
    }

    /**
     * Method that moves a Skull from the KillShotTrack of the map to the dieing player and the {@link KillShot Killshot}
     * to the KillShotTrack
     *
     * @param deathPlayer {@link Player Player} that died
     */
    private void moveSkull(Player deathPlayer) {
        int points;
        KillShot killShot;
        String killer = deathPlayer.getPlayerBoard().getDamages().get(10);

        if (deathPlayer.getPlayerBoard().getDamages().size() == 12) {
            points = 2;
        } else {
            points = 1;
        }

        killShot = new KillShot(killer, points);
        if (gameInstance.remainingSkulls() == 0) {
            gameInstance.getFinalFrenzyKillShots().add(killShot);
        } else {
            gameInstance.addKillShot(killShot);
        }
    }

    /**
     * Method that distributes the points at the end of the game from the damaged players remained after the FinalFrenzy
     */
    private void handleLastPointsDistribution() {
        List<UserPlayer> players = gameInstance.getPlayers();
        Bot bot = (Bot) gameInstance.getBot();

        if (gameInstance.isBotPresent() && bot.getPlayerBoard().getDamageCount() > 0) {
            // in the last distribution each damaged player counts as a dead one to calculate points
            distributePoints(bot);
        }

        for (UserPlayer player : players) {
            if (player.getPlayerBoard().getDamageCount() > 0) {
                // in the last distribution each damaged player counts as a dead one to calculate points
                distributePoints(player);
                if (gameInstance.getDeathPlayers().contains(player)) {
                    moveSkull(player);
                }
            }
        }
    }

    /**
     * Method that distributes the points that the KillShotTrack gives to each player who did a {@link KillShot Killshot}
     * during the game. Points are given in the same way as in a {@link PlayerBoard PlayerBoard}
     */
    private void handleKillShotTrackDistribution() {
        Integer[] trackerPoints = gameInstance.getTrackerPoints();
        List<KillShot> killShotTracker = gameInstance.getKillShotTrack();
        List<KillShot> finalFrenzyTracker = gameInstance.getFinalFrenzyKillShots();
        List<String> killers = new ArrayList<>();
        ArrayList<String> distinctKillers;
        Map<String, DamageCountWrapper> receivers = new HashMap<>();

        // first I add the killers on the tracker
        for (KillShot killShot : killShotTracker) {
            killers.add(killShot.getKiller());
        }

        // then I add the killers of the frenzy mode
        for (KillShot killShot : finalFrenzyTracker) {
            killers.add(killShot.getKiller());
        }

        distinctKillers = killers.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        for (int i = 0; i < distinctKillers.size(); ++i) {
            int frequency = Collections.frequency(killers, distinctKillers.get(i));
            frequency += getPointsOnKillShots(distinctKillers.get(i), killShotTracker, finalFrenzyTracker);
            DamageCountWrapper damageCountWrapper = new DamageCountWrapper(i, frequency);
            receivers.put(distinctKillers.get(i), damageCountWrapper);
        }

        Map<String, DamageCountWrapper> orderedReceivers = receivers
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int pointsIndex = 0;
        for (Map.Entry entry : orderedReceivers.entrySet()) {
            Player tempReceiver = gameInstance.getUserPlayerByUsername((String) entry.getKey());
            tempReceiver.addPoints(trackerPoints[pointsIndex]);
            ++pointsIndex;
        }
    }

    /**
     * Utility method for {@link #handleKillShotTrackDistribution() handleKillShotTrackDistribution} method used to count
     * how many {@link KillShot KillShot} each {@code killer} on the KillShotTrack did during the game
     *
     * @param killer             String containing the UserName of the counting {@link Player Player} on the KillShotTrack
     * @param killShotTracker    List of {@link KillShot Killshots} coming from the game map
     * @param finalFrenzyTracker List of {@link KillShot Killshots} containing all the {@link KillShot Killshots} done
     *                           during the FinalFrenzy
     * @return an int that rerpesents the frequency of the Killer's {@link KillShot KillShots} on the KillShotTracker
     */
    private int getPointsOnKillShots(String killer, List<KillShot> killShotTracker, List<KillShot> finalFrenzyTracker) {
        int pointsOnKillShots = 0;

        for (KillShot killShot : killShotTracker) {
            if (killShot.getKiller().equals(killer)) {
                pointsOnKillShots += killShot.getPoints();
            }
        }

        for (KillShot killShot : finalFrenzyTracker) {
            if (killShot.getKiller().equals(killer)) {
                pointsOnKillShots += killShot.getPoints();
            }
        }

        return pointsOnKillShots;
    }

    /**
     * Builds the {@link PlayerPoints PlayerPoint} object for each player in the ga,e
     *
     * @return an ArrayList containing all the {@link PlayerPoints PlayerPoints}
     */
    private ArrayList<PlayerPoints> initPlayerPoints() {
        List<UserPlayer> players = gameInstance.getPlayers();
        ArrayList<PlayerPoints> playerPoints = new ArrayList<>();

        if (gameInstance.isBotPresent()) {
            Bot bot = (Bot) gameInstance.getBot();
            PlayerPoints botPoints = new PlayerPoints(bot.getUsername(), bot.getColor(), bot.getPoints());
            playerPoints.add(botPoints);
        }

        for (UserPlayer player : players) {
            PlayerPoints userPoints = new PlayerPoints(player.getUsername(), player.getColor(), player.getPoints());
            playerPoints.add(userPoints);
        }

        return playerPoints;
    }

    /**
     * Sets the winners from the ArrayList of usernames passed
     *
     * @param usernames    the usernames of the winners
     * @param playerPoints the player points of all the players in the game
     */
    private void setWinners(ArrayList<String> usernames, List<PlayerPoints> playerPoints) {
        for (String winner : usernames) {
            for (PlayerPoints winnerPoints : playerPoints) {
                if (winner.equals(winnerPoints.getUserName())) {
                    winnerPoints.setWinner();
                }
            }
        }
    }

    /**
     * Method that declares the Winner/s of the game with a {@link WinnersResponse WinnerResponse} that will be
     * broadcasted to every player in the game
     *
     * @param winners the ArrayList containing the {@link PlayerPoints PlayerPoints} of all the players
     * @return a {@link WinnersResponse WinnerResponse} containing the ArrayList of {@link Player Player/s} that won the Game
     */
    private WinnersResponse declareWinner(ArrayList<PlayerPoints> winners) {
        List<UserPlayer> players = gameInstance.getPlayers();
        ArrayList<Player> tiePlayers = new ArrayList<>();

        ArrayList<Player> orderedPlayers = players.stream().sorted().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        int maxPoints = orderedPlayers.get(0).getPoints();
        tiePlayers.add(orderedPlayers.get(0));
        for (int i = 1; i < orderedPlayers.size(); ++i) {
            if (orderedPlayers.get(i).getPoints() == maxPoints) {
                tiePlayers.add(orderedPlayers.get(i));
            }
        }

        if (gameInstance.isBotPresent()) {
            if (gameInstance.getBot().getPoints() > maxPoints) {
                setWinners(new ArrayList<>(List.of(gameInstance.getBot().getUsername())), winners);
                return new WinnersResponse(winners);
            } else if (gameInstance.getBot().getPoints() == maxPoints) {
                tiePlayers.add(gameInstance.getBot());
            }
        }

        if (tiePlayers.size() == 1) {
            setWinners(new ArrayList<>(List.of(tiePlayers.get(0).getUsername())), winners);
            return new WinnersResponse(winners);
        } else {
            handleTiePlayers(tiePlayers, winners);
            return new WinnersResponse(winners);
        }
    }

    /**
     * Utility method used by {@link #declareWinner(ArrayList)} )}  declareWinner} method to get the Winner from a List of {@link Player Players}
     * that did the same points during the Game
     *
     * @param tiePlayers ArrayList of {@link Player Players} with the same points at the end of the Game
     * @param winners    ArrayList with all the {@link PlayerPoints PlayerPoints}
     */
    private void handleTiePlayers(ArrayList<Player> tiePlayers, ArrayList<PlayerPoints> winners) {
        List<KillShot> killShotTracker = gameInstance.getKillShotTrack();

        for (KillShot killShot : killShotTracker) {
            for (Player player : tiePlayers) {
                if (player.getUsername().equals(killShot.getKiller())) {
                    setWinners(new ArrayList<>(List.of(player.getUsername())), winners);
                }
            }
        }

        setWinners(new ArrayList<>(tiePlayers.stream().map(Player::getUsername).collect(Collectors.toList())), winners);
    }

    /**
     * Utility Method used to build a negative {@link Response Response}
     *
     * @return a {@link Response Response} with {@link MessageStatus MessageStatus.ERROR}
     */
    private Response buildInvalidResponse() {
        return new Response("Invalid message", MessageStatus.ERROR);
    }

    /**
     * This method sends to all clients the new state of the {@link Game Game}, contained in the
     * {@link model.GameSerialized GameSerialized}. This method is used to send an update of the
     * {@link Game Game} everytime that a normal action is completed
     */
    public void sendPrivateUpdates() {
        List<UserPlayer> players = gameInstance.getPlayers();

        for (UserPlayer player : players) {
            server.sendMessage(player.getUsername(), new GameStateMessage(player.getUsername(), roundManager.getTurnManager().getTurnOwner().getUsername(), false));
        }
    }

    /**
     * This method sends to all clients the new state of the {@link Game Game} whenever a turn is
     * assigned to a player that may use a TAGBACK GRENADE
     */
    void sendGrenadePrivateUpdates() {
        List<UserPlayer> players = gameInstance.getPlayers();

        for (UserPlayer player : players) {
            server.sendMessage(player.getUsername(), new GameStateMessage(player.getUsername(), roundManager.getTurnManager().getTurnOwner().getUsername(), true));
        }
    }

    /**
     * Utility method to send a broadcast message to all the Clients
     *
     * @param message the {@link Message Message} to be sent
     */
    void sendBroadcastMessage(Message message) {
        server.sendMessageToAll(message);
    }

    public String getTurnOwnerUsername() {
        return roundManager.getTurnManager().getTurnOwner().getUsername();
    }

    /**
     * Utility Class Used to manage needed parameters when a Shooter wants to use the TARGETING SCOPE
     */
    class ShootParameters implements Serializable {
        private static final long serialVersionUID = 6251659361269907424L;

        ShootRequest shootRequest;
        Boolean secondAction;

        /**
         * Creates an instance of {@link ShootParameters ShootParameters} saving the previous {@link ShootRequest ShootRequest}
         * and the Boolean used to verify if the {@link model.actions.ShootAction ShootAction} performed is the first or
         * the second
         *
         * @param shootRequest {@link ShootRequest ShootRequest} of the current {@link model.actions.ShootAction ShootAction}
         * @param secondAction Boolean containing the informations to perform an action that is: true -> the second,
         *                     false -> the first
         */
        ShootParameters(ShootRequest shootRequest, boolean secondAction) {
            this.shootRequest = shootRequest;
            this.secondAction = secondAction;
        }
    }

    /**
     * Utility Class that implements {@link Comparable Comparable} used to handle the points distributions both in methods:
     * {@link #distributePoints(Player) distributePoints} and {@link #handleKillShotTrackDistribution() handleKillShotTrackDistribution}
     */
    class DamageCountWrapper implements Comparable<DamageCountWrapper> {
        final int position;
        final int damage;

        /**
         * Creates an instance of {@link DamageCountWrapper DamageCountWrapper} with the informations needed to choose the
         * order of the {@link Player Players} that are receiving points from a Death
         *
         * @param position int that specifies the position of the current {@link Player Player} damage on the death's
         *                 {@link PlayerBoard PlayerBoard}
         * @param damage   int that specifies how many damages the current {@link Player Player} has on the death's
         *                 {@link PlayerBoard PlayerBoard}
         */
        DamageCountWrapper(int position, int damage) {
            this.position = position;
            this.damage = damage;
        }

        /**
         * Method used to compare two playerpoints as they can be ordered for the winners points determination
         *
         * @param otherDamageCountWrapper the other damage to be compared
         * @return true if the first has more damage than the second, otherwise false
         */
        @Override
        public int compareTo(DamageCountWrapper otherDamageCountWrapper) {
            // Reversed compare to get the best damage dealer first
            if (this.damage == otherDamageCountWrapper.damage) {
                return this.position - otherDamageCountWrapper.position;
            } else {
                return otherDamageCountWrapper.damage - this.damage;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DamageCountWrapper that = (DamageCountWrapper) o;
            return position == that.position &&
                    damage == that.damage;
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, damage);
        }
    }
}