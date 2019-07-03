package network.message;

import enumerations.MessageContent;
import utility.NullObjectHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadRequest extends ActionRequest {
    private static final long serialVersionUID = -2207568671961041647L;

    private final ArrayList<Integer> weapons;

    public ReloadRequest(String username, String token, List<Integer> weapons, List<Integer> paymentPowerups) {
        super(username, token, MessageContent.RELOAD, null, paymentPowerups);

        this.weapons = NullObjectHelper.getNotNullArrayList(weapons);
    }

    public List<Integer> getWeapons() {
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
