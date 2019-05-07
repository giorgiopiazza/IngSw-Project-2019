package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class MovePickRequest extends ActionRequest {
    public MovePickRequest(String username, PlayerPosition senderMovePosition, ArrayList<Integer> paymentPowerups) {
        super(username, MessageContent.MOVE_PICK, senderMovePosition, paymentPowerups);
    }
}
