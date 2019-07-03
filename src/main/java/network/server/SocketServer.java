package network.server;

import network.message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {
    private final Server server;
    private final int port;

    private ServerSocket serverSocket;

    public SocketServer(Server server, int port) {
        this.server = server;
        this.port = port;
    }

    void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            start();
        } catch (IOException e) {
            Server.LOGGER.severe(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket client = serverSocket.accept();
                new SocketConnection(this, client);
            } catch (IOException e) {
                Server.LOGGER.warning(e.getMessage());
            }
        }
    }

    void login(String username, Connection connection) {
        server.login(username, connection);
    }

    void onMessage(Message message) {
        server.onMessage(message);
    }

    void onDisconnect(Connection playerConnection) {
        server.onDisconnect(playerConnection);
    }
}
