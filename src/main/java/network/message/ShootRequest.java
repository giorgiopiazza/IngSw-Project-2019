package network.message;

import enumerations.RoomColor;
import enumerations.MessageContent;
import model.player.PlayerPosition;
import utility.NullObjectHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShootRequest extends EffectRequest {
    private static final long serialVersionUID = -9183566520524697764L;

    private final int weaponID;
    private final int effect;

    private PlayerPosition moveBeforeShootPosition;

    private final boolean moveSenderFirst;
    private final boolean moveInMiddle;
    private final boolean moveTargetsFirst;
    private final boolean moveToLastTarget;

    private ArrayList<Integer> rechargingWeapons;

    public ShootRequest(ShootRequestBuilder builder) {
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
        this.moveBeforeShootPosition = builder.moveBeforeShootPosition;
        this.moveSenderFirst = builder.moveSenderFirst;
        this.moveInMiddle = builder.moveInMiddle;
        this.moveTargetsFirst = builder.moveTargetsFirst;
        this.moveToLastTarget = builder.moveToLastTarget;
        this.rechargingWeapons = NullObjectHelper.getNotNullArrayList(builder.rechargingWeapons);
    }

    public int getWeaponID() {
        return weaponID;
    }

    public int getEffect() {
        return effect;
    }

    public PlayerPosition getMoveBeforeShootPosition() {
        return this.moveBeforeShootPosition;
    }

    public boolean isMoveSenderFirst() {
        return moveSenderFirst;
    }

    public boolean isMoveInMiddle() {
        return moveInMiddle;
    }

    public boolean isMoveTargetsFirst() {
        return moveTargetsFirst;
    }

    public boolean isMoveToLastTarget() {
        return moveToLastTarget;
    }

    public List<Integer> getRechargingWeapons() {
        return rechargingWeapons;
    }

    public static class ShootRequestBuilder {
        private String username;
        private String token;
        private int weaponID;
        private int effect;

        private List<String> targetPlayersUsernames;
        private List<PlayerPosition> targetPositions;
        private RoomColor targetRoomColor;

        private PlayerPosition moveBeforeShootPosition;
        private PlayerPosition senderMovePosition;
        private List<PlayerPosition> targetPlayersMovePositions;

        private boolean moveSenderFirst;
        private boolean moveInMiddle;
        private boolean moveTargetsFirst;
        private boolean moveToLastTarget;

        private List<Integer> paymentPowerups;
        private List<Integer> rechargingWeapons;

        public ShootRequestBuilder(String username, String token, int weaponID, int effect) {
            this.username = username;
            this.token = token;
            this.weaponID = weaponID;
            this.effect = effect;
        }

        public ShootRequestBuilder targetPlayersUsernames(List<String> targetPlayersUsernames) {
            this.targetPlayersUsernames = targetPlayersUsernames;
            return this;
        }

        public ShootRequestBuilder targetPositions(List<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public ShootRequestBuilder targetRoomColor(RoomColor targetRoomColor) {
            this.targetRoomColor = targetRoomColor;
            return this;
        }

        public ShootRequestBuilder moveBeforeShootPosition(PlayerPosition moveBeforeShootPosition) {
            this.moveBeforeShootPosition = moveBeforeShootPosition;
            return this;
        }

        public ShootRequestBuilder senderMovePosition(PlayerPosition senderMovePosition) {
            this.senderMovePosition = senderMovePosition;
            return this;
        }

        public ShootRequestBuilder targetPlayersMovePositions(List<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public ShootRequestBuilder moveSenderFirst(boolean moveSenderFirst) {
            this.moveSenderFirst = moveSenderFirst;
            return this;
        }

        public ShootRequestBuilder moveInMiddle(boolean moveInMiddle) {
            this.moveInMiddle = moveInMiddle;
            return this;
        }

        public ShootRequestBuilder moveTargetsFirst(boolean moveTargetsFirst) {
            this.moveTargetsFirst = moveTargetsFirst;
            return this;
        }

        public ShootRequestBuilder moveToLastTarget(boolean moveToLastTarget) {
            this.moveToLastTarget = moveToLastTarget;
            return this;
        }

        public ShootRequestBuilder paymentPowerups(List<Integer> paymentPowerups) {
            this.paymentPowerups = paymentPowerups;
            return this;
        }

        public ShootRequestBuilder rechargingWeapons(List<Integer> rechargingWeapons) {
            this.rechargingWeapons = rechargingWeapons;
            return this;
        }

        public ShootRequest build() {
            return new ShootRequest(this);
        }
    }

    @Override
    public String toString() {
        return "ShootRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                ", senderMovePosition=" + getSenderMovePosition() +
                ", paymentPowerups=" + (getPaymentPowerups() == null ? "null" : Arrays.toString(getPaymentPowerups().toArray())) +
                ", targetPlayersUsername=" + (getTargetPlayersUsername() == null ? "null" : Arrays.toString(getTargetPlayersUsername().toArray())) +
                ", targetPlayersMovePositions=" + (getTargetPlayersMovePositions() == null ? "null" : Arrays.toString(getTargetPlayersMovePositions().toArray())) +
                ", targetPositions=" + (getTargetPositions() == null ? "null" : Arrays.toString(getTargetPositions().toArray())) +
                ", targetRoomColor=" + getTargetRoomColor() +
                ", weaponID=" + weaponID +
                ", effect=" + effect +
                ", moveBeforeShootPosition=" + moveBeforeShootPosition +
                ", moveSenderFirst=" + moveSenderFirst +
                ", moveTargetsFirst=" + moveTargetsFirst +
                ", rechargingWeapons=" + (rechargingWeapons == null ? "null" : Arrays.toString(rechargingWeapons.toArray())) +
                '}';
    }
}
