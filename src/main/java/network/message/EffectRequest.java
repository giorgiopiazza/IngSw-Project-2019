package network.message;

import enumerations.Color;
import model.player.PlayerPosition;

import java.util.List;

public class EffectRequest extends Message {
    public final List<Integer> targetPlayersID;
    public final List<PlayerPosition> targetPositions;
    public final Color targetRoomColor;

    public final PlayerPosition senderMovePosition;
    public final List<PlayerPosition> targetPlayersMovePositions;

    public final List<Integer> powerupsID;

    public EffectRequest(int senderID, List<Integer> targetPlayersID, List<PlayerPosition> targetPositions,
                         Color targetRoomColor, PlayerPosition senderMovePosition,
                         List<PlayerPosition> targetPlayersMovePositions, List<Integer> powerupsID) {
        super(senderID);

        this.targetPlayersID = targetPlayersID;
        this.targetPositions = targetPositions;
        this.targetRoomColor = targetRoomColor;
        this.senderMovePosition = senderMovePosition;
        this.targetPlayersMovePositions = targetPlayersMovePositions;
        this.powerupsID = powerupsID;
    }
}
