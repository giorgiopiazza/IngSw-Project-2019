package network.message;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import model.Game;

public class Response extends Message {
    private final String message;
    private final MessageStatus status;

    public Response(String message, MessageStatus status) {
        super(Game.GOD, null, MessageContent.RESPONSE);

        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public MessageStatus getStatus() {
        return status;
    }
}
