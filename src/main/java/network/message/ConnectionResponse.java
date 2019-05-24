package network.message;

import enumerations.MessageStatus;

public class ConnectionResponse extends Response {
    private final String newToken;

    public ConnectionResponse(String message, String token, MessageStatus status) {
        super(message, status);
        this.newToken = token;
    }

    public String getNewToken() {
        return newToken;
    }
}
