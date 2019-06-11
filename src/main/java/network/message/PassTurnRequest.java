package network.message;

import enumerations.MessageContent;

public class PassTurnRequest extends Message {
    private static final long serialVersionUID = 838173783902712501L;

    public PassTurnRequest(String username, String token) {
        super(username, token, MessageContent.PASS_TURN);
    }
}
