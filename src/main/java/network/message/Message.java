package network.message;

import enumerations.MessageContent;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private final String senderUsername;
    private final MessageContent content;

    Message(String senderUsername, MessageContent content) {
        this.senderUsername = senderUsername;
        this.content = content;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public MessageContent getContent() {
        return content;
    }
}
