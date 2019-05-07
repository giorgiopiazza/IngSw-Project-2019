package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public abstract class ActionRequest extends Message {
    private final PlayerPosition senderMovePosition;
    private final ArrayList<Integer> paymentPowerups;


    ActionRequest(String username, MessageContent content, PlayerPosition senderMovePosition, ArrayList<Integer> paymentPowerupsID) {
        super(username, content);
        this.senderMovePosition = senderMovePosition;
        this.paymentPowerups = paymentPowerupsID;
    }

    public PlayerPosition getSenderMovePosition() {
        return senderMovePosition;
    }

    public ArrayList<Integer> getPaymentPowerups() {
        return paymentPowerups;
    }
}
