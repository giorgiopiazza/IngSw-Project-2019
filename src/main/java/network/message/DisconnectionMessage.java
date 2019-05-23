package network.message;

import enumerations.MessageContent;
import model.Game;

public class DisconnectionMessage extends Message {
    private final String username;

    public DisconnectionMessage(String username) {
        super(Game.GOD, MessageContent.DISCONNECTION);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
