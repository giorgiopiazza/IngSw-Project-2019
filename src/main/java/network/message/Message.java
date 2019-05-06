package network.message;

import enumerations.MessageContent;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private final int senderID;
    private final MessageContent content;

    Message(int senderID, MessageContent content) {
        this.senderID = senderID;
        this.content = content;
    }

    public int getSenderID() {
        return senderID;
    }

    public MessageContent getContent() {
        return content;
    }
}
