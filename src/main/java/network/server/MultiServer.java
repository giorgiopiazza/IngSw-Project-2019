package network.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiServer {

    public static final int PORT = 2727;
    private ServerSocket serverSocket;
    private List<ServerThread> clients;
    static final Logger LOGGER = Logger.getGlobal();

    public MultiServer() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) { LOGGER.log(Level.SEVERE, e.toString()); }

        clients = new ArrayList<>();
    }

    public boolean acceptClient() {
        if(clients.size() >= 5) return false;

        Socket client;

        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString());
            return false;
        }

        ServerThread serverThread = new ServerThread(client, clients.size());
        clients.add(serverThread);
        serverThread.start();

        LOGGER.log(Level.INFO, "Accepted client: {0}", client);

        return true;
    }

    public InetAddress getAddress() {
        return serverSocket.getInetAddress();
    }

    public boolean closeClient(Socket client) {
        for (int i = 0; i < clients.size(); i++) {
            final Socket that = clients.get(i).getClient();

            synchronized (that) {
                if (that.equals(client)) {
                    try {
                        that.close();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, e.toString());
                        return false;
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public void closeAll() {
        for (ServerThread client : clients) {
            Socket socket = client.getClient();
            synchronized (socket) {
                try {
                    socket.close();
                } catch (IOException e) { LOGGER.log(Level.SEVERE, e.toString()); }
            }
        }
    }
}
