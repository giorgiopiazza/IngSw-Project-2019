package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public abstract class ActionRequest extends Message {
    private final PlayerPosition senderMovePosition;
    private final ArrayList<Integer> paymentPowerupsID;


    ActionRequest(int senderID, MessageContent content, PlayerPosition senderMovePosition, ArrayList<Integer> paymentPowerupsID) {
        super(senderID, content);
        this.senderMovePosition = senderMovePosition;
        this.paymentPowerupsID = paymentPowerupsID;
    }

    public PlayerPosition getSenderMovePosition() {
        return senderMovePosition;
    }

    public ArrayList<Integer> getPaymentPowerupsID() {
        return paymentPowerupsID;
    }
}
