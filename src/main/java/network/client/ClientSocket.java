package network.client;

import enumerations.MessageContent;
import network.message.ConnectionRequest;
import network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * This class represents a Socket Client
 */
public class ClientSocket extends Client implements Runnable {
    private transient Socket socket;

    private transient ObjectInputStream in;
    private transient ObjectOutputStream out;

    private transient Thread messageReceiver;

    /**
     * Constructs a RMI client
     *
     * @param username username of the player
     * @param address  address of the server
     * @param port     port of the server
     * @throws IOException in case of problems with communication with server
     */
    public ClientSocket(String username, String address, int port) throws IOException {
        super(username, address, port);
    }

    /**
     * Starts a connection with server
     *
     * @throws IOException in case of problems with communication with server
     */
    @Override
    public void startConnection() throws IOException {
        socket = new Socket(getAddress(), getPort());
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        sendMessage(new ConnectionRequest(getUsername()));

        messageReceiver = new Thread(this);
        messageReceiver.start();
    }

    /**
     * Sends a message to server
     *
     * @param message message to send to the server
     * @throws IOException in case of problems with communication with server
     */
    @Override
    public void sendMessage(Message message) throws IOException {
        if (out != null) {
            out.writeObject(message);
            out.reset();
        }
    }

    /**
     * Process that listens the input stream and adds messages to the queue of messages
     * Pings messages are discarded
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = (Message) in.readObject();

                if (message != null && message.getContent() != MessageContent.PING) {
                    synchronized (messageQueue) {
                        messageQueue.add(message);
                    }
                }
            } catch (IOException e) {
                disconnect();
            } catch (ClassNotFoundException e) {
                // Discard Message
            }
        }
    }

    /**
     * Disconnects the client and interrupts the thread
     */
    private void disconnect() {
        try {
            close();
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }

    /**
     * Closes connection with server
     *
     * @throws IOException in case of problems with communication with server
     */
    @Override
    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }

        messageReceiver.interrupt();

        in = null;
        out = null;
    }
}
