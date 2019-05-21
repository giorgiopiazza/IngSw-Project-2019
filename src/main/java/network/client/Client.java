package network.client;

import network.message.Message;
import network.server.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class Client extends UnicastRemoteObject {
    private final String username;
    private final String address;
    private final int port;

    public Client(String username, String address, int port) throws RemoteException {
        this.username = username;
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public abstract void startConnection() throws Exception;

    public abstract void sendMessage(Message message) throws Exception;

    public abstract void close() throws Exception;

    public String getUsername() {
        return username;
    }
}
