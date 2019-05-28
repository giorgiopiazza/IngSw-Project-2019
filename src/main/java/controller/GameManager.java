package controller;

import enumerations.*;
import exceptions.AdrenalinaException;
import exceptions.game.InvalidGameStateException;
import exceptions.game.InvalidKillshotNumberException;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.cards.PowerupCard;
import model.player.KillShot;
import model.player.Player;
import model.player.PlayerBoard;
import model.player.Terminator;
import model.player.UserPlayer;
import network.message.*;
import network.server.Server;
import utility.LobbyTimer;
import utility.TimerRunListener;

import java.util.*;
import java.util.stream.Collectors;


/**
 * This Class is the Controller that receives Messages, validates them and moves the Game State to make the game evolve
 */
public class GameManager implements TimerRunListener {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 5;

    private final Server server;
    private PossibleGameState gameState;
    private final Game gameInstance;
    private GameLobby lobby;
    private RoundManager roundManager;
    private ShootParameters shootParameters;

    private int lobbyTimeoutTime;
    private Timer lobbyTimer;
    private boolean lobbyTimerRunning = false;

    /**
     * Creates an instance of {@link GameManager GameManager} binding the server tha will send messages to him
     *
     * @param server the Server to be bind
     */
    public GameManager(Server server) {
        this.server = server;
        gameState = PossibleGameState.GAME_ROOM;
        this.lobby = new GameLobby();
        this.gameInstance = Game.getInstance();
        this.roundManager = new RoundManager(this);

        lobbyTimeoutTime = 10000;
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
        winners = declareWinner();
        server.sendMessageToAll(winners);

    }

    /**
     * Main method on which the class is based. It is the method that receives messages, validate them and then execute
     * changing the state of the real model
     *
     * @param receivedMessage Message received by the server from a Client that wants to act
     * @return a {@link Message Message} which contains the result of the received message
     */
    public Message onMessage(Message receivedMessage) {
        Response tempResponse;

        try {
            // on the game setup messages can be received from any player
            if (gameState == PossibleGameState.GAME_ROOM) {
                return firstStateHandler(receivedMessage);
            }

            // if the message received comes from a client that is not the turn owner it is never executed!
            if (!gameInstance.getPlayerByName(receivedMessage.getSenderUsername()).equals(roundManager.getTurnManager().getTurnOwner())) {
                return new Response("Message from a player that is not his turn!", MessageStatus.ERROR);
            }

            // SPECIAL STATES handling
            tempResponse = specialStatesHandler(receivedMessage);

            if (tempResponse.getStatus() != MessageStatus.NO_RESPONSE) {
                return tempResponse;
            } // else no special States are affected

            switch (receivedMessage.getContent()) {
                case TERMINATOR_SPAWN:
                    return terminatorSpawnCheckState(receivedMessage);
                case DISCARD_POWERUP:
                    return discardPowerupCheckState(receivedMessage);
                case TERMINATOR:
                    return terminatorCheckState(receivedMessage);
                case POWERUP:
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
                default:
                    throw new InvalidGameStateException();
            }
        } catch (ClassCastException e) {
            Server.LOGGER.severe("Invalid cast of a message from " + receivedMessage.getSenderUsername());
            return buildInvalidResponse();
        }

    }

