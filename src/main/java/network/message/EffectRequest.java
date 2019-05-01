package network.message;

import enumerations.Color;
import model.player.PlayerPosition;

import java.util.ArrayList;

public abstract class EffectRequest extends Message {
    private final ArrayList<Integer> targetPlayersID;
    private final ArrayList<PlayerPosition> targetPositions;
    private final Color targetRoomColor;

    private final PlayerPosition senderMovePosition;
    private final ArrayList<PlayerPosition> targetPlayersMovePositions;

    private final ArrayList<Integer> powerupsID;

    public EffectRequest(int senderID, ArrayList<Integer> targetPlayersID, ArrayList<PlayerPosition> targetPositions,
                         Color targetRoomColor, PlayerPosition senderMovePosition,
                         ArrayList<PlayerPosition> targetPlayersMovePositions, ArrayList<Integer> powerupsID) {
        super(senderID);

        this.targetPlayersID = targetPlayersID;
        this.targetPositions = targetPositions;
        this.targetRoomColor = targetRoomColor;
        this.senderMovePosition = senderMovePosition;
        this.targetPlayersMovePositions = targetPlayersMovePositions;
        this.powerupsID = powerupsID;
    }

    public ArrayList<Integer> getTargetPlayersID() {
        return targetPlayersID;
    }

    public ArrayList<PlayerPosition> getTargetPositions() {
        return targetPositions;
    }

    public Color getTargetRoomColor() {
        return targetRoomColor;
    }

    public PlayerPosition getSenderMovePosition() {
        return senderMovePosition;
    }

    public ArrayList<PlayerPosition> getTargetPlayersMovePositions() {
        return targetPlayersMovePositions;
    }

    public ArrayList<Integer> getPowerupsID() {
        return powerupsID;
    }
}
