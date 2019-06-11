package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;
import java.util.Objects;

public abstract class ActionRequest extends Message {
    private static final long serialVersionUID = 9032466429818166290L;

    private final PlayerPosition senderMovePosition;
    private final ArrayList<Integer> paymentPowerups;

    public ActionRequest(String username, String token, MessageContent content, PlayerPosition senderMovePosition, ArrayList<Integer> paymentPowerups) {
        super(username, token, content);
        this.senderMovePosition = senderMovePosition;
        this.paymentPowerups = Objects.requireNonNullElse(paymentPowerups, new ArrayList<>());
    }

    public PlayerPosition getSenderMovePosition() {
        return senderMovePosition;
    }

    public ArrayList<Integer> getPaymentPowerups() {
        if(paymentPowerups == null) return new ArrayList<>();
        else return paymentPowerups;
    }
}
