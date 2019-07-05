package network.client;

/**
 * this interface need to notify the implementer when the connection with the server is interrupted
 */
public interface DisconnectionListener {
    /**
     * This method is called when the connection to the server is interrupted
     */
    void onDisconnection();
}
