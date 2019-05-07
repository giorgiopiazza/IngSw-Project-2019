package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

public class MoveRequest extends ActionRequest {
    public MoveRequest(String username, PlayerPosition senderMovePosition) {
        super(username, MessageContent.MOVE, senderMovePosition, null);
    }
}
