package network.client;

import network.message.Message;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Client
 */
public abstract class Client extends UnicastRemoteObject {
    private final String username;
    private final String address;
    private final int port;

    final ArrayList<Message> messageQueue;

    /**
     * Constructs a client
     *
     * @param username username of the player
     * @param address  address of the server
     * @param port     port of the server
     * @throws RemoteException in case of problems with communication with server
     */
    public Client(String username, String address, int port) throws RemoteException {
        this.username = username;
        this.address = address;
        this.port = port;

        this.messageQueue = new ArrayList<>();
    }

    /**
     * @return the address of the server
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the port of the server
     */
    public int getPort() {
        return port;
    }

    /**
     * Starts a connection with the server
     *
     * @throws Exception in case of problems with communication with server
     */
    public abstract void startConnection() throws Exception;


    /**
     * Sends a message to the server
     * @param message message to send to the server
     * @throws IOException in case of problems with communication with server
     */
    public abstract void sendMessage(Message message) throws IOException;

    /**
     * Closes connection with the server
     * @throws Exception in case of problems with communication with server
     */
    public abstract void close() throws Exception;

    /**
     * @return the list of messages in the queue
     */
    public ArrayList<Message> receiveMessages() {
        ArrayList<Message> copyList;

        synchronized (messageQueue) {
            copyList = new ArrayList<>(List.copyOf(messageQueue));
            messageQueue.clear();
        }

        return copyList;
    }

    /**
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }
}
