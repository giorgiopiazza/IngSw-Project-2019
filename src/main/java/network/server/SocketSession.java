package network.server;

import enumerations.MessageContent;
import network.message.Message;
import network.message.PingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class SocketSession extends Thread implements Session {
    private final SocketServer socketServer;
    private final Socket socket;

    private boolean connected;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    SocketSession(SocketServer socketServer, Socket socket) {
        this.socketServer = socketServer;
        this.socket = socket;

        this.connected = true;

        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Server.LOGGER.severe(e.toString());
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = (Message) in.readObject();
                if (message != null) {
                    if (message.getContent() == MessageContent.CONNECTION) {
                        socketServer.login(message.getSenderUsername(), this);
                    } else {
                        socketServer.onMessage(message);
                    }
                }
            } catch (IOException e) {
                disconnect();
            } catch (ClassNotFoundException e) {
                Server.LOGGER.severe(e.getMessage());
            }
        }
    }

    public void disconnect() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            Server.LOGGER.severe(e.getMessage());
        }

        this.interrupt(); // Interrupts the thread
        connected = false;

        socketServer.onDisconnect(this);
    }

    public void ping() {
        sendMessage(new PingMessage());
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void sendMessage(Message message) {
        if (connected) {
            try {
                out.writeObject(message);
                out.flush();
                out.reset();
            } catch (IOException e) {
                disconnect();
            }
        }
    }
}