package network.server;

import enumerations.MessageStatus;
import network.message.Message;
import network.message.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

class ServerThreadSocket extends ServerThread {
    private boolean requested;
    private final List<Message> sendQueue;
    private CloseConnectionListener listener;

    ServerThreadSocket(Socket socket, String username, ObjectInputStream in, CloseConnectionListener listener) {
        super(socket, username, in);
        this.listener = listener;
        sendQueue = new ArrayList<>();
        requested = false;
    }

    @Override
    public synchronized void start() {
        super.start();
        MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: started", this.id);
    }

    @Override
    public void run() {
        while (true) {
            Message cmd;

            synchronized (socket) {
                // read request from the client
                try {
                    cmd = (Message) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    MultiServer.LOGGER.log(Level.SEVERE, e.toString());
                    cmd = null;
                }

                sendQueueMessages();

                if (cmd == null) break;

                // switch with possible requests by client
                try {
                    messageCasesSwitch(cmd);
                } catch (IOException e) {
                    MultiServer.LOGGER.log(Level.SEVERE, e.toString());
                }

                try {
                    out.reset();
                } catch (IOException e) {
                    MultiServer.LOGGER.log(Level.SEVERE, e.toString());
                }

            }
            MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: {1}", new Object[] {id, cmd});
        }

        synchronized (socket) {
            try {
                socket.close();
            } catch (IOException e) {
                MultiServer.LOGGER.log(Level.SEVERE, e.toString());
            }
        }
        MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: stop", this.id);
        listener.onCloseConnection(username);
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

    private void messageCasesSwitch(Message request) throws IOException {
        // TODO: all request
        switch (request.getContent()) {
            case READY:
                break;
            case TERMINATOR_SPAWN:
                break;
            case DISCARD_POWERUP:
                break;
            case MOVE:
                // only for testing, to change
                out.writeObject(new Response("moved", MessageStatus.OK));
                break;
            case MOVE_PICK:
                break;
            case SHOOT:
                break;
            case RELOAD:
                break;
            case POWERUP:
                break;
            case PASS_TURN:

                break;

            default:

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
            } catch (IOException e) {
                MultiServer.LOGGER.log(Level.SEVERE, e.toString());
            }
        }
    }

}
