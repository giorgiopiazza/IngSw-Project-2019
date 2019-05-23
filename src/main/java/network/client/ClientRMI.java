package network.client;

import network.message.Message;
import network.server.RMIHandler;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI extends Client implements RMIClientSession {
    private RMIHandler server;
    private Registry registry;

    public ClientRMI(String username, String address, int port) throws RemoteException {
        super(username, address, port);
    }

    @Override
    public void startConnection() throws IOException, NotBoundException {
        registry = LocateRegistry.getRegistry(getAddress(), getPort());
        server = (RMIHandler) registry.lookup("AdrenalineServer");

        server.login(getUsername(), this);
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        if (server == null) {
            return;
        }

        server.onMessage(message);
    }

    @Override
    public void close() throws RemoteException, NotBoundException {
        registry.unbind("AdrenalineServer");
        server = null;
    }

    @Override
    public void onMessage(Message message) {
        // TODO
    }

    @Override
    public void ping() {
        // Pinged
    }
}
