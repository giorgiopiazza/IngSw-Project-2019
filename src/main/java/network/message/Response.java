package network.message;

import enumerations.MessageContent;
import enumerations.MessageStatus;

public class Response extends Message {
    private final String message;
    private final MessageStatus status;

    public Response(String message, MessageStatus status) {
        super("GOD", MessageContent.RESPONSE);

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
