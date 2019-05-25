package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

public class MoveRequest extends ActionRequest {
    public MoveRequest(String username, String token, PlayerPosition senderMovePosition) {
        super(username, token, MessageContent.MOVE, senderMovePosition, null);
    }
}
