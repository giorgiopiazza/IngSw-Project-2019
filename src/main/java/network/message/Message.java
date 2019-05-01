package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private final int senderID;

    public Message(int senderID) {
        this.senderID = senderID;
    }

    public int getSenderID() {
        return senderID;
    }
}
