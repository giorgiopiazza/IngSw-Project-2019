package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class MovePickRequest extends ActionRequest {
    public MovePickRequest(int senderID, PlayerPosition senderMovePosition, ArrayList<Integer> paymentPowerupsID) {
        super(senderID, MessageContent.MOVE_PICK, senderMovePosition, paymentPowerupsID);
    }
}
