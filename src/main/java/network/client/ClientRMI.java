package network.client;

import network.message.Message;

import java.util.List;

public class ClientRMI extends Client {

    public ClientRMI(String username) {
        super(username);
    }

    @Override
    public void sendMessage(Message message) throws Exception {

    }

    @Override
    public List<Message> receiveMessages() throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {

    }

}
