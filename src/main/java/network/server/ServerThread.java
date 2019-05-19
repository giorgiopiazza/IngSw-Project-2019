package network.server;

import network.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

abstract class ServerThread extends Thread {
    private boolean suspended;
    int id;
    final Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    String username;

    ServerThread(Socket socket, String username, ObjectInputStream in) {
        this.username = username;
        this.socket = socket;
        this.suspended = false;

        try {
            this.in = in;
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) { MultiServer.LOGGER.log(Level.SEVERE, e.toString()); }
    }

    public Socket getClient() {
        return socket;
    }

    /**
     * Send the {@code message} to the connected client
     *
     * @param message the message to send
     */
    public abstract void sendToClient(Message message);

    /**
     * Close the connection for {@code message} reason
     *
     * @param message reason of closing connection
     */
    public abstract void close(Message message);

    public String getUsername() {
        return username;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}
