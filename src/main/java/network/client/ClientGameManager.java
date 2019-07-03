package network.client;

import enumerations.*;
import exceptions.player.ClientRoundManagerException;
import exceptions.player.PlayerNotFoundException;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.GameMap;
import model.player.Player;
import model.player.UserPlayer;
import network.message.*;
import utility.GameCostants;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Handles the game advancement and listen for the reception of messages
 */
public abstract class ClientGameManager implements ClientGameManagerListener, ClientUpdateListener, Runnable {
    public static final Logger LOGGER = Logger.getLogger("adrenaline_client");

    public static final String SEND_ERROR = "Error while sending the request";
    public static final String ERROR_DIALOG_TITLE = "Error";
    protected static final String INVALID_STRING = "Invalid String!";

    public static final String TELEPORTER = "TELEPORTER";
    public static final String NEWTON = "NEWTON";
    public static final String TAGBACK_GRENADE = "TAGBACK GRENADE";
    public static final String TARGETING_SCOPE = "TARGETING SCOPE";

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Object gameSerializedLock = new Object(); // handles GameSerialized parallelism

    private Client client;
    private boolean joinedLobby;

    private ClientRoundManager roundManager; // manage the rounds of this client
    private GameSerialized gameSerialized;
    private ClientUpdater clientUpdater;

    private String firstPlayer;
    private String turnOwner;
    private boolean turnOwnerChanged;
    private boolean waitingGrenade = false;

    private boolean firstTurn;
    private boolean yourTurn;

    private boolean isBotPresent;
    private boolean botRequest;

    private boolean noChangeStateRequest; // Identify a request that doesn't have to change the player state

    private boolean gameEnded = false;

