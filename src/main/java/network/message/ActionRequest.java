package network.message;

import model.player.PlayerPosition;

import java.util.ArrayList;

public abstract class ActionRequest extends Message {
    private final PlayerPosition senderMovePosition;
    private final ArrayList<Integer> powerupsID;

    public ActionRequest(int senderID, PlayerPosition senderMovePosition, ArrayList<Integer> powerupsID) {
        super(senderID);
        this.senderMovePosition = senderMovePosition;
        this.powerupsID = powerupsID;
    }

    public PlayerPosition getSenderMovePosition() {
        return senderMovePosition;
    }

    public ArrayList<Integer> getPowerupsID() {
        return powerupsID;
    }
}
