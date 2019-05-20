package network.server;

import controller.GameManager;
import network.message.Message;
import network.message.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

class ServerThreadSocket extends ServerThread {
    private boolean requested;
    private final List<Message> sendQueue;
    private CloseConnectionListener closeConnectionListener;
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean closed;

    ServerThreadSocket(Socket socket, String username, ObjectInputStream in) {
        super(username);

        this.socket = socket;

        try {
            this.in = in;
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) { MultiServer.LOGGER.log(Level.SEVERE, e.toString()); }

        sendQueue = new ArrayList<>();
        requested = false;
        closed = false;

        addMessageListener(GameManager.getInstance());
    }

    ServerThreadSocket(Socket socket, String username, ObjectInputStream in, CloseConnectionListener closeConnectionListener) {
        this(socket, username, in);
        this.closeConnectionListener = closeConnectionListener;
    }

    @Override
    public synchronized void start() {
        super.start();
        MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: started", getServerThreadId());
    }

    void addCloseConnectionListener(CloseConnectionListener closeConnectionListener) {
        this.closeConnectionListener = closeConnectionListener;
    }

    @Override
    public void run() {
        while (true) {
            Message cmd = null;
            Response response = null;

            synchronized (socket) {
                // read request from the client
                try {
                    cmd = (Message) in.readObject();
                } catch (IOException e) {
                    MultiServer.LOGGER.log(Level.SEVERE, e.toString());
                    cmd = null;
                } catch (ClassNotFoundException e) {
                    // TODO: qualcuno ha fatto il furbo, da gestire
                }

                sendQueueMessages();

                if (cmd == null) break;

                try {
                    response = getMessageListener().onMessage(cmd);
                    out.writeObject(response);
                    out.reset();
                } catch (IOException e) {
                    MultiServer.LOGGER.log(Level.SEVERE, e.toString());
                }

            }
            MultiServer.LOGGER.log(Level.INFO, "ServerThread {0} <request: {1}, response: {2}>", new Object[] {getServerThreadId(), cmd, response});
        }

        synchronized (socket) {
            try {
                socket.close();
            } catch (IOException e) {
                MultiServer.LOGGER.log(Level.SEVERE, e.toString());
            }
        }
        MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: stop", getServerThreadId());
        if (!closed) closeConnectionListener.onCloseConnection(getUsername());
    }

    /**
     * send to client the queue of server side messages
     * these messages cannot be requested by any client
     * type of server side messages: GAME_STATE
     */
    private void sendQueueMessages() {
        synchronized (sendQueue) {
            if (this.requested) {
                try {
                    for (Message message : sendQueue) {
                        out.writeObject(message);
                        out.reset();
                    }
                    sendQueue.clear();
                    this.requested = false;
                } catch (IOException e) {
                    MultiServer.LOGGER.log(Level.SEVERE, e.toString());
                }
            }
        }
    }

    /**
     * Send the {@code message} to the client
     *
     * @param message the message to send
     */
    @Override
    public void sendToClient(Message message) {
        synchronized (sendQueue) {
            this.requested = true;
            this.sendQueue.add(message);
        }
    }

    @Override
    public void close(Message message) {
        synchronized (socket) {
            try {
                out.writeObject(message);
                out.reset();
                socket.close();
                closed = true;
            } catch (IOException e) {
                MultiServer.LOGGER.log(Level.SEVERE, e.toString());
            }
        }
    }

}