    /**
     * This method handles both the extemporary usage of a powerup like: TAGBACK GRANADE or TARGETING SCOPE and the
     * Respawn actions of the {@link Terminator Terminator} and {@link UserPlayer UserPlayer}
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
     * Method used to oblige a player to use the {@link model.actions.TerminatorAction TerminatorAction} in case he hasn't
     * already performed it
     *
     * @param receivedMessage the {@link Message Message} received that can be both: {@link UseTerminatorRequest UseTerminatorRequest}
     *                        or a {@link PowerupRequest PowerupRequest}
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response handleTerminatorAsLastAction(Message receivedMessage) {
        // only messages to use the terminator action and a powerup can be used!
        switch (receivedMessage.getContent()) {
            case TERMINATOR:
                return roundManager.handleTerminatorAction((UseTerminatorRequest) receivedMessage, PossibleGameState.MISSING_TERMINATOR_ACTION);
            case POWERUP:
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
        if (receivedMessage.getContent() == MessageContent.POWERUP) {
            return onGranadeMessage(receivedMessage);
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
        if (receivedMessage.getContent() == MessageContent.POWERUP) {
            if (!((PowerupRequest) receivedMessage).getPowerup().isEmpty()) {
                return roundManager.handleShootAction(shootParameters.shootRequest, (PowerupRequest) receivedMessage, shootParameters.secondAction);
            } else {
                return roundManager.handleShootAction(shootParameters.shootRequest, null, shootParameters.secondAction);
            }
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and execute the methods related to the Respawn of the {@link Terminator Terminator}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response checkTerminatorRespawn(Message receivedMessage) {
        Response tempResponse;
        if (receivedMessage.getContent() == MessageContent.TERMINATOR_SPAWN) {
            tempResponse = roundManager.handleTerminatorRespawn((TerminatorSpawnRequest) receivedMessage);
            if (tempResponse.getStatus() == MessageStatus.OK) {
                // if the Respawn message is validated I can distribute the points of the terminator's playerboard, move the skull from the tracker and then reset his playerboard
                distributePoints(gameInstance.getTerminator());
                moveSkull(gameInstance.getTerminator());
                gameInstance.getTerminator().getPlayerBoard().onDeath();

                // if the state changed to FINAL_FRENZY, no other players died and the FRENZY MODE starts
                if (gameState == PossibleGameState.FINAL_FRENZY) {
                    gameInstance.setState(GameState.FINAL_FRENZY);
                    finalFrenzySetup();
                }
                return tempResponse;
            } else {
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
                UserPlayer respawnedPlayer = gameInstance.getUserPlayerByUsername(receivedMessage.getSenderUsername());
                distributePoints(respawnedPlayer);
                moveSkull(respawnedPlayer);
                respawnedPlayer.getPlayerBoard().onDeath();

                // if the state changed to FINAL_FRENZY, no other players died and the FRENZY MODE starts
                if (gameState == PossibleGameState.FINAL_FRENZY) {
                    gameInstance.setState(GameState.FINAL_FRENZY);
                    finalFrenzySetup();
                }
                return tempResponse;
            } else {
                return tempResponse;
            }
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the FirstSpawn of the {@link Terminator Terminator}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response terminatorSpawnCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_READY) {
            // remember a player must see the powerups he has drawn before spawning the terminator!
            return roundManager.handleTerminatorFirstSpawn((TerminatorSpawnRequest) receivedMessage);
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
        if (gameState == PossibleGameState.GAME_READY) {
            return roundManager.handleFirstSpawn((DiscardPowerupRequest) receivedMessage);
        } else {
            return buildInvalidResponse();
        }
    }

    /**
     * Method that checks and executes the {@link UseTerminatorRequest TerminatorRequest}
     *
     * @param receivedMessage the {@link Message Message} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response terminatorCheckState(Message receivedMessage) {
        if (gameState == PossibleGameState.GAME_READY) {
            return roundManager.handleTerminatorAction((UseTerminatorRequest) receivedMessage, gameState);
        } else if (gameState == PossibleGameState.GAME_STARTED) {
            return roundManager.handleTerminatorAction((UseTerminatorRequest) receivedMessage, gameState);
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
            return onShootMessage((ShootRequest) receivedMessage, handleSecondAction());
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
     *                        {@link MessageContent MessaGeContent.GET_IN_LOBBY} and {@link MessageContent MessageContent.GAME_SETUP}
     * @return a {@link Message} handled by the server
     */
    private Message firstStateHandler(Message receivedMessage) {
        switch (receivedMessage.getContent()) {
            case GET_IN_LOBBY:
                return lobbyMessageHandler((LobbyMessage) receivedMessage);
            case GAME_SETUP:
                return setupMessageHandler((GameSetupMessage) receivedMessage);
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
        gameInstance.setTerminator(lobby.getTerminatorPresence());

        // then I can start adding players to the game with the color specified in their Lobby Message
        for (LobbyMessage player : lobby.getInLobbyPlayers()) {
            gameInstance.addPlayer(new UserPlayer(player.getSenderUsername(), player.getChosenColor(), new PlayerBoard()));
        }

        // in the end I set the map and the number of Skulls chosen
        try {
            gameInstance.setGameMap(lobby.getFavouriteMap());
        } catch (InvalidMapNumberException e) {
            // never reached here the lobby returns always a valid number
        }

        try {
            gameInstance.setKillShotNum(lobby.getFavouriteSkullsNum());
        } catch (InvalidKillshotNumberException e) {
            // never reached here the lobby returns always a valid number
        }

        // at this point gme should always be ready to start
        if (gameInstance.isGameReadyToStart()) {
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
        changeState(PossibleGameState.GAME_READY);

        UserPlayer firstPlayer = roundManager.getTurnManager().getTurnOwner();

        // if the game has the terminator I set the first player state depending on the presence of the terminator
        if (gameInstance.isTerminatorPresent()) {
            firstPlayer.changePlayerState(PossiblePlayerState.SPAWN_TERMINATOR);
        } // else the state remains first spawn and it's ok to start

        // I first need to pick the two powerups for the first player playing
        roundManager.pickTwoPowerups();

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
        if (lobbyMessage.getContent() == MessageContent.GET_IN_LOBBY && !inLobbyPlayers.contains(lobbyMessage)) {
            if ((lobby.getTerminatorPresence() && inLobbyPlayers.size() < 4) ||
                    (!lobby.getTerminatorPresence() && inLobbyPlayers.size() < 5) &&
                            lobbyMessage.getChosenColor() != null && unusedColors.contains(lobbyMessage.getChosenColor())) {
                inLobbyPlayers.add(lobbyMessage);
                timerCheck();
            } else {
                return buildInvalidResponse();
            }
        } else if (lobbyMessage.getContent() == MessageContent.DISCONNECTION && inLobbyPlayers.contains(lobbyMessage)) {
            inLobbyPlayers.remove(lobbyMessage);
            removeVote(lobbyMessage.getSenderUsername());
            timerCheck();
            return null;
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
            }
        } else {
            if (inLobbyPlayers.size() >= MIN_PLAYERS) {
                lobbyTimer = new Timer();
                lobbyTimer.schedule(new LobbyTimer(this), lobbyTimeoutTime);
                Server.LOGGER.info("Lobby timer started (" + lobbyTimeoutTime / 1000 + " s)");
                lobbyTimerRunning = true;
            }
        }

    }

    @Override
    public void onTimerRun() {
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

    /**
     * Method used to remove a vote from the lobby when a player disconnects from the game
     *
     * @param disconnectedPlayer String containing the UserName of the disconnected player
     */
    private void removeVote(String disconnectedPlayer) {
        ArrayList<GameSetupMessage> playersVotes = lobby.getVotedPlayers();

        for (GameSetupMessage setupMessage : playersVotes) {
            if (setupMessage.getSenderUsername().equals(disconnectedPlayer)) {
                playersVotes.remove(setupMessage);
            }
        }
    }

    /**
     * Method that handles the reception of a {@link GameSetupMessage GameSetupMessage} containing informations about the
     * votes of the parameters the Sender wants to play with
     *
     * @param setupMessage the {@link GameSetupMessage GameSetupMessage} received
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response setupMessageHandler(GameSetupMessage setupMessage) {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();
        ArrayList<GameSetupMessage> alreadyVotedPlayers = lobby.getVotedPlayers();

        for (LobbyMessage lobbyPlayer : inLobbyPlayers) {
            if (lobbyPlayer.getSenderUsername().equals(setupMessage.getSenderUsername()) && !alreadyVotedPlayers.contains(setupMessage)) {
                alreadyVotedPlayers.add(setupMessage);
                return new Response("Vote added", MessageStatus.OK);
            }
        }

        return new Response("Vote NOT added player already voted!", MessageStatus.ERROR);
    }

    /**
     * Method used to handle the decision of a player to use or not a TAGBACK GRANADE when damaged.
     *
     * @param receivedMessage the {@link Message Message} received that can be handled if a {@link PowerupRequest PowerupRequest}
     *                        or a {@link PassTurnRequest PassTurnRequest}
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response onGranadeMessage(Message receivedMessage) {
        switch (receivedMessage.getContent()) {
            case POWERUP:
                return roundManager.handleGranadeUsage((PowerupRequest) receivedMessage);
            case PASS_TURN:     // this is a "false" PASS_TURN, turn goes to the next damaged player by the "real" turnOwner
                // implementation then goes directly here
                roundManager.getTurnManager().increaseCount();
                roundManager.getTurnManager().giveTurn(roundManager.getTurnManager().getDamagedPlayers().get(roundManager.getTurnManager().getTurnCount()));
                return new Response("Granade not used", MessageStatus.OK);
            default:
                return new Response("Invalid Message while in granade state", MessageStatus.ERROR);
        }
    }

    /**
     * Method that handles the reception of a {@link ShootRequest ShootRequest} setting needed parameters if the shooter
     * later decides to use a TARGETING SCOPE with his weapon
     *
     * @param shootRequest the {@link ShootRequest ShootRequest} received
     * @param secondAction Boolean that specifies if the performing action is the second
     * @return a positive or negative {@link Response Response} handled by the server
     */
    private Response onShootMessage(ShootRequest shootRequest, boolean secondAction) {
        shootParameters = null;
        PowerupCard[] ownersPowerups = roundManager.getTurnManager().getTurnOwner().getPowerups();

        for (PowerupCard powerupCard : ownersPowerups) {
            if (powerupCard.getName().equals("TARGETING SCOPE")) {
                shootParameters = new ShootParameters(shootRequest, secondAction);
                changeState(PossibleGameState.SCOPE_USAGE);
                return new Response("Shoot Action can have SCOPE usage", MessageStatus.OK);
            }
        }

        // if turnOwner has no SCOPEs the shoot action is handled normally
        return roundManager.handleShootAction(shootRequest, null, secondAction);
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
                return roundManager.getTurnManager().getAfterFrenzy().contains(roundManager.getTurnManager().getTurnOwner());
            default:
                throw new InvalidGameStateException();
        }
    }

    /**
     * Method that flips all the PlayerBoards in the FinalFrenzy if needed to all {@link UserPlayer UserPlayers} and also
     * {@link Terminator Terminator}
     */
    private void finalFrenzySetup() {
        // boards flipping setup
        if (gameInstance.isTerminatorPresent() && gameInstance.getTerminator().getPlayerBoard().getDamageCount() == 0) {
            try {
                gameInstance.getTerminator().getPlayerBoard().flipBoard();
            } catch (AdrenalinaException e) {
                // exceptions thrown can never be reached thanks to the control done
            }
        }

        for (UserPlayer player : gameInstance.getPlayers()) {
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

        if (deathsPlayerBoard.isBoardFlipped()) {     // first blood assignment
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
        Terminator terminator = (Terminator) gameInstance.getTerminator();

        if (gameInstance.isTerminatorPresent() && terminator.getPlayerBoard().getDamageCount() > 0) {
            // in the last distribution each damaged player counts as a dead one to calculate points
            distributePoints(terminator);
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
        ArrayList<KillShot> killShotTracker = gameInstance.getKillShotTrack();
        ArrayList<KillShot> finalFrenzyTracker = gameInstance.getFinalFrenzyKillShots();
        ArrayList<String> killers = new ArrayList<>();
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
     * @param killShotTracker    ArrayList of {@link KillShot Killshots} coming from the game map
     * @param finalFrenzyTracker ArrayList of {@link KillShot Killshots} containing all the {@link KillShot Killshots} done
     *                           during the FinalFrenzy
     * @return an int that rerpesents the frequency of the Killer's {@link KillShot KillShots} on the KillShotTracker
     */
    private int getPointsOnKillShots(String killer, ArrayList<KillShot> killShotTracker, ArrayList<KillShot> finalFrenzyTracker) {
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
     * Method that declares the Winner/s of the game with a {@link WinnersResponse WinnerResponse} that will be
     * broadcasted to every player in the game
     *
     * @return a {@link WinnersResponse WinnerResponse} containing the ArrayList of {@link Player Player/s} that won the Game
     */
    private WinnersResponse declareWinner() {
        List<UserPlayer> players = gameInstance.getPlayers();
        ArrayList<Player> tiePlayers = new ArrayList<>();
        ArrayList<Player> winners = new ArrayList<>();

        ArrayList<Player> orderedPlayers = players.stream().sorted().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        int maxPoints = orderedPlayers.get(0).getPoints();
        tiePlayers.add(orderedPlayers.get(0));
        for (int i = 1; i < orderedPlayers.size(); ++i) {
            if (orderedPlayers.get(i).getPoints() == maxPoints) {
                tiePlayers.add(orderedPlayers.get(i));
            }
        }

        if (gameInstance.isTerminatorPresent()) {
            if (gameInstance.getTerminator().getPoints() > maxPoints) {
                winners.add(gameInstance.getTerminator());
                return new WinnersResponse(winners);
            } else if (gameInstance.getTerminator().getPoints() == maxPoints) {
                tiePlayers.add(gameInstance.getTerminator());
            }
        }

        if (tiePlayers.size() == 1) {
            return new WinnersResponse(tiePlayers);
        } else {
            winners = handleTiePlayers(tiePlayers);
            return new WinnersResponse(winners);
        }
    }

    /**
     * Utility method used by {@link #declareWinner() declareWinner} method to get the Winner from a List of {@link Player Players}
     * that did the same points during the Game
     *
     * @param tiePlayers ArrayList of {@link Player Players} with the same points at the end of the Game
     * @return the ArrayList of {@link Player Player/s} that won the Game
     */
    private ArrayList<Player> handleTiePlayers(ArrayList<Player> tiePlayers) {
        ArrayList<KillShot> killShotTracker = gameInstance.getKillShotTrack();
        ArrayList<Player> winner = new ArrayList<>();

        for (KillShot killShot : killShotTracker) {
            for (Player player : tiePlayers) {
                if (player.getUsername().equals(killShot.getKiller())) {
                    winner.add(player);
                    return winner;
                }
            }
        }

        return tiePlayers;
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
     * Utility Class Used to manage needed parameters when a Shooter wants to use the TARGETING SCOPE
     */
    class ShootParameters {
        ShootRequest shootRequest;
        boolean secondAction;

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