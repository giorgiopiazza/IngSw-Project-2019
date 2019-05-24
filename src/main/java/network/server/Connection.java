package network.server;

import network.message.Message;

import java.io.IOException;

/**
 * This interface that represents a connection with a client
 */
public interface Connection {
    /**
     * @return the connection status
     */
    boolean isConnected();

    /**
     * Sends a message to the client
     *
     * @param message message to send to the client
     * @throws IOException in case of problems with communication with client
     */
    void sendMessage(Message message) throws IOException;

    /**
     * Disconnects from the client
     */
    void disconnect();

    /**
     * Sends a ping message to client
     */
    void ping();
}
