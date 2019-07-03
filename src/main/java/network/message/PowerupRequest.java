package network.message;

import enumerations.Ammo;
import enumerations.RoomColor;
import enumerations.MessageContent;
import model.player.PlayerPosition;
import utility.NullObjectHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PowerupRequest extends EffectRequest {
    private static final long serialVersionUID = 8674157231024320484L;

    private final ArrayList<Integer> powerup;
    private final ArrayList<Ammo> ammoColor;

    public PowerupRequest(PowerupRequestBuilder builder) {
        super(
                new EffectRequestBuilder(builder.username, builder.token, MessageContent.POWERUP_USAGE)
                        .targetPlayersUsernames(builder.targetPlayersUsername)
                        .targetPositions(builder.targetPositions)
                        .targetRoomColor(builder.targetRoomColor)
                        .senderMovePosition(builder.senderMovePosition)
                        .targetPlayersMovePositions(builder.targetPlayersMovePositions)
                        .paymentPowerups(builder.paymentPowerups)
        );

        this.powerup = NullObjectHelper.getNotNullArrayList(builder.powerup);
        this.ammoColor = NullObjectHelper.getNotNullArrayList(builder.ammoColor);
    }

    public List<Integer> getPowerup() {
        return powerup;
    }

    public List<Ammo> getAmmoColor() {
        return ammoColor;
    }

    public static class PowerupRequestBuilder {
        private String username;
        private String token;
        private List<Integer> powerup;
        private List<Ammo> ammoColor;

        private List<String> targetPlayersUsername;
        private List<PlayerPosition> targetPositions;
        private RoomColor targetRoomColor;

        private PlayerPosition senderMovePosition;
        private List<PlayerPosition> targetPlayersMovePositions;

        private List<Integer> paymentPowerups;

        public PowerupRequestBuilder(String username, String token, List<Integer> powerup) {
            this.username = username;
            this.token = token;
            this.powerup = powerup;
        }

        public PowerupRequestBuilder targetPlayersUsername(List<String> targetPlayersUsername) {
            this.targetPlayersUsername = targetPlayersUsername;
            return this;
        }

        public PowerupRequestBuilder targetPositions(List<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public PowerupRequestBuilder targetRoomColor(RoomColor targetRoomColor) {
            this.targetRoomColor = targetRoomColor;
            return this;
        }

        public PowerupRequestBuilder senderMovePosition(PlayerPosition senderMovePosition) {
            this.senderMovePosition = senderMovePosition;
            return this;
        }

        public PowerupRequestBuilder targetPlayersMovePositions(List<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public PowerupRequestBuilder paymentPowerups(List<Integer> paymentPowerups) {
            this.paymentPowerups = paymentPowerups;
            return this;
        }

        public PowerupRequestBuilder ammoColor(List<Ammo> ammoColor) {
            this.ammoColor = ammoColor;
            return this;
        }

        public PowerupRequest build() {
            return new PowerupRequest(this);
        }
    }

    @Override
    public String toString() {
        return "PowerupRequest{" +
                "senderUsername=" + getSenderUsername() +
                ",content=" + getContent() +
                ", senderMovePosition=" + getSenderMovePosition() +
                ", paymentPowerups=" + (getPaymentPowerups() == null ? "null" : Arrays.toString(getPaymentPowerups().toArray())) +
                ",targetPlayersUsername=" + (getTargetPlayersUsername() == null ? "null" : Arrays.toString(getTargetPlayersUsername().toArray())) +
                ", targetPlayersMovePositions=" + (getTargetPlayersMovePositions() == null ? "null" : Arrays.toString(getTargetPlayersMovePositions().toArray())) +
                ", targetPositions=" + (getTargetPositions() == null ? "null" : Arrays.toString(getTargetPositions().toArray())) +
                ", targetRoomColor=" + getTargetRoomColor() +
                ", powerup=" + (powerup == null ? "null" : Arrays.toString(powerup.toArray())) +
                ", ammoColor=" + ammoColor +
                '}';
    }
}