    public ClientGameManager() {
        firstTurn = true;
        noChangeStateRequest = false;
        turnOwnerChanged = false;

        joinedLobby = false;

        Date date = GregorianCalendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-mm_HH.mm.ss");

        try {
            FileHandler fh = new FileHandler("log/client-" + dateFormat.format(date) + ".log");
            fh.setFormatter(new SimpleFormatter());
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                queue.take().run();
            } catch (InterruptedException e) {
                LOGGER.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void onUpdate(Message message) {
        if (gameEnded) {
            return;
        }

        switch (message.getContent()) {
            case PLAYERS_IN_LOBBY:
                handlePlayersInLobby((LobbyPlayersResponse) message);
                break;

            case CONNECTION_RESPONSE:
                handleConnectionResponse((ConnectionResponse) message);
                break;

            case COLOR_RESPONSE:
                handleColorResponse((ColorResponse) message);
                break;

            case VOTE_RESPONSE:
                handleVoteResponse((GameVoteResponse) message);
                break;

            case RESPONSE:
                handleResponse((Response) message);
                break;

            case GAME_STATE:
                handleGameStateMessage((GameStateMessage) message);
                break;

            case READY:
                handleGameStartMessage((GameStartMessage) message);
                break;

            case WINNER:
                handleWinner((WinnersResponse) message);
                break;

            case RECONNECTION:
                handleReconnection((ReconnectionMessage) message);
                break;

            case DISCONNECTION:
                handleDisconnection((DisconnectionMessage) message);
                break;

            case GAME_LOAD:
                handleGameLoad((GameLoadResponse) message);
                break;

            default:
        }

        LOGGER.log(Level.INFO, "Received: {0}", message);
    }

    /**
     * Creates a connection with the server based on user input data
     *
     * @param connection type of connection (0 for Socket, 1 for RMI)
     * @param username   username of the player
     * @param address    address of the server
     * @param port       port of the serve
     * @throws Exception if something goes wrong with the creation of the connection
     */
    public void createConnection(int connection, String username, String address, int port, DisconnectionListener disconnectionListener) throws Exception {
        if (connection == 0) {
            client = new ClientSocket(username, address, port, disconnectionListener);
        } else {
            client = new ClientRMI(username, address, port, disconnectionListener);
        }

        client.startConnection();
        startUpdater();
    }

    /**
     * Closes the connection with the server
     */
    public void closeConnection() {
        if (clientUpdater != null) {
            clientUpdater.stop();
            clientUpdater = null;
        }

        try {
            client.close();
        } catch (Exception e) {
            // No issues
        }
        client = null;
    }

    /**
     * Starts the updater for listening the reception of messages
     */
    private void startUpdater() {
        clientUpdater = new ClientUpdater(client, this);
    }

    /**
     * Called when the game starts. It setups the state game machine
     */
    private void startGame() {
        roundManager = new ClientRoundManager(isBotPresent);

        if (firstTurn) { // First round
            if (firstPlayer.equals(getUsername())) { // First player to play
                yourTurn = true;

                if (isBotPresent) {
                    roundManager.botSpawn();
                    roundManager.setBotFirstTurn();
                }
            }

            queue.add(() -> firstPlayerCommunication(firstPlayer));
            firstTurn = false;
        }

        newTurn();
    }

    /**
     * Called when a change of turn owner happen
     */
    private void newTurn() {
        if (yourTurn) {
            roundManager.beginRound();
            makeMove();
        } else {
            queue.add(() -> notYourTurn(turnOwner));
        }
    }

    /**
     * Show the client all the possible actions
     */
    protected void makeMove() {
        if (getUsername().equals(turnOwner)) {
            queue.add(() -> displayActions(getPossibleActions()));
        }
    }

    /**
     * Executes the action chosen
     *
     * @param chosenAction action chosen by the user
     */
    public void doAction(PossibleAction chosenAction) {
        Runnable action;

        switch (chosenAction) {
            case SPAWN_BOT:
                action = this::botSpawn;
                break;

            case RESPAWN_BOT:
                action = this::botRespawn;
                break;

            case CHOOSE_SPAWN:
            case CHOOSE_RESPAWN:
                action = this::spawn;
                break;

            case POWER_UP:
                action = this::powerup;
                break;

            case GRENADE_USAGE:
                action = this::tagbackGrenade;
                break;

            case SCOPE_USAGE:
                action = this::targetingScope;
                break;

            case MOVE:
                action = this::move;
                break;

            case MOVE_AND_PICK:
                action = this::moveAndPick;
                break;

            case SHOOT:
                action = this::shoot;
                break;

            case ADRENALINE_PICK:
                action = this::adrenalinePick;
                break;

            case ADRENALINE_SHOOT:
                action = this::adrenalineShoot;
                break;

            case FRENZY_MOVE:
                action = this::frenzyMove;
                break;

            case FRENZY_PICK:
                action = this::frenzyPick;
                break;

            case FRENZY_SHOOT:
                action = this::frenzyShoot;
                break;

            case LIGHT_FRENZY_PICK:
                action = this::lightFrenzyPick;
                break;

            case LIGHT_FRENZY_SHOOT:
                action = this::lightFrenzyShoot;
                break;

            case BOT_ACTION:
                action = this::botAction;
                break;

            case RELOAD:
                action = this::reload;
                break;

            case PASS_TURN:
                action = this::passTurn;
                break;

            default:
                throw new ClientRoundManagerException("Invalid Action");
        }

        queue.add(action);
    }

    /**
     * Sends a request to the server
     *
     * @param message message to send to the server
     * @return {@code true} if the message was sent successfully, {@code false} otherwise
     */
    public boolean sendRequest(Message message) {
        if (roundManager != null) {
            checkChangeStateRequest(message);
        }

        try {
            client.sendMessage(message);
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Checks if the message have to change the state of the machine in case of positive response
     *
     * @param message message to send to the server
     */
    private void checkChangeStateRequest(Message message) {
        noChangeStateRequest = (roundManager.getUserPlayerState() != UserPlayerState.BOT_ACTION && message.getContent() == MessageContent.BOT_ACTION) ||
                (message.getContent() == MessageContent.POWERUP_USAGE && !((PowerupRequest) message).getPowerup().isEmpty() &&
                        (getPowerups().get(((PowerupRequest) message).getPowerup().get(0)).getName().equals(TELEPORTER) ||
                                getPowerups().get(((PowerupRequest) message).getPowerup().get(0)).getName().equals(NEWTON)));

        botRequest = message.getContent() == MessageContent.BOT_ACTION;
    }


    /**
     * Handles the response to the server connection
     *
     * @param connectionResponse response received
     */
    private void handleConnectionResponse(ConnectionResponse connectionResponse) {
        if (connectionResponse.getStatus().equals(MessageStatus.OK)) {
            client.setToken(connectionResponse.getNewToken());
        } else {
            closeConnection();
        }

        queue.add(() -> connectionResponse(connectionResponse));
    }

    /**
     * Handles the response to the color choose request
     *
     * @param colorResponse response received
     */
    private void handleColorResponse(ColorResponse colorResponse) {
        queue.add(() -> askColor(colorResponse.getColorList()));
    }

    /**
     * Handles the vote response
     *
     * @param gameVoteResponse response received
     */
    private void handleVoteResponse(GameVoteResponse gameVoteResponse) {
        queue.add(() -> voteResponse(gameVoteResponse));
    }

    /**
     * Handles the message of a new player in lobby
     *
     * @param message message received
     */
    private void handlePlayersInLobby(LobbyPlayersResponse message) {
        queue.add(() -> playersLobbyUpdate(message.getUsers()));
    }

    /**
     * Handles the generic response after a action request
     *
     * @param response response received
     */
    private void handleResponse(Response response) {
        if (!joinedLobby) {
            joinedLobby = response.getStatus() == MessageStatus.OK;
            queue.add(() -> lobbyJoinResponse(response));
        } else {
            if (response.getStatus() == MessageStatus.ERROR) {
                queue.add(() -> responseError(response.getMessage()));
            } else {
                onPositiveResponse(response);
            }

            if (!waitingGrenade) {
                if (roundManager.getUserPlayerState() != UserPlayerState.END) {
                    makeMove();
                } else {
                    roundManager.endRound();
                }
            } else {
                waitingGrenade = false;
            }

            if (yourTurn && turnOwnerChanged) { // Use to wait the response before calling newTurn()
                turnOwnerChanged = false;
                yourTurn = false;

                newTurn();
            }
        }
    }

    /**
     * Handles the update of the game state
     *
     * @param gameStateMessage game state update received
     */
    private void handleGameStateMessage(GameStateMessage gameStateMessage) {
        checkFrenzyMode(gameStateMessage);

        synchronized (gameSerializedLock) {
            gameSerialized = gameStateMessage.getGameSerialized();
        }

        queue.add(this::gameStateUpdate);

        checkTurnChange(gameStateMessage);
    }

    /**
     * Handles the game start message
     *
     * @param gameStartMessage start message received
     */
    private void handleGameStartMessage(GameStartMessage gameStartMessage) {
        synchronized (gameSerializedLock) {
            firstPlayer = gameStartMessage.getFirstPlayer();
            turnOwner = gameStartMessage.getFirstPlayer();

            isBotPresent = gameSerialized.isBotPresent();

            startGame();
        }
    }

    /**
     * Handles the message of end game
     *
     * @param winnerResponse winners message received
     */
    private void handleWinner(WinnersResponse winnerResponse) {
        gameEnded = true;

        synchronized (gameSerializedLock) {
            queue.add(() -> notifyGameEnd(winnerResponse.getWinners()));
        }
    }

    /**
     * Handles a reconnection message
     *
     * @param reconnectionMessage reconnection message received
     */
    private void handleReconnection(ReconnectionMessage reconnectionMessage) {
        turnOwner = "";
        firstTurn = false;
        yourTurn = false;
        joinedLobby = true;

        client.setToken(reconnectionMessage.getNewToken());

        synchronized (gameSerializedLock) {
            gameSerialized = reconnectionMessage.getGameStateMessage().getGameSerialized();
            isBotPresent = gameSerialized.isBotPresent();
        }
        roundManager = new ClientRoundManager(isBotPresent);

        checkFrenzyMode(reconnectionMessage.getGameStateMessage());

        if (getPlayer().getPosition() != null) {
            roundManager.reconnection();
        }

        turnOwner = reconnectionMessage.getGameStateMessage().getTurnOwner();

        synchronized (gameSerializedLock) {
            queue.add(this::gameStateUpdate);
        }

        checkTurnChange(reconnectionMessage.getGameStateMessage());
    }

    /**
     * Handles a disconnection message
     *
     * @param disconnectionMessage disconnection message received
     */
    private void handleDisconnection(DisconnectionMessage disconnectionMessage) {
        queue.add(() -> onPlayerDisconnect(disconnectionMessage.getUsername()));
    }

    /**
     * Handles game load message
     *
     * @param gameLoadResponse game load message received
     */
    private void handleGameLoad(GameLoadResponse gameLoadResponse) {
        turnOwner = "";
        firstTurn = false;
        yourTurn = false;
        joinedLobby = true;

        client.setToken(gameLoadResponse.getNewToken());

        roundManager = new ClientRoundManager(gameLoadResponse.isBotPresent());

        roundManager.setPlayerState(gameLoadResponse.getUserPlayerState());

        isBotPresent = gameLoadResponse.isBotPresent();

        queue.add(this::loadResponse);
    }

    /**
     * Changes next state of the game machine when a positive response is received
     *
     * @param response response received
     */
    private void onPositiveResponse(Response response) {
        if (botRequest) {
            botRequest = false;
            roundManager.setBotMoved();
        }

        if (response.getStatus() == MessageStatus.NEED_PLAYER_ACTION) {
            roundManager.targetingScope();
        } else if (roundManager.getUserPlayerState() == UserPlayerState.ENDING_PHASE &&
                isBotPresent && gameSerialized.getBot().isDead()) {
            roundManager.botRespawn();
        } else {
            nextState();
        }
    }

    /**
     * Checks if the turn owner is changed
     *
     * @param stateMessage game state message received
     */
    private void checkTurnChange(GameStateMessage stateMessage) {
        if (!firstTurn) {
            if (!stateMessage.getTurnOwner().equals(turnOwner)) {
                turnOwner = stateMessage.getTurnOwner();
                turnOwnerChanged = true;

                if (yourTurn) {
                    waitingGrenade = stateMessage.isGrenadeUsage();
                }
            }

            if (!yourTurn) { // If you are not the turn owner you don't need to wait a response
                turnOwnerChanged = false;

                if (turnOwner.equals(getUsername())) {
                    yourTurn = true;
                    checkSpecialState(stateMessage);
                }

                newTurn();
            }
        }
    }

    /**
     * Changes next state of the game machine when a special action needs to be done
     *
     * @param gameStateMessage game state message received
     */
    private void checkSpecialState(GameStateMessage gameStateMessage) {
        if (gameStateMessage.isGrenadeUsage()) {
            roundManager.grenade();
        } else if (getPlayer().isDead()) {
            roundManager.death();
        }
    }

    /**
     * Checks if frenzy mode is started and acts consequently
     *
     * @param stateMessage game state message received
     */
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

    /**
     * Handles the change of the current state to the next state
     */
    private void nextState() {
        if (noChangeStateRequest) { // In case of action that don't have to change the state of the game machine (e.g. powerup usage)
            noChangeStateRequest = false;
        } else {
            roundManager.nextState();
        }
    }

    /**
     * @return a list of possible actions based on the current state of the player
     */
    private List<PossibleAction> getPossibleActions() {
        switch (roundManager.getUserPlayerState()) {
            case BOT_SPAWN:
                return List.of(PossibleAction.SPAWN_BOT);

            case BOT_RESPAWN:
                return List.of(PossibleAction.RESPAWN_BOT);

            case SPAWN:
                return List.of(PossibleAction.CHOOSE_SPAWN);

            case FIRST_ACTION:
            case SECOND_ACTION:
            case FIRST_FRENZY_ACTION:
            case SECOND_FRENZY_ACTION:
                return getGameActions();

            case FIRST_SCOPE_USAGE:
            case SECOND_SCOPE_USAGE:
                return List.of(PossibleAction.SCOPE_USAGE);

            case BOT_ACTION:
                return List.of(PossibleAction.BOT_ACTION);

            case GRENADE_USAGE:
                return List.of(PossibleAction.GRENADE_USAGE);

            case ENDING_PHASE:
                return getEndingActions();

            case DEAD:
                return List.of(PossibleAction.CHOOSE_RESPAWN);

            default:
                throw new ClientRoundManagerException("Cannot be here: " + roundManager.getUserPlayerState().name());
        }
    }

    /**
     * Based on the game state returns a list of the normal game possible actions
     *
     * @return a list of the normal game possible actions
     */
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

        if (roundManager.isBotPresent() && roundManager.isBotCanMove() && !roundManager.hasBotMoved()) {
            actions.add(PossibleAction.BOT_ACTION);
        }

        return actions;
    }

    /**
     * Based on the state of the playerboard returns a list of the base three action of the game
     *
     * @return a list of the base three action of the game (MOVE, MOVE and PICK and SHOOT)
     */
    private List<PossibleAction> possibleActions() {
        List<PossibleAction> actions = new ArrayList<>();
        PlayerBoardState boardState = getPlayer().getPlayerBoard().getBoardState();

        actions.add(PossibleAction.MOVE);

        switch (boardState) {
            case NORMAL:
                actions.add(PossibleAction.MOVE_AND_PICK);

                if (!getPlayerWeapons(getUsername()).isEmpty() && !allDead()) {
                    actions.add(PossibleAction.SHOOT);
                }
                break;

            case FIRST_ADRENALINE:
                actions.add(PossibleAction.ADRENALINE_PICK);

                if (!getPlayerWeapons(getUsername()).isEmpty() && !allDead()) {
                    actions.add(PossibleAction.SHOOT);
                }
                break;

            case SECOND_ADRENALINE:
                actions.add(PossibleAction.ADRENALINE_PICK);

                if (!getPlayerWeapons(getUsername()).isEmpty() && !allDead()) {
                    actions.add(PossibleAction.ADRENALINE_SHOOT);
                }
                break;
        }

        return actions;
    }

    /**
     * Returns the final frenzy actions based on who activated the frenzy mode
     *
     * @return the list of possible possible FinalFrenzy Actions
     */
    private List<PossibleAction> possibleFinalFrenzyActions() {
        List<PossibleAction> actions = new ArrayList<>();

        if (roundManager.isDoubleActionFrenzy()) {
            actions.add(PossibleAction.FRENZY_MOVE);
            if (!allDead()) actions.add(PossibleAction.FRENZY_SHOOT);
            actions.add(PossibleAction.FRENZY_PICK);
        } else {
            if (!allDead()) actions.add(PossibleAction.LIGHT_FRENZY_SHOOT);
            actions.add(PossibleAction.LIGHT_FRENZY_PICK);
        }

        return actions;
    }

    /**
     * Returns the action of the ending turn phase
     *
     * @return the list of possible actions of the end of the turn
     */
    private List<PossibleAction> getEndingActions() {
        List<PossibleAction> actions = new ArrayList<>();

        if (getPowerups().stream().anyMatch(p -> p.getName().equals(TELEPORTER) || p.getName().equals(NEWTON))) {
            actions.add(PossibleAction.POWER_UP);
        }

        if (roundManager.getGameClientState() == GameClientState.NORMAL && getPlayerWeapons(getUsername()).stream().anyMatch(w -> w.status() == 1)) {
            actions.add(PossibleAction.RELOAD);
        }

        actions.add(PossibleAction.PASS_TURN);

        return actions;
    }

    /**
     * Checks if all player except the current player are dead
     *
     * @return {@code true} if all player are dead (except the current player), {@code false} otherwise
     */
    private boolean allDead() {
        boolean noOnePlayer;

        int deadPlayers = 0;

        for (Player player : getPlayers()) {
            if (player.isDead() || player.getPosition() == null) deadPlayers++;
        }

        noOnePlayer = deadPlayers == getPlayers().size() - 1;

        return noOnePlayer;
    }

    /**
     * @return a list of all the players
     */
    public List<UserPlayer> getPlayers() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPlayers();
        }
    }

    /**
     * @return a list of all the players plus the bot if present
     */
    public List<Player> getPlayersWithBot() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getAllPlayers();
        }
    }

    /**
     * @return the game state object
     */
    public GameSerialized getGameSerialized() {
        synchronized (gameSerializedLock) {
            return gameSerialized;
        }
    }

    /**
     * @return the list of player's powerups
     */
    public List<PowerupCard> getPowerups() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPowerups();
        }
    }

    public PowerupCard getSpawnPowerup() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getSpawnPowerup();
        }
    }

    /**
     * Returns the weapon cards of the player passed
     *
     * @param username username of the interested player
     * @return a list of weapon cards of the player
     */
    private List<WeaponCard> getPlayerWeapons(String username) {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPlayerWeapons(username);
        }
    }

    public GameMap getGameMap() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getGameMap();
        }
    }


    /**
     * @return the player of the client
     */
    public UserPlayer getPlayer() {
        return (UserPlayer) getPlayerByName(getUsername());
    }

    /**
     * Returns a player based on the provided username
     *
     * @param username username of the interested player
     * @return the player requested
     */
    public Player getPlayerByName(String username) {
        synchronized (gameSerializedLock) {
            Player player;

            if (username.equalsIgnoreCase(GameCostants.BOT_NAME)) {
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

    /**
     * @return the game client state
     */
    public GameClientState getGameClientState() {
        return roundManager.getGameClientState();
    }

    /**
     * @return the client username
     */
    public String getUsername() {
        return client.getUsername();
    }

    /**
     * @return the client toke
     */
    public String getClientToken() {
        return client.getToken();
    }

    /**
     * @return the turn owner
     */
    public String getTurnOwner() {
        return turnOwner;
    }

    /**
     * @return the username of the first player of the game
     */
    public String getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * stop the ping timer to avoid the disconnection after an invalid lobbyResponse
     */
    public void stopPingTimer() {
        client.pingTimer.cancel();
    }
}
