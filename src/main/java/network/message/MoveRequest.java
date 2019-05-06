package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

public class MoveRequest extends ActionRequest {
    public MoveRequest(int senderID, PlayerPosition senderMovePosition) {
        super(senderID, MessageContent.MOVE, senderMovePosition, null);
    }
}
