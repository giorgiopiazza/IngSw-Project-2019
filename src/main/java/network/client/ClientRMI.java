package network.client;

import network.message.Message;
import network.server.RMIHandler;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * This class represents a RMI Client
 */
public class ClientRMI extends Client implements RMIClientConnection {
    private static final long serialVersionUID = -6701767031892802332L;
    private transient RMIHandler server;

    /**
     * Constructs a RMI client
     *
     * @param username username of the player
     * @param address  address of the server
     * @param port     port of the server
     * @throws RemoteException in case of problems with communication with server
     */
    public ClientRMI(String username, String address, int port) throws RemoteException {
        super(username, address, port);
    }

    /**
     * Starts a connection with server
     *
     * @throws IOException       in case of problems with communication with server
     * @throws NotBoundException when the registry doesn't exists
     */
    @Override
    public void startConnection() throws IOException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(getAddress(), getPort());
        server = (RMIHandler) registry.lookup("AdrenalineServer");

        server.login(getUsername(), this);
    }

    /**
     * Sends a message to server
     *
     * @param message message to send to the server
     * @throws RemoteException in case of problems with communication with server
     */
    @Override
    public void sendMessage(Message message) throws RemoteException {
        if (server == null) {
            throw new RemoteException();
        }

        server.onMessage(message);
    }

    /**
     * Closes connection with server
     *
     * @throws RemoteException   in case of problems with communication with server
     */
    @Override
    public void close() throws RemoteException {
        server.disconnectMe();
        server = null;
    }

    /**
     * Receives a message sent to client
     *
     * @param message message sent to client
     */
    @Override
    public void onMessage(Message message) {
        synchronized (messageQueue) {
            messageQueue.add(message);
        }
    }

    /**
     * Receives a ping message
     */
    @Override
    public void ping() {
        // Pinged
    }

    @Override
    public void disconnectMe() throws RemoteException {
        server = null;
    }
}
