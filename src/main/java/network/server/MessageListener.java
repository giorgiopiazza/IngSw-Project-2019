package network.server;

import network.message.Message;
import network.message.Response;

public interface MessageListener {

    /**
     * given a message received from a client, the calculated response returns
     *
     * @param received the message received from a client
     * @return the response calculated based on the request
     */
    public Response onMessage(Message received);
}
