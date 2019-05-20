package network.client;

import network.message.Message;

import java.util.List;

public abstract class Client {
    private String username;

    public Client(String username) {
        this.username = username;
    }

    public abstract void sendMessage(Message message) throws Exception;

    public abstract List<Message> receiveMessages() throws Exception;

    public abstract  void close() throws Exception;

    public String getUsername() {
        return username;
    }


}
