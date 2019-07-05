package network.message;

import enumerations.MessageContent;

import java.io.Serializable;

/**
 * This abstract class need to differentiate that the server can send to the client or vice versa
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = -5411382756213360684L;

    private final String senderUsername;
    private final String token;
    private final MessageContent content;

    Message(String senderUsername, String token, MessageContent content) {
        this.senderUsername = senderUsername;
        this.token = token;
        this.content = content;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public MessageContent getContent() {
        return content;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderUsername='" + senderUsername + '\'' +
                ", content=" + content +
                '}';
    }
}
