package network.message;

import enumerations.MessageContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ReloadRequest extends ActionRequest {
    private static final long serialVersionUID = -2207568671961041647L;

    private final ArrayList<Integer> weapons;

    public ReloadRequest(String username, String token, ArrayList<Integer> weapons, ArrayList<Integer> paymentPowerups) {
        super(username, token, MessageContent.RELOAD, null, paymentPowerups);

        this.weapons = Objects.requireNonNullElse(weapons, new ArrayList<>());
    }

    public ArrayList<Integer> getWeapons() {
        return weapons;
    }

    @Override
    public String toString() {
        return "ReloadRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                ", senderMovePosition=" + getSenderMovePosition() +
                ", paymentPowerups=" + (getPaymentPowerups() == null ? "null" : Arrays.toString(getPaymentPowerups().toArray())) +
                ", weapons=" + (weapons == null ? "null" : Arrays.toString(weapons.toArray())) +
                '}';
    }
}
