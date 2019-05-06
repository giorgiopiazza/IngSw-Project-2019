package network.message;

import enumerations.MessageContent;

import java.util.ArrayList;

public class ReloadRequest extends Message {
    private final int weaponID;
    private final ArrayList<Integer> paymentPowerupsID;

    public ReloadRequest(int senderID, int weaponID, ArrayList<Integer> paymentPowerupsID) {
        super(senderID, MessageContent.RELOAD);

        this.weaponID = weaponID;
        this.paymentPowerupsID = paymentPowerupsID;
    }
}
