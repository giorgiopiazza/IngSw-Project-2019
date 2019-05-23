package network.client;

import network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientSession extends Remote {
    void onMessage(Message message) throws RemoteException;
    void ping() throws RemoteException;
}
