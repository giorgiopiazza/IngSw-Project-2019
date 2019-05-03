package network.message;

import enumerations.Color;
import model.player.PlayerPosition;

import java.util.ArrayList;

public abstract class EffectRequest extends ActionRequest {
    private final ArrayList<Integer> targetPlayersID;
    private final ArrayList<PlayerPosition> targetPositions;
    private final Color targetRoomColor;

    private final ArrayList<PlayerPosition> targetPlayersMovePositions;

    public EffectRequest(int senderID, ArrayList<Integer> targetPlayersID, ArrayList<PlayerPosition> targetPositions,
                         Color targetRoomColor, PlayerPosition senderMovePosition,
                         ArrayList<PlayerPosition> targetPlayersMovePositions, ArrayList<Integer> powerupsID) {
        super(senderID, senderMovePosition, powerupsID);

        this.targetPlayersID = targetPlayersID;
        this.targetPositions = targetPositions;
        this.targetRoomColor = targetRoomColor;
        this.targetPlayersMovePositions = targetPlayersMovePositions;
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

    public ArrayList<PlayerPosition> getTargetPlayersMovePositions() {
        return targetPlayersMovePositions;
    }
}
