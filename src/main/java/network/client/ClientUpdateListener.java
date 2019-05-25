package network.client;

import network.message.Message;

import java.util.List;

public interface ClientUpdateListener {

    void onUpdate(List<Message> messages);
}
