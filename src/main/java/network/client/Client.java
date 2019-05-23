package network.client;

import network.message.Message;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public abstract class Client extends UnicastRemoteObject {
    private final String username;
    private final String address;
    private final int port;

    final ArrayList<Message> messageQueue;

    public Client(String username, String address, int port) throws RemoteException {
        this.username = username;
        this.address = address;
        this.port = port;

        this.messageQueue = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public abstract void startConnection() throws Exception;

    public abstract void sendMessage(Message message) throws IOException;

    public abstract void close() throws Exception;

    public ArrayList<Message> receiveMessages() {
        ArrayList<Message> copyList;

        synchronized (messageQueue) {
            copyList = new ArrayList<>(List.copyOf(messageQueue));
            messageQueue.clear();
        }

        return copyList;
    }

    public String getUsername() {
        return username;
    }
}
