package network.message;

import enumerations.MessageContent;

import java.util.ArrayList;

public class ReloadRequest extends Message {
    private final int weaponID;
    private final ArrayList<Integer> paymentPowerups;

    public ReloadRequest(String username, int weaponID, ArrayList<Integer> paymentPowerups) {
        super(username, MessageContent.RELOAD);

        this.weaponID = weaponID;
        this.paymentPowerups = paymentPowerups;
    }

    public int getWeaponID() {
        return weaponID;
    }

    public ArrayList<Integer> getPaymentPowerups() {
        return paymentPowerups;
    }
}
