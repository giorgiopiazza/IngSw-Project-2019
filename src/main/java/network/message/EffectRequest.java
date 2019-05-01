package network.message;

import enumerations.Color;
import model.player.PlayerPosition;

import java.util.ArrayList;

public abstract class EffectRequest extends Message {
    public final ArrayList<Integer> targetPlayersID;
    public final ArrayList<PlayerPosition> targetPositions;
    public final Color targetRoomColor;

    public final PlayerPosition senderMovePosition;
    public final ArrayList<PlayerPosition> targetPlayersMovePositions;

    public final ArrayList<Integer> powerupsID;

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
}
