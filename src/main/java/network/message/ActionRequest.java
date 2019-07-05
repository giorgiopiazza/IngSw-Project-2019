package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;
import utility.NullObjectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class of message request for requesting the execution of an action
 */
public abstract class ActionRequest extends Message {
    private static final long serialVersionUID = 9032466429818166290L;

    private final PlayerPosition senderMovePosition;
    private final ArrayList<Integer> paymentPowerups;

    ActionRequest(String username, String token, MessageContent content, PlayerPosition senderMovePosition, List<Integer> paymentPowerups) {
        super(username, token, content);
        this.senderMovePosition = senderMovePosition;
        this.paymentPowerups = NullObjectHelper.getNotNullArrayList(paymentPowerups);
    }

    public PlayerPosition getSenderMovePosition() {
        return senderMovePosition;
    }

    public List<Integer> getPaymentPowerups() {
        if (paymentPowerups == null) return new ArrayList<>();
        else return paymentPowerups;
    }
}
