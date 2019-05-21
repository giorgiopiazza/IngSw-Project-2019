package network.server;

import enumerations.MessageStatus;
import network.message.Message;
import network.message.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    public static final int SOCKET_PORT = 2727;
    public static final int RMI_PORT = 7272;

    private static final int MAX_CLIENT = 5;

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
                            new Response("Max number of player reached ", MessageStatus.ERROR)
                    );

                    session.disconnect();
                    LOGGER.log(Level.INFO, "{0} tried to connect but game is full!", username);
                } else { // New player
                    clients.put(username, session);
                    session.sendMessage(
                            new Response("Successfully connected", MessageStatus.OK)
                    );
                    LOGGER.log(Level.INFO, "{0} connected to server!", username);
                }
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * Process a message sent to server
     *
     * @param message sent to server
     */
    public void onMessage(Message message) {
        // TODO
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
                    LOGGER.severe(e.getMessage());
                }
            }
        }
    }

    /**
     * Sends a message to a client
     *
     * @param username of the client who will receive the message
     * @param message to send
     */
    public void sendMessage(String username, Message message) {
        for (Map.Entry<String, Session> client : clients.entrySet()) {
            if (client.getKey().equals(username) && client.getValue().isConnected()) {
                try {
                    client.getValue().sendMessage(message);
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
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

}
