package network.message;

import enumerations.MessageContent;
import model.Game;

public class PingMessage extends Message {
    private static final long serialVersionUID = 8092508198825773159L;

    public PingMessage() {
        super(Game.GOD, null, MessageContent.PING);
    }
}
