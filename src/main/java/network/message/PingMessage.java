package network.message;

import enumerations.MessageContent;
import model.Game;

public class PingMessage extends Message {
    public PingMessage() {
        super(Game.GOD, null, MessageContent.PING);
    }
}
