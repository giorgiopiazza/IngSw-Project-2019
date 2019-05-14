package network.server;

import exceptions.network.ClassAdrenalinaNotFoundException;
import network.message.ConnectionRequest;
import network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiServer implements CloseConnectionListener {
    public static final int SOCKET_PORT = 2727;
    public static final int RMI_PORT = 7272;

    private ServerSocket serverSocket;
    private Registry registry;
    private List<ServerThread> clients;
    static final Logger LOGGER = Logger.getGlobal();

    public MultiServer() throws IOException {
        serverSocket = new ServerSocket(SOCKET_PORT);
        // registry = LocateRegistry.createRegistry(RMI_PORT);

        clients = new ArrayList<>();
    }

    /**
     * accept a client connection, max 5 clients connected
     *
     * @return the username if number of clients before call < 5, otherwise {@code null}
     * @throws IOException if connection abort
     */
    public String acceptSocketClient() throws IOException {
        if(clients.size() >= 5) return null;

        Socket client;
        ConnectionRequest request;

        client = serverSocket.accept();
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(client.getInputStream());
            request = (ConnectionRequest) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new ClassAdrenalinaNotFoundException();
        }

        ServerThread serverThread = new ServerThreadSocket(client, request.getSenderUsername(), in, this);
        clients.add(serverThread);
        serverThread.start();

        LOGGER.log(Level.INFO, "Accepted client: {0}, request: {1}", new Object[] {client, request});

        return request.getSenderUsername();
    }

    /**
     * Get the {@code InetAddress} of the server
     *
     * @return the address
     */
    public InetAddress getAddress() {
        return serverSocket.getInetAddress();
    }

    /**
     * Close the connection of the {@code username} and remove it from the {@code clients} list
     *
     * @param username the username of the client to close
     * @return true if username is present, otherwise false
     */
    public boolean closeClient(String username) {
        for (int i = 0; i < clients.size(); i++) {
            String currUser = clients.get(i).getUsername();
            final Socket currSocket = clients.get(i).getClient();

            if (currUser.equals(username)) {
                synchronized (currSocket) {
                    try {
                        currSocket.close();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, e.toString());
                    }
                }
                clients.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Close all connection and flush the clients list
     *
     */
    public void closeAll() {
        for (ServerThread client : clients) {
            Socket socket = client.getClient();

            synchronized (socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.toString());
                }
            }
        }

        clients.clear();
    }

    /**
     * Send a {@code message} to all connected clients
     *
     * @param message the message to send
     */
    public void sendToAll(Message message) {
        for (ServerThread serverThread : clients) {
            serverThread.sendToClient(message);
        }
    }

    @Override
    public void onCloseConnection(String username) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUsername().equals(username)) {
                clients.remove(i);
                break;
            }
        }
    }
}
