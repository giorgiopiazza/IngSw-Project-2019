package network.client;

import network.message.Message;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientUpdater implements Runnable {
    private final Client client;
    private ClientUpdateListener updateListener;

    public ClientUpdater(Client client, ClientUpdateListener updateListener) {
        this.client = client;
        this.updateListener = updateListener;

        new Thread(this).start();
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
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.getGlobal().log(Level.SEVERE, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
