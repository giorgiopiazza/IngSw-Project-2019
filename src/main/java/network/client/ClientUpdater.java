package network.client;

import enumerations.MessageContent;
import network.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                List<Message> responses = new ArrayList<>();

                do {
                    messages = client.receiveMessages();
                } while (messages.isEmpty());

                for (Message message : messages) {
                    if (message.getContent() != MessageContent.RESPONSE) {
                        updateListener.onUpdate(message);
                    } else {
                        responses.add(message);
                    }
                }

                if (responses.size() > 1) {
                    // TODO Better exception
                    throw new RuntimeException("You can't reiceve two repsonses at the same time");
                }

                for (Message response : responses) {
                    updateListener.onUpdate(response);
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
