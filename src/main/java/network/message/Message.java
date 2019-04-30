package network.message;

import java.io.Serializable;

public class Message implements Serializable {
    public final int senderID;

    public Message(int senderID) {
        this.senderID = senderID;
    }
}
