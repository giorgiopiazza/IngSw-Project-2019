package network.client;

import network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is the one sent to the RMI server enabling him to send messages to client
 */
public interface RMIClientConnection extends Remote {
    /**
     * Sends a message to client
     *
     * @param message message sent to client
     * @throws RemoteException in case of problems with communication with client
     */
    void onMessage(Message message) throws RemoteException;

    /**
     * Sends a ping message to client
     *
     * @throws RemoteException in case of problems with communication with client
     */
    void ping() throws RemoteException;

    /**
     * Disconnects the client from server
     *
     * @throws RemoteException in case of problems with communication with client
     */
    void disconnectMe() throws RemoteException;

}
