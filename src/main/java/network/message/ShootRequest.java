package network.message;

import enumerations.RoomColor;
import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class ShootRequest extends EffectRequest {
    private static final long serialVersionUID = -9183566520524697764L;

    private final int weaponID;
    private final int effect;

    private final boolean moveSenderFirst;
    private final boolean moveTargetsFirst;

    private ArrayList<Integer> rechargingWeapons;

    public ShootRequest(FireRequestBuilder builder) {
        super(
                new EffectRequestBuilder(builder.username, builder.token, MessageContent.SHOOT)
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
        this.rechargingWeapons = builder.rechargingWeapons;
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

    public ArrayList<Integer> getRechargingWeapons() {
        return rechargingWeapons;
    }

    public static class FireRequestBuilder {
        private String username;
        private String token;
        private int weaponID;
        private int effect;

        private ArrayList<String> targetPlayersUsernames;
        private ArrayList<PlayerPosition> targetPositions;
        private RoomColor targetRoomColor;

        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private boolean moveSenderFirst;
        private boolean moveTargetsFirst;

        private ArrayList<Integer> paymentPowerups;
        private ArrayList<Integer> rechargingWeapons;

        public FireRequestBuilder(String username, String token, int weaponID, int effect, ArrayList<Integer> rechargingWeapons) {
            this.username = username;
            this.token = token;
            this.weaponID = weaponID;
            this.effect = effect;
            this.rechargingWeapons = rechargingWeapons;
        }

        public FireRequestBuilder targetPlayersUsernames(ArrayList<String> targetPlayersUsernames) {
            this.targetPlayersUsernames = targetPlayersUsernames;
            return this;
        }

        public FireRequestBuilder targetPositions(ArrayList<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public FireRequestBuilder targetRoomColor(RoomColor targetRoomColor) {
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
