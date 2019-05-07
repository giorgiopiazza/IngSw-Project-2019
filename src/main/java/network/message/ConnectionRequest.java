package network.message;

import enumerations.MessageContent;

public class ConnectionRequest extends Message {
    public ConnectionRequest(String username) {
        super(username, MessageContent.CONNECTION);
    }
}
