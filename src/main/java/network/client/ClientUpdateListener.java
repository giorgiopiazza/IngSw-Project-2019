package network.client;

import network.message.Message;

public interface ClientUpdateListener {

    void onUpdate(Message message);
}
