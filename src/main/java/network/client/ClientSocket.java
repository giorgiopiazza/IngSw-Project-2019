package network.client;

import network.message.ConnectionRequest;
import network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientSocket extends Client implements Runnable {
    private transient Socket socket;

    private transient ObjectInputStream in;
    private transient ObjectOutputStream out;

    private transient Thread messageReceiver;

    public ClientSocket(String username, String address, int port) throws IOException {
        super(username, address, port);
    }

    @Override
    public void startConnection() throws IOException {
        socket = new Socket(getAddress(), getPort());
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        sendMessage(new ConnectionRequest(getUsername()));

        messageReceiver = new Thread(this);
        messageReceiver.start();
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        if (out != null) {
            out.writeObject(message);
            out.reset();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = (Message) in.readObject();

                if (message != null) {
                    synchronized (messageQueue) {
                        messageQueue.add(message);
                    }
                }
            } catch (IOException e) {
                disconnect();
            } catch (ClassNotFoundException e) {
                // Discard Message
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                disconnect();
            }
        }
    }

    private void disconnect() {
        try {
            close();
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
        }

        messageReceiver.interrupt();
    }

    @Override
    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }

        in = null;
        out = null;
    }
}
