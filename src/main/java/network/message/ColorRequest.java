package network.message;

import enumerations.MessageContent;

public class ColorRequest extends Message {
    public ColorRequest(String username, String token) {
        super(username, token, MessageContent.COLOR);
    }
}
