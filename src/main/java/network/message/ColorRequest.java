package network.message;

import enumerations.MessageContent;

/**
 * Message class for querying which color are free to use
 */
public class ColorRequest extends Message {
    private static final long serialVersionUID = 8937363835835301847L;

    public ColorRequest(String username, String token) {
        super(username, token, MessageContent.COLOR);
    }

    @Override
    public String toString() {
        return "ColorRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                "}";
    }
}
