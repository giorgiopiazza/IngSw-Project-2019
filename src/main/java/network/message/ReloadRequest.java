package network.message;

import enumerations.MessageContent;

import java.util.ArrayList;
import java.util.Objects;

public class ReloadRequest extends Message {
    private final ArrayList<Integer> weapons;
    private final ArrayList<Integer> paymentPowerups;

    public ReloadRequest(String username, ArrayList<Integer> weapons, ArrayList<Integer> paymentPowerups) {
        super(username, MessageContent.RELOAD);

        this.weapons = Objects.requireNonNullElse(weapons, new ArrayList<>());
        this.paymentPowerups = Objects.requireNonNullElse(paymentPowerups, new ArrayList<>());
    }

    public ArrayList<Integer> getWeapons() {
        return weapons;
    }

    public ArrayList<Integer> getPaymentPowerups() {
        return paymentPowerups;
    }
}
