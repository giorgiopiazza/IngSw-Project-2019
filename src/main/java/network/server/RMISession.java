package network.server;

import network.client.RMIClientSession;
import network.message.Message;

import java.rmi.RemoteException;

public class RMISession implements Session {
    private final Server server;
    private final RMIClientSession clientSession;

    private boolean connected = true;

    public RMISession(Server server, RMIClientSession clientSession) {
        this.server = server;
        this.clientSession = clientSession;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        clientSession.onMessage(message);
    }

    @Override
    public void disconnect() {
        connected = false;
        server.onDisconnect(this);
    }

    @Override
    public void ping() {
        try {
            clientSession.ping();
        } catch (RemoteException e) {
            disconnect();
        }
    }
}
