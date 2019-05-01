package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
    public final int senderID;

    public Message(int senderID) {
        this.senderID = senderID;
    }
}
