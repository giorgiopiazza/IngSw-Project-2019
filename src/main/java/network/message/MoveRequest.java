package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.Arrays;

public class MoveRequest extends ActionRequest {
    private static final long serialVersionUID = 7410856239418653990L;

    public MoveRequest(String username, String token, PlayerPosition senderMovePosition) {
        super(username, token, MessageContent.MOVE, senderMovePosition, null);
    }

    @Override
    public String toString() {
        return "MoveRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() + ", " +
                ", senderMovePosition=" + getSenderMovePosition() +
                ", paymentPowerups=" + (getPaymentPowerups() == null ? "null" : Arrays.toString(getPaymentPowerups().toArray())) +
                "}";
    }
}
