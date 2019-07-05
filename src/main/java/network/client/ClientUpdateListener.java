package network.client;

import network.message.Message;

/**
 * This interface is used to notify the implementer of an incoming message
 */
public interface ClientUpdateListener {

    /**
     * This method notify the implementer that a message has been received
     *
     * @param message the received message from the server
     */
    void onUpdate(Message message);
}
