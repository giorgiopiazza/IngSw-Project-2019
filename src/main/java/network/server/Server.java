package network.server;

import com.google.gson.JsonObject;
import controller.GameManager;
import enumerations.MessageContent;
import enumerations.MessageStatus;
import enumerations.PossibleGameState;
import enumerations.UserPlayerState;
import model.Game;
import model.player.UserPlayer;
import network.message.*;
import utility.ConfigurationParser;
import utility.MoveTimer;
import utility.persistency.SaveGame;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * This class is the main server class which starts a Socket and a RMI server.
 * It handles all the client regardless of whether they are Sockets or RMI
 */
public class Server implements Runnable {
    private final int socketPort;
    private final int rmiPort;

    private static final String[] FORBIDDEN_USERNAME = {Game.GOD, Game.BOT};
    private static final String DEFAULT_CONF_FILE_PATH = "conf.json";

    private Map<String, Connection> clients;

    private final GameManager gameManager;
    private boolean waitForLoad;

    public static final Logger LOGGER = Logger.getLogger("Server");

    private int startTime;
    private int moveTime;

    private Timer moveTimer;

    private Server(String confFilePath) {
        try {
            FileHandler fh = new FileHandler("server.log");
            fh.setFormatter(new SimpleFormatter());

            LOGGER.addHandler(fh);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        clients = new HashMap<>();
        waitForLoad = true;

        JsonObject jo = ConfigurationParser.parseConfiguration(confFilePath);

        if (jo == null) {
            socketPort = 0;
            gameManager = null;
            rmiPort = 0;
            LOGGER.log(Level.SEVERE, "Configuration file not found: {0}", confFilePath);
            return;
        }

        startTime = jo.get("start_time").getAsInt();
        moveTime = jo.get("move_time").getAsInt() * 1000;
        socketPort = jo.get("socket_port").getAsInt();
        rmiPort = jo.get("rmi_port").getAsInt();

        LOGGER.log(Level.INFO, "Start time : {0}", startTime);
        LOGGER.log(Level.INFO, "Move time : {0}", moveTime / 1000);
        LOGGER.log(Level.INFO, "Socket port : {0}", socketPort);
        LOGGER.log(Level.INFO, "Rmi port : {0}", rmiPort);

        SocketServer serverSocket = new SocketServer(this, socketPort);
        serverSocket.startServer();

        LOGGER.info("Socket Server Started");

        RMIServer rmiServer = new RMIServer(this, rmiPort);
        rmiServer.startServer();

        LOGGER.info("RMI Server Started");

        gameManager = SaveGame.loadGame(this, startTime);
        reserveSlots(gameManager.getGameInstance().getPlayers());

        Thread pingThread = new Thread(this);
        pingThread.start();

        moveTimer = new Timer();
    }

    public Server(boolean terminator, int skullNum, String confFilePath) {
        clients = new HashMap<>();
        waitForLoad = false;

        JsonObject jo = ConfigurationParser.parseConfiguration(confFilePath);

        if (jo == null) {
            rmiPort = 0;
            gameManager = null;
            socketPort = 0;
            LOGGER.log(Level.SEVERE, "Configuration file not found: {0}", confFilePath);
            return;
        }

        startTime = jo.get("start_time").getAsInt();
        moveTime = jo.get("move_time").getAsInt() * 1000;
        socketPort = jo.get("socket_port").getAsInt();
        rmiPort = jo.get("rmi_port").getAsInt();

        LOGGER.log(Level.INFO, "Start time : {0}", startTime);
        LOGGER.log(Level.INFO, "Move time : {0}", moveTime / 1000);
        LOGGER.log(Level.INFO, "Socket port : {0}", socketPort);
        LOGGER.log(Level.INFO, "Rmi port : {0}", rmiPort);

        SocketServer serverSocket = new SocketServer(this, socketPort);
        serverSocket.startServer();

        LOGGER.info("Socket Server Started");

        RMIServer rmiServer = new RMIServer(this, rmiPort);
        rmiServer.startServer();

        LOGGER.info("RMI Server Started");

        gameManager = new GameManager(this, terminator, skullNum, startTime);

        Thread pingThread = new Thread(this);
        pingThread.start();

        moveTimer = new Timer();
    }

    /**
     * Reserves server slots for player loaded from the game save
     *
     * @param loadedPlayers from the game save
     */
    private void reserveSlots(List<UserPlayer> loadedPlayers) {
        for (UserPlayer player : loadedPlayers) {
            clients.put(player.getUsername(), null);
        }
    }

    public static void main(String[] args) {
        String confFilePath = DEFAULT_CONF_FILE_PATH;
        boolean terminator = false;
        int skullNum = 5;
        boolean reloadGame = false;

        // normal complete Server launch should have the following parameters: -l "confFilePath.txt" -b true/false -s #skulls
        // normal complete Server launch with game Reload should have the following parameter: -l "confFilePath.txt" -r

        if (args.length > 0 && args.length < 7) {
            int i = 0;
            while (i < args.length) {
                if (args[i].charAt(0) == '-' && args[i].length() == 2 && args.length >= i + 1 && args[i + 1].charAt(0) != '-') {
                    switch (args[i].charAt(1)) {
                        case 'l':
                            confFilePath = args[i + 1];
                            ++i;
                            break;
                        case 'b':
                            terminator = Boolean.parseBoolean(args[i + 1]);
                            ++i;
                            break;
                        case 's':
                            skullNum = Integer.parseInt(args[i + 1]);
                            ++i;
                            break;
                        case 'r':
                            reloadGame = true;
                            break;
                        default:
                            break;
                    }
                }

                ++i;
            }
        }

        // if the starting command contains -r it means that a game is going to be reloaded
        if (reloadGame) {
            new Server(confFilePath);
            return;
        }

        // if the passed value is correct it is used for the game, if not DEFAULT value is set back to 5
        if (skullNum < 5 || skullNum > 8) {
            skullNum = 5;
        }

        new Server(terminator, skullNum, confFilePath);
    }

    /**
     * Adds or reconnects a player to the server
     *
     * @param username   username of the player
     * @param connection connection of the client
     */
    void login(String username, Connection connection) {
        try {
            if (clients.containsKey(username)) {
                knownPlayerLogin(username, connection);
            } else {
                newPlayerLogin(username, connection);
            }
        } catch (IOException e) {
            connection.disconnect();
        }
    }

    /**
     * Handles a known player login
     *
     * @param username   username of the player who is trying to login
     * @param connection connection of the client
     * @throws IOException when send message fails
     */
    private void knownPlayerLogin(String username, Connection connection) throws IOException {
        if (clients.get(username) == null || !clients.get(username).isConnected()) { // Player Reconnection
            clients.replace(username, connection);

            String token = UUID.randomUUID().toString();
            connection.setToken(token);

            if (waitForLoad) {// Game in lobby state for load a game
                connection.sendMessage(
                        new GameLoadResponse("Successfully reconnected", token,
                                gameManager.getUserPlayerState(username), gameManager.getGameInstance().isTerminatorPresent())
                );
                checkLoadReady();
            } else {
                if (gameManager.getGameState() == PossibleGameState.GAME_ROOM) { // Game in lobby state
                    connection.sendMessage(
                            new ConnectionResponse("Successfully reconnected", token, MessageStatus.OK)
                    );
                } else { // Game started
                    connection.sendMessage(
                            gameManager.onConnectionMessage(new LobbyMessage(username, token, null, false))
                    );
                }
            }

            LOGGER.log(Level.INFO, "{0} reconnected to server!", username);
        } else { // Player already connected
            connection.sendMessage(
                    new ConnectionResponse("Player already connected", null, MessageStatus.ERROR)
            );

            connection.disconnect();
            LOGGER.log(Level.INFO, "{0} already connected to server!", username);
        }
    }

    /**
     * Handles a new player login
     *
     * @param username   username of the player who is trying to login
     * @param connection connection of the client
     * @throws IOException when send message fails
     */
    private void newPlayerLogin(String username, Connection connection) throws IOException {
        if (gameManager.getGameInstance().isGameStarted()) { // Game Started
            connection.sendMessage(
                    new ConnectionResponse("Game is already started!", null, MessageStatus.ERROR)
            );

            connection.disconnect();
            LOGGER.log(Level.INFO, "{0} attempted to connect!", username);
        } else if (gameManager.isLobbyFull()) { // Lobby Full
            connection.sendMessage(
                    new ConnectionResponse("Max number of player reached", null, MessageStatus.ERROR)
            );

            connection.disconnect();
            LOGGER.log(Level.INFO, "{0} tried to connect but game is full!", username);
        } else { // New player
            if (isUsernameLegit(username)) { // Username legit
                clients.put(username, connection);

                String token = UUID.randomUUID().toString();
                connection.setToken(token);

                connection.sendMessage(
                        new ConnectionResponse("Successfully connected", token, MessageStatus.OK)
                );

                LOGGER.log(Level.INFO, "{0} connected to server!", username);
            } else { // Username not legit
                connection.sendMessage(
                        new ConnectionResponse("Invalid Username", null, MessageStatus.ERROR)
                );

                connection.disconnect();
                LOGGER.log(Level.INFO, "{0} tried to connect with invalid name!", username);
            }
        }
    }

    private void checkLoadReady() {
        if (clients.entrySet().stream().noneMatch(entry -> entry.getValue() == null || !entry.getValue().isConnected())) {
            waitForLoad = false;
            gameManager.sendPrivateUpdates();
        }
    }

    /**
     * Checks if a username is legit by checking that is not equal to a forbidden username
     *
     * @param username username to check
     * @return if a username is legit
     */
    private boolean isUsernameLegit(String username) {
        for (String forbidden : FORBIDDEN_USERNAME) {
            if (username.equalsIgnoreCase(forbidden)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Process a message sent to server
     *
     * @param message message sent to server
     */
    void onMessage(Message message) {
        if (message != null && message.getSenderUsername() != null && (message.getToken() != null || message.getSenderUsername().equals("god"))) {
            if (message.getContent().equals(MessageContent.SHOOT)) LOGGER.log(Level.INFO, message.toString());
            else LOGGER.log(Level.INFO, "Received: {0}", message);
            String msgToken = message.getToken();
            Connection conn = clients.get(message.getSenderUsername());

            if (conn == null) {
                LOGGER.log(Level.INFO, "Message Request {0} - Unknown username {1}", new Object[]{message.getContent().name(), message.getSenderUsername()});
            } else if (msgToken.equals(conn.getToken())) { // Checks that sender is the real player
                Message response = gameManager.onMessage(message);
                if (message.getContent().equals(MessageContent.LOBBY_VOTE)) sendMessageToAll(new LobbyPlayersResponse(new ArrayList<>(clients.keySet())));
                // update timer
                if (Game.getInstance().isGameStarted()) {
                    if (message.getContent().equals(MessageContent.PASS_TURN)
                            && response.getContent().equals(MessageContent.RESPONSE)
                            && ((Response) response).getStatus().equals(MessageStatus.OK)) { // if the player pass the turn with success
                        // cancel the move timer for the previous player and schedule another one for the new player to play
                        Connection connection = clients.get(gameManager.getRoundManager().getTurnManager().getTurnOwner().getUsername());
                        String user = gameManager.getRoundManager().getTurnManager().getTurnOwner().getUsername();
                        LOGGER.log(Level.INFO, "Move timer reset for user {0}, {1} seconds left", new Object[]{user, moveTime / 1000});

                        moveTimer.cancel();
                        moveTimer = new Timer();
                        moveTimer.schedule(new MoveTimer(connection, user), moveTime);
                    } else if (message.getSenderUsername().equals(gameManager.getRoundManager().getTurnManager().getTurnOwner().getUsername())) { // if the message was send by the current user
                        LOGGER.log(Level.INFO, "Move timer reset for user {0}, {1} seconds left", new Object[]{message.getSenderUsername(), moveTime / 1000});

                        moveTimer.cancel();
                        moveTimer = new Timer();
                        moveTimer.schedule(new MoveTimer(conn, message.getSenderUsername()), moveTime);
                    }
                }
                // send message to client
                sendMessage(message.getSenderUsername(), response);
            }
        }
    }

    /**
     * Called when a player disconnects
     *
     * @param playerConnection connection of the player that just disconnected
     */
    void onDisconnect(Connection playerConnection) {
        String username = getUsernameByConnection(playerConnection);

        if (username != null) {
            LOGGER.log(Level.INFO, "{0} disconnected from server!", username);

            if (gameManager.getGameState() == PossibleGameState.GAME_ROOM) {
                clients.remove(username);

                // if game not started yet, send to all the updated list of players in lobby
                if (!Game.getInstance().isGameStarted()) sendMessageToAll(new LobbyPlayersResponse(new ArrayList<>(clients.keySet())));
                gameManager.onMessage(new LobbyMessage(username, null, null, true));
                LOGGER.log(Level.INFO, "{0} removed from client list!", username);
            } else {
                gameManager.onConnectionMessage(new LobbyMessage(username, null, null, true));
            }

            sendMessageToAll(new DisconnectionMessage(username));
        }
    }

    /**
     * Sends a message to all clients
     *
     * @param message message to send
     */
    public void sendMessageToAll(Message message) {
        for (Map.Entry<String, Connection> client : clients.entrySet()) {
            if (client.getValue() != null && client.getValue().isConnected()) {
                try {
                    client.getValue().sendMessage(message);
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }
        LOGGER.log(Level.INFO, "Send to all: {0}", message);
    }

    /**
     * Sends a message to a client
     *
     * @param username username of the client who will receive the message
     * @param message  message to send
     */
    public void sendMessage(String username, Message message) {
        for (Map.Entry<String, Connection> client : clients.entrySet()) {
            if (client.getKey().equals(username) && client.getValue() != null && client.getValue().isConnected()) {
                try {
                    client.getValue().sendMessage(message);
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
                break;
            }
        }

        if (message.getContent().equals(MessageContent.COLOR_RESPONSE)) sendMessageToAll(new LobbyPlayersResponse(new ArrayList<>(clients.keySet())));
        LOGGER.log(Level.INFO, "Send: {0}, {1}", new Object[]{message.getSenderUsername(), message});
    }

    /**
     * Returns the username of the connection owner
     *
     * @param connection connection to check
     * @return the username
     */
    private String getUsernameByConnection(Connection connection) {
        Set<String> usernameList = clients.entrySet()
                .stream()
                .filter(entry -> connection.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (usernameList.isEmpty()) {
            return null;
        } else {
            return usernameList.iterator().next();
        }
    }

    /**
     * Process that pings all the clients to check if they are still connected
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (Map.Entry<String, Connection> client : clients.entrySet()) {
                if (client.getValue() != null && client.getValue().isConnected()) {
                    client.getValue().ping();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
