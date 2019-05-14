package network.server;

public interface CloseConnectionListener {

    /**
     * Close the connection with that {@code username} and remove the client from the clients list
     *
     * @param username the username to remove
     */
    public void onCloseConnection(String username);

}
