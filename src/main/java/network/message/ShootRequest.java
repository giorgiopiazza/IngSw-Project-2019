package network.message;

import enumerations.Color;
import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class ShootRequest extends EffectRequest {
    private final int weaponID;
    private final int effectID;

    private final boolean moveSenderFirst;
    private final boolean moveTargetsFirst;

    public ShootRequest(FireRequestBuilder builder) {
        super(
                new EffectRequestBuilder(builder.senderID, MessageContent.SHOOT)
                        .targetPlayersID(builder.targetPlayersID)
                        .targetPositions(builder.targetPositions)
                        .targetRoomColor(builder.targetRoomColor)
                        .senderMovePosition(builder.senderMovePosition)
                        .targetPlayersMovePositions(builder.targetPlayersMovePositions)
                        .paymentPowerupsID(builder.paymentPowerupsID)
        );

        this.weaponID = builder.weaponID;
        this.effectID = builder.effectID;
        this.moveSenderFirst = builder.moveSenderFirst;
        this.moveTargetsFirst = builder.moveTargetsFirst;
    }

    public int getWeaponID() {
        return weaponID;
    }

    public int getEffectID() {
        return effectID;
    }

    public boolean isMoveSenderFirst() {
        return moveSenderFirst;
    }

    public boolean isMoveTargetsFirst() {
        return moveTargetsFirst;
    }

    public static class FireRequestBuilder {
        private int senderID;
        private int weaponID;
        private int effectID;

        private ArrayList<Integer> targetPlayersID;
        private ArrayList<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private boolean moveSenderFirst;
        private boolean moveTargetsFirst;

        private ArrayList<Integer> paymentPowerupsID;

        public FireRequestBuilder(int senderID, int weaponID, int effectID) {
            this.senderID = senderID;
            this.weaponID = weaponID;
            this.effectID = effectID;
        }

        public FireRequestBuilder targetPlayersID(ArrayList<Integer> targetPlayersID) {
            this.targetPlayersID = targetPlayersID;
            return this;
        }

        public FireRequestBuilder targetPositions(ArrayList<PlayerPosition> targetPositions) {
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

        public FireRequestBuilder targetPlayersMovePositions(ArrayList<PlayerPosition> targetPlayersMovePositions) {
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

        public FireRequestBuilder paymentPowerupsID(ArrayList<Integer> paymentPowerupsID) {
            this.paymentPowerupsID = paymentPowerupsID;
            return this;
        }

        public ShootRequest build() {
            return new ShootRequest(this);
        }
    }
}
