package network.client;

import enumerations.MessageContent;
import network.message.ConnectionRequest;
import network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Timer;
import java.util.logging.Logger;

/**
 * This class represents a Socket Client
 */
public class ClientSocket extends Client implements Runnable {
    private static final long serialVersionUID = -7286675375073912395L;
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
    public ClientSocket(String username, String address, int port, DisconnectionListener disconnectionListener) throws IOException {
        super(username, address, port, disconnectionListener);
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
                } else if (message != null && message.getContent() == MessageContent.PING) {
                    super.pingTimer.cancel();
                    super.pingTimer = new Timer();
                    super.pingTimer.schedule(new PingTimerTask(super.disconnectionListener), Client.DISCONNECTION_TIME);
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
            Logger.getLogger("adrenaline_client").severe(e.getMessage());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClientSocket that = (ClientSocket) o;
        return Objects.equals(socket, that.socket) &&
                Objects.equals(in, that.in) &&
                Objects.equals(out, that.out) &&
                Objects.equals(messageReceiver, that.messageReceiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), socket, in, out, messageReceiver);
    }
}
