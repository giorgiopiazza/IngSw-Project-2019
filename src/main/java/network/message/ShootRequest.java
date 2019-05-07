package network.message;

import enumerations.Color;
import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class ShootRequest extends EffectRequest {
    private final int weaponID;
    private final int effect;

    private final boolean moveSenderFirst;
    private final boolean moveTargetsFirst;

    public ShootRequest(FireRequestBuilder builder) {
        super(
                new EffectRequestBuilder(builder.username, MessageContent.SHOOT)
                        .targetPlayersUsernames(builder.targetPlayersUsernames)
                        .targetPositions(builder.targetPositions)
                        .targetRoomColor(builder.targetRoomColor)
                        .senderMovePosition(builder.senderMovePosition)
                        .targetPlayersMovePositions(builder.targetPlayersMovePositions)
                        .paymentPowerups(builder.paymentPowerups)
        );

        this.weaponID = builder.weaponID;
        this.effect = builder.effect;
        this.moveSenderFirst = builder.moveSenderFirst;
        this.moveTargetsFirst = builder.moveTargetsFirst;
    }

    public int getWeaponID() {
        return weaponID;
    }

    public int getEffect() {
        return effect;
    }

    public boolean isMoveSenderFirst() {
        return moveSenderFirst;
    }

    public boolean isMoveTargetsFirst() {
        return moveTargetsFirst;
    }

    public static class FireRequestBuilder {
        private String username;
        private int weaponID;
        private int effect;

        private ArrayList<String> targetPlayersUsernames;
        private ArrayList<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private boolean moveSenderFirst;
        private boolean moveTargetsFirst;

        private ArrayList<Integer> paymentPowerups;

        public FireRequestBuilder(String username, int weaponID, int effect) {
            this.username = username;
            this.weaponID = weaponID;
            this.effect = effect;
        }

        public FireRequestBuilder targetPlayersUsernames(ArrayList<String> targetPlayersUsernames) {
            this.targetPlayersUsernames = targetPlayersUsernames;
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

        public FireRequestBuilder paymentPowerups(ArrayList<Integer> paymentPowerups) {
            this.paymentPowerups = paymentPowerups;
            return this;
        }

        public ShootRequest build() {
            return new ShootRequest(this);
        }
    }
}
