package network.message;

import enumerations.MessageContent;

/**
 * Message class for requesting a pass turn
 */
public class PassTurnRequest extends Message {
    private static final long serialVersionUID = 838173783902712501L;

    public PassTurnRequest(String username, String token) {
        super(username, token, MessageContent.PASS_TURN);
    }

    @Override
    public String toString() {
        return "PassTurnRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                "}";
    }
}
