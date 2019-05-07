package network.message;

import enumerations.MessageContent;

public class PassTurnRequest extends Message {
    public PassTurnRequest(String username) {
        super(username, MessageContent.PASS_TURN);
    }
}
