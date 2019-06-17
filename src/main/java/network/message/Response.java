package network.message;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import model.Game;

public class Response extends Message {
    private static final long serialVersionUID = 9049719323850459931L;

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

    @Override
    public String toString() {
        return "Response{" +
                "content=" + getContent() +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
