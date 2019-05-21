package network.server;

import network.client.RMIClientSession;
import network.message.Message;

import java.rmi.RemoteException;

public class RMISession implements Session {
    private final RMIClientSession clientSession;
    private boolean connected = true;

    public RMISession(RMIClientSession clientSession) {
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
    }
}
