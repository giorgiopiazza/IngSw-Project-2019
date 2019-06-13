package network.server;

import enumerations.MessageContent;
import network.message.Message;
import network.message.PingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class represents a Socket connection with a client
 */
class SocketConnection extends Connection implements Runnable {
    private final SocketServer socketServer;
    private final Socket socket;

    private final Object outLock = new Object();
    private final Object inLock = new Object();

    private boolean connected;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Thread listener;

    /**
     * Constructs a connection over the socket with the socket server
     *
     * @param socketServer socket server
     * @param socket       socket of the client
     */
    SocketConnection(SocketServer socketServer, Socket socket) {
        this.socketServer = socketServer;
        this.socket = socket;

        this.connected = true;

        try {
            synchronized (inLock) {
                this.in = new ObjectInputStream(socket.getInputStream());
            }

            synchronized (outLock) {
                this.out = new ObjectOutputStream(socket.getOutputStream());
            }
        } catch (IOException e) {
            Server.LOGGER.severe(e.toString());
        }

        listener = new Thread(this);
        listener.start();
    }

    /**
     * Process that continues to listen the input stream and send the messages to
     * server in case of message
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                synchronized (inLock) {
                    Message message = (Message) in.readObject();

                    if (message != null) {
                        if (message.getContent() == MessageContent.CONNECTION) {
                            socketServer.login(message.getSenderUsername(), this);
                        } else {
                            socketServer.onMessage(message);
                        }
                    }
                }
            } catch (IOException e) {
                disconnect();
                Server.LOGGER.severe(e.getMessage());
            } catch (ClassNotFoundException e) {
                Server.LOGGER.severe(e.getMessage());
            }
        }
    }

    /**
     * @return the connection status
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Sends a message to the client
     *
     * @param message to send to the client
     */
    @Override
    public void sendMessage(Message message) {
        if (connected) {
            try {
                synchronized (outLock) {
                    out.writeObject(message);
                    out.reset();
                }
            } catch (IOException e) {
                Server.LOGGER.severe(e.getMessage());
                disconnect();
            }
        }
    }

    /**
     * Disconnects from the client
     */
    @Override
    public void disconnect() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            Server.LOGGER.severe(e.getMessage());
        }

        listener.interrupt(); // Interrupts the thread
        connected = false;

        socketServer.onDisconnect(this);
    }

    /**
     * Sends a ping message to client
     */
    @Override
    public void ping() {
        sendMessage(new PingMessage());
    }
}
