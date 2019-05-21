package network.server;

import network.client.RMIClientSession;
import network.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIHandlerImplementation extends UnicastRemoteObject implements RMIHandler {
    private final Server server;

    public RMIHandlerImplementation(Server server) throws RemoteException {
        this.server = server;
    }

    @Override
    public void login(String username, RMIClientSession clientSession) {
        RMISession rmiSession = new RMISession(clientSession);
        server.login(username, rmiSession);
    }

    @Override
    public void onMessage(Message message) {
        server.onMessage(message);
    }
}
