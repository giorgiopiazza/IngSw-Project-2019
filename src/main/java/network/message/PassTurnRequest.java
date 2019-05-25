package network.message;

import enumerations.MessageContent;

public class PassTurnRequest extends Message {
    public PassTurnRequest(String username,  String token) {
        super(username, token, MessageContent.PASS_TURN);
    }
}
