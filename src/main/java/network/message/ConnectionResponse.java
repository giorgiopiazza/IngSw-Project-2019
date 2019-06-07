package network.message;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import model.Game;

public class ConnectionResponse extends Message {
    private static final long serialVersionUID = 8971780730242420173L;

    private final String newToken;
    private final String message;
    private final MessageStatus status;

    public ConnectionResponse(String message, String newToken, MessageStatus status) {
        super(Game.GOD, null, MessageContent.CONNECTION_RESPONSE);
        this.message = message;
        this.newToken = newToken;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getNewToken() {
        return newToken;
    }

    public MessageStatus getStatus() {
        return status;
    }


}
