package network.client;

import network.message.Message;

import java.util.List;

public class ClientUpdater implements Runnable {
    private final Client client;
    private ClientUpdateListener updateListener;
    private Thread thread;

    public ClientUpdater(Client client, ClientUpdateListener updateListener) {
        this.client = client;
        this.updateListener = updateListener;
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (client) {
                List<Message> messages;

                do {
                    messages = client.receiveMessages();
                } while (messages.isEmpty());

                messages.forEach(updateListener::onUpdate);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        this.thread.interrupt();
    }

    public void start() {
        if (this.thread.isInterrupted()) {
            this.thread.start();
        }
    }
}
