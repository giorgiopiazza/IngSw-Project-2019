package network.server;

import enumerations.MessageStatus;
import model.Game;
import network.message.DisconnectionMessage;
import network.message.Message;
import network.message.Response;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Server implements Runnable {
    public static final int SOCKET_PORT = 2727;
    public static final int RMI_PORT = 7272;

    private static final int MAX_CLIENT = 5;

    private static final String[] FORBIDDEN_USERNAMES = {Game.GOD, Game.TERMINATOR_USERNAME};

    private Map<String, Session> clients;
    static final Logger LOGGER = Logger.getLogger("Server");

    public Server() {
        clients = new HashMap<>();

        SocketServer serverSocket = new SocketServer(this, SOCKET_PORT);
        serverSocket.startServer();

        LOGGER.info("Socket Server Started");

        RMIServer rmiServer = new RMIServer(this, RMI_PORT);
        rmiServer.startServer();

        LOGGER.info("RMI Server Started");

        Thread pinger = new Thread(this);
        pinger.start();
    }

    public static void main(String[] args) {
        new Server();
    }

    /**
     * Adds or reconnects a player to the server
     *
     * @param username of the player
     * @param session  of the connection
     */
    public void login(String username, Session session) {
        try {
            if (clients.containsKey(username)) {
                if (!session.isConnected()) { // Reconnection
                    clients.replace(username, session);
                    session.sendMessage(
                            new Response("Successfully reconnected", MessageStatus.OK)
                    );

                    LOGGER.log(Level.INFO, "{0} reconnected to server!", username);
                } else { // Username not valid
                    session.sendMessage(
                            new Response("Player already connected", MessageStatus.ERROR)
                    );

                    session.disconnect();
                    LOGGER.log(Level.INFO, "{0} already connected to server!", username);
                }
            } else {
                if (clients.keySet().size() == MAX_CLIENT) { // Max players
                    session.sendMessage(
                            new Response("Max number of player reached", MessageStatus.ERROR)
                    );

                    session.disconnect();
                    LOGGER.log(Level.INFO, "{0} tried to connect but game is full!", username);
                } else { // New player
                    if (isUsernameLegit(username)) {
                        clients.put(username, session);
                        session.sendMessage(
                                new Response("Successfully connected", MessageStatus.OK)
                        );
                        LOGGER.log(Level.INFO, "{0} connected to server!", username);
                    } else {
                        session.sendMessage(
                                new Response("Invalid Username", MessageStatus.ERROR)
                        );

                        session.disconnect();
                        LOGGER.log(Level.INFO, "{0} tried to connect with invalid name!", username);
                    }
                }
            }
        } catch (IOException e) {
            session.disconnect();
        }
    }

    /**
     * Checks if a username is legit by checking that is not equal to a forbidden username
     *
     * @param username to check
     * @return if a username is legit
     */
    private boolean isUsernameLegit(String username) {
        for (String forbidden : FORBIDDEN_USERNAMES) {
            if (username.equalsIgnoreCase(forbidden)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Process a message sent to server
     *
     * @param message sent to server
     */
    public void onMessage(Message message) {
        // TODO
    }

    public void onDisconnect(Session playerSession) {
        String username = getUsernameBySession(playerSession);

        if (username != null) {
            sendMessageToAll(new DisconnectionMessage(username));
            LOGGER.log(Level.INFO, "{0} disconnected from server!", username);
        }
    }

    /**
     * Sends a message to all clients
     *
     * @param message to send
     */
    public void sendMessageToAll(Message message) {
        for (Map.Entry<String, Session> client : clients.entrySet()) {
            if (client.getValue().isConnected()) {
                try {
                    client.getValue().sendMessage(message);
                } catch (IOException e) {
                    // Already handled
                }
            }
        }
    }

    /**
     * Sends a message to a client
     *
     * @param username of the client who will receive the message
     * @param message  to send
     */
    public void sendMessage(String username, Message message) {
        for (Map.Entry<String, Session> client : clients.entrySet()) {
            if (client.getKey().equals(username) && client.getValue().isConnected()) {
                try {
                    client.getValue().sendMessage(message);
                } catch (IOException e) {
                    // Already handled
                }
                break;
            }
        }
    }

    /**
     * Gets all disconnected players
     *
     * @return a list of all username of disconnected players
     */
    public List<String> getDisconnectedPlayers() {
        List<String> players = new ArrayList<>();

        for (Map.Entry<String, Session> client : clients.entrySet()) {
            if (!client.getValue().isConnected()) {
                players.add(client.getKey());
            }
        }

        return players;
    }

    /**
     * Returns the username of the session owner
     *
     * @param session to check
     * @return the username
     */
    private String getUsernameBySession(Session session) {
        Set<String> usernames = clients.entrySet()
                .stream()
                .filter(entry -> session.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (usernames.isEmpty()) {
            return null;
        } else {
            return usernames.iterator().next();
        }
    }

    /**
     * Process that pings all the clients to check if they are still connected
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (Map.Entry<String, Session> client : clients.entrySet()) {
                if (client.getValue().isConnected()) {
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
