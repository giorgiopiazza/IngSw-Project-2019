package network.message;

import enumerations.RoomColor;
import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class ShootRequest extends EffectRequest {
    private static final long serialVersionUID = -9183566520524697764L;

    private final int weaponID;
    private final int effect;

    private PlayerPosition adrenalineMovePosition;

    private final boolean moveSenderFirst;
    private final boolean moveTargetsFirst;

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
        this.adrenalineMovePosition = builder.addingMovePosition;
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

    public PlayerPosition getAdrenalineMovePosition() {
        return this.adrenalineMovePosition;
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

    public static class ShootRequestBuilder {
        private String username;
        private String token;
        private int weaponID;
        private int effect;

        private ArrayList<String> targetPlayersUsernames;
        private ArrayList<PlayerPosition> targetPositions;
        private RoomColor targetRoomColor;

        private PlayerPosition addingMovePosition;
        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private boolean moveSenderFirst;
        private boolean moveTargetsFirst;

        private ArrayList<Integer> paymentPowerups;
        private ArrayList<Integer> rechargingWeapons;

        public ShootRequestBuilder(String username, String token, int weaponID, int effect, ArrayList<Integer> rechargingWeapons) {
            this.username = username;
            this.token = token;
            this.weaponID = weaponID;
            this.effect = effect;
            this.rechargingWeapons = rechargingWeapons;
        }

        public ShootRequestBuilder targetPlayersUsernames(ArrayList<String> targetPlayersUsernames) {
            this.targetPlayersUsernames = targetPlayersUsernames;
            return this;
        }

        public ShootRequestBuilder targetPositions(ArrayList<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public ShootRequestBuilder targetRoomColor(RoomColor targetRoomColor) {
            this.targetRoomColor = targetRoomColor;
            return this;
        }

        public ShootRequestBuilder adrenalineMovePosition(PlayerPosition adrenalineMovePosition) {
            this.addingMovePosition = adrenalineMovePosition;
            return this;
        }

        public ShootRequestBuilder senderMovePosition(PlayerPosition senderMovePosition) {
            this.senderMovePosition = senderMovePosition;
            return this;
        }

        public ShootRequestBuilder targetPlayersMovePositions(ArrayList<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public ShootRequestBuilder moveSenderFirst(boolean moveSenderFirst) {
            this.moveSenderFirst = moveSenderFirst;
            return this;
        }

        public ShootRequestBuilder moveTargetsFirst(boolean moveTargetsFirst) {
            this.moveTargetsFirst = moveTargetsFirst;
            return this;
        }

        public ShootRequestBuilder paymentPowerups(ArrayList<Integer> paymentPowerups) {
            this.paymentPowerups = paymentPowerups;
            return this;
        }

        public ShootRequestBuilder rechargingWeapons(ArrayList<Integer> rechargingWeapons) {
            this.rechargingWeapons = rechargingWeapons;
            return this;
        }

        public ShootRequest build() {
            return new ShootRequest(this);
        }
    }
}
