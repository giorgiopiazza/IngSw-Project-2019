package network.client;

import network.message.Message;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientUpdater implements Runnable {
    private final Client client;
    private ClientUpdateListener updateListener;
    private boolean stop;
    private Thread thread;
    private final Object waiter;

    public ClientUpdater(Client client, ClientUpdateListener updateListener, Object waiter) {
        this.client = client;
        this.updateListener = updateListener;
        this.stop = false;
        this.waiter = waiter;
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

                updateListener.onUpdate(messages);
                synchronized (waiter) {
                    waiter.notify();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.getGlobal().log(Level.SEVERE, e.getMessage());
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
