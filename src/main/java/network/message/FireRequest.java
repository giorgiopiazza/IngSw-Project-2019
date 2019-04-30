package network.message;

import enumerations.Color;
import model.player.PlayerPosition;

import java.util.List;

public class FireRequest extends EffectRequest {
    public final int weaponID;
    public final int effectID;

    public final boolean moveSenderFirst;
    public final boolean moveTargetsFirst;

    public FireRequest(FireRequestBuilder builder) {
        super(builder.senderID, builder.targetPlayersID, builder.targetPositions,
                builder.targetRoomColor, builder.senderMovePosition, builder.targetPlayersMovePositions, builder.powerupsID);

        this.weaponID = builder.weaponID;
        this.effectID = builder.effectID;
        this.moveSenderFirst = builder.moveSenderFirst;
        this.moveTargetsFirst = builder.moveTargetsFirst;
    }

    public static class FireRequestBuilder {
        private int senderID;
        private int weaponID;
        private int effectID;

        private List<Integer> targetPlayersID;
        private List<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private List<PlayerPosition> targetPlayersMovePositions;

        private boolean moveSenderFirst;
        private boolean moveTargetsFirst;

        private List<Integer> powerupsID;

        public FireRequestBuilder(int senderID, int weaponID, int effectID) {
            this.senderID = senderID;
            this.weaponID = weaponID;
            this.effectID = effectID;
        }

        public FireRequestBuilder targetPlayersID(List<Integer> targetPlayersID) {
            this.targetPlayersID = targetPlayersID;
            return this;
        }

        public FireRequestBuilder targetPositions(List<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public FireRequestBuilder targetRoomColor(Color targetRoomColor) {
            this.targetRoomColor = targetRoomColor;
            return this;
        }

        public FireRequestBuilder senderMovePosition(PlayerPosition senderMovePosition) {
            this.senderMovePosition = senderMovePosition;
            return this;
        }

        public FireRequestBuilder targetPlayersMovePositions(List<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public FireRequestBuilder moveSenderFirst(boolean moveSenderFirst) {
            this.moveSenderFirst = moveSenderFirst;
            return this;
        }

        public FireRequestBuilder moveTargetsFirst(boolean moveTargetsFirst) {
            this.moveTargetsFirst = moveTargetsFirst;
            return this;
        }

        public FireRequestBuilder powerupsID(List<Integer> powerupsID) {
            this.powerupsID = powerupsID;
            return this;
        }

        public FireRequest build() {
            return new FireRequest(this);
        }
    }
}
