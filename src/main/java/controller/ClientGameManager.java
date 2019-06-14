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
import network.client.*;
import network.message.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class ClientGameManager implements ClientGameManagerListener, ClientUpdateListener, Runnable {
    public static final String TAGBACK_GRENADE = "TAGBACK_GRENADE";
    public static final String TELEPORTER = "TELEPORTER";
    public static final String NEWTON = "NEWTON";
    public static final String TARGETING_SCOPE = "TARGETING_SCOPE";

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

    private boolean firstTurn;
    private boolean yourTurn;

    private boolean isBotPresent;

    private boolean noChangeStateRequest; // Identify a request that doesn't have to change the player state

    public ClientGameManager() {
        firstTurn = true;
        noChangeStateRequest = false;
        turnOwnerChanged = false;

        joinedLobby = false;

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

    protected void createConnection(int connection, String username, String address, int port) throws Exception {
        if (connection == 0) {
            client = new ClientSocket(username, address, port);
        } else {
            client = new ClientRMI(username, address, port);
        }

        client.startConnection();
        startUpdater();
    }

    private void startUpdater() {
        clientUpdater = new ClientUpdater(client, this);
    }

    private void startGame() {
        roundManager = new ClientRoundManager(isBotPresent);

        if (firstTurn) { // First round
            if (firstPlayer.equals(getUsername())) { // First player to play
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
                botSpawn();
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
                adrenalinePick();
                break;

            case ADRENALINE_SHOOT:
                adrenalineShoot();
                break;

            case FRENZY_MOVE:
                frenzyMove();
                break;

            case FRENZY_PICK:
                frenzyPick();
                break;

            case FRENZY_SHOOT:
                frenzyShoot();
                break;

            case LIGHT_FRENZY_PICK:
                lightFrenzyPick();
                break;

            case LIGHT_FRENZY_SHOOT:
                lightFrenzyShoot();
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

            default:
        }
    }

    private void handleConnectionResponse(ConnectionResponse connectionResponse) {
        if (connectionResponse.getStatus().equals(MessageStatus.OK)) {
            client.setToken(connectionResponse.getNewToken());
        } else {
            clientUpdater.stop();
            clientUpdater = null;

            try {
                client.close();
            } catch (Exception e) {
                // No issues
            }
            client = null;
        }

        queue.add(() -> connectionResponse(connectionResponse));
    }

    private void handleColorResponse(ColorResponse colorResponse) {
        queue.add(() -> askColor(colorResponse.getColorList()));
    }

    private void handleVoteResponse(GameVoteResponse gameVoteResponse) {
        queue.add(() -> voteResponse(gameVoteResponse));
    }

    private void handleResponse(Response response) {
        if (!joinedLobby) {
            joinedLobby = response.getStatus().equals(MessageStatus.OK);
            queue.add(() -> lobbyJoinResponse(response));
        } else {
            if (response.getStatus().equals(MessageStatus.ERROR)) {
                queue.add(() -> responseError(response.getMessage()));
            } else {
                nextState();
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
        }
    }

    private void handleGameStateMessage(GameStateMessage gameStateMessage) {
        checkFrenzyMode(gameStateMessage);

        synchronized (gameSerializedLock) {
            gameSerialized = gameStateMessage.getGameSerialized();

            queue.add(() -> gameStateUpdate(gameSerialized));
        }

        checkTurnChange(gameStateMessage);
    }

    private void handleGameStartMessage(GameStartMessage gameStartMessage) {
        synchronized (gameSerializedLock) {
            firstPlayer = gameStartMessage.getFirstPlayer();
            turnOwner = gameStartMessage.getFirstPlayer();

            isBotPresent = gameSerialized.isBotPresent();

            queue.add(this::startGame);
        }
    }

    private void handleWinner(WinnersResponse winnerResponse) {
        synchronized (gameSerializedLock) {
            queue.add(() -> notifyGameEnd(winnerResponse.getWinners()));
        }
    }

    private void handleReconnection(ReconnectionMessage reconnectionMessage) {
        turnOwner = "";
        firstTurn = false;
        yourTurn = false;

        client.setToken(reconnectionMessage.getToken());

        synchronized (gameSerializedLock) {
            gameSerialized = reconnectionMessage.getGameStateMessage().getGameSerialized();
            isBotPresent = gameSerialized.isBotPresent();
        }
        roundManager = new ClientRoundManager(isBotPresent);

        checkFrenzyMode(reconnectionMessage.getGameStateMessage());

        roundManager.firstAction();

        synchronized (gameSerializedLock) {
            queue.add(() -> gameStateUpdate(gameSerialized));
        }

        checkTurnChange(reconnectionMessage.getGameStateMessage());
    }

    private void handleDisconnection(DisconnectionMessage disconnectionMessage) {
        queue.add(() -> onPlayerDisconnect(disconnectionMessage.getUsername()));
    }

    private void checkTurnChange(GameStateMessage stateMessage) {
        if (!firstTurn) {
            if (!stateMessage.getTurnOwner().equals(turnOwner)) {
                turnOwner = stateMessage.getTurnOwner();
                turnOwnerChanged = true;
            }

            if (!yourTurn) { // If you are not the turn owner you don't need to wait a response
                turnOwnerChanged = false;

                if (turnOwner.equals(getUsername())) {
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

    private void nextState() {
        if (noChangeStateRequest) {
            noChangeStateRequest = false;
        } else {
            roundManager.nextState();
        }
    }

    protected void reAskAction() {
        queue.add(this::makeMove);
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
     * check if all player except the current player are dead
     *
     * @return true if all player are dead (except the current player), otherwise false
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
     * Returns the final frenzy actions based on who activated the frenzy mode and the position of the player
     * in the game turn
     *
     * @return the list of possible possibleFinalFrenzyActions for {@code that} player
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

    private List<PossibleAction> getEndingActions() {
        List<PossibleAction> actions = new ArrayList<>();

        if (getPowerups().stream().anyMatch(p -> p.getName().equals(TELEPORTER) || p.getName().equals(NEWTON))) {
            actions.add(PossibleAction.POWER_UP);
        }

        if (getPlayerWeapons(getUsername()).stream().anyMatch(w -> w.status() == 1)) {
            actions.add(PossibleAction.RELOAD);
        }

        actions.add(PossibleAction.PASS_TURN);

        return actions;
    }

    protected List<Player> getPlayers() {
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

    protected List<WeaponCard> getPlayerWeapons(String username) {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPlayerWeapons(username);
        }
    }

    public UserPlayer getPlayer() {
        return (UserPlayer) getPlayerByName(getUsername());
    }

    protected Player getPlayerByName(String username) {
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

    public String getFirstPlayer() {
        return firstPlayer;
    }

    protected boolean sendRequest(Message message) {
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

    private void checkChangeStateRequest(Message message) {
        noChangeStateRequest = (roundManager.getUserPlayerState() != UserPlayerState.BOT_ACTION && message.getContent() == MessageContent.BOT_ACTION) ||
                message.getContent() == MessageContent.POWERUP_USAGE;
    }

    protected String getClientToken() {
        return client.getToken();
    }

    protected String getUsername() {
        return client.getUsername();
    }
}
