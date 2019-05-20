package network.server;

import network.message.Message;

abstract class ServerThread extends Thread {
    private static int totId = 0;
    private boolean suspended;
    private MessageListener messageListener;
    private int id;
    private String username;

    ServerThread(String username) {
        this.username = username;
        this.suspended = false;
        this.id = totId++;
    }

    @Override
    public abstract void run();

    /**
     *
     * @param messageListener the {@code messageListener} to set
     */
    public void addMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Send the {@code message} to the connected client
     *
     * @param message the message to send
     */
    public abstract void sendToClient(Message message);

    /**
     * Close the connection for {@code message} reason
     *
     * @param message reason of closing connection
     */
    public abstract void close(Message message);

    public String getUsername() {
        return username;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public int getServerThreadId() {
        return id;
    }
}
