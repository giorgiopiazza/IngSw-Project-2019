package network.server;

import network.client.RMIClientSession;
import network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIHandler extends Remote {
    void login(String username, RMIClientSession RMIClientSession) throws RemoteException;
    void onMessage(Message message) throws RemoteException;
}
