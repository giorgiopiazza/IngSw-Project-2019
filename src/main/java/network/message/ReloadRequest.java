package network.message;

import enumerations.MessageContent;

import java.util.ArrayList;
import java.util.Objects;

public class ReloadRequest extends ActionRequest {
    private final ArrayList<Integer> weapons;

    public ReloadRequest(String username, String token, ArrayList<Integer> weapons, ArrayList<Integer> paymentPowerups) {
        super(username, token, MessageContent.RELOAD, null, paymentPowerups);

        this.weapons = Objects.requireNonNullElse(weapons, new ArrayList<>());
    }

    public ArrayList<Integer> getWeapons() {
        return weapons;
    }
}
