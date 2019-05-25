package network.client;

import network.message.Message;

import java.util.List;

public interface ClientUpdateListener {

    public void onUpdate(List<Message> messages);

}
