package network.message;

import enumerations.Ammo;
import enumerations.Color;
import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class PowerupRequest extends EffectRequest {
    private final int powerupID;
    private final Ammo ammoColor;

    public PowerupRequest(PowerupRequestBuilder builder) {
        super(
                new EffectRequestBuilder(builder.username, MessageContent.POWERUP)
                        .targetPlayersUsernames(builder.targetPlayersUsernames)
                        .targetPositions(builder.targetPositions)
                        .targetRoomColor(builder.targetRoomColor)
                        .senderMovePosition(builder.senderMovePosition)
                        .targetPlayersMovePositions(builder.targetPlayersMovePositions)
                        .paymentPowerups(builder.paymentPowerups)
        );

        this.powerupID = builder.powerupID;
        this.ammoColor = builder.ammoColor;
    }

    public int getPowerupID() {
        return powerupID;
    }

    public Ammo getAmmoColor() {
        return ammoColor;
    }

    public static class PowerupRequestBuilder {
        private String username;
        private int powerupID;
        private Ammo ammoColor;

        private ArrayList<String> targetPlayersUsernames;
        private ArrayList<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private ArrayList<Integer> paymentPowerups;

        public PowerupRequestBuilder(String username, int powerupID) {
            this.username = username;
            this.powerupID = powerupID;
        }

        public PowerupRequestBuilder targetPlayersID(ArrayList<String> targetPlayersUsernames) {
            this.targetPlayersUsernames = targetPlayersUsernames;
            return this;
        }

        public PowerupRequestBuilder targetPositions(ArrayList<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public PowerupRequestBuilder targetRoomColor(Color targetRoomColor) {
            this.targetRoomColor = targetRoomColor;
            return this;
        }

        public PowerupRequestBuilder senderMovePosition(PlayerPosition senderMovePosition) {
            this.senderMovePosition = senderMovePosition;
            return this;
        }

        public PowerupRequestBuilder targetPlayersMovePositions(ArrayList<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public PowerupRequestBuilder paymentPowerups(ArrayList<Integer> paymentPowerups) {
            this.paymentPowerups = paymentPowerups;
            return this;
        }

        public PowerupRequestBuilder ammoColor(Ammo ammoColor) {
            this.ammoColor = ammoColor;
            return this;
        }

        public PowerupRequest build() {
            return new PowerupRequest(this);
        }
    }
}
