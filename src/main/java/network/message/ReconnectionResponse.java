package network.message;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import model.Game;

public class ReconnectionResponse extends Message {
    private static final long serialVersionUID = -3306576176165651299L;

    private final String newToken;
    private final String message;
    private final MessageStatus status;

    public ReconnectionResponse(String message, String newToken, MessageStatus status) {
        super(Game.GOD, null, MessageContent.RECONNECTION);
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
