package network.server;

@FunctionalInterface
public interface CloseConnectionListener {

    /**
     * Close the connection with that {@code username} and remove the client from the clients list
     *
     * @param username the username to remove
     */
    void onCloseConnection(String username);

}
