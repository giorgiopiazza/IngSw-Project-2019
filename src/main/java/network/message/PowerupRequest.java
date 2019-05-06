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
                new EffectRequestBuilder(builder.senderID, MessageContent.POWERUP)
                        .targetPlayersID(builder.targetPlayersID)
                        .targetPositions(builder.targetPositions)
                        .targetRoomColor(builder.targetRoomColor)
                        .senderMovePosition(builder.senderMovePosition)
                        .targetPlayersMovePositions(builder.targetPlayersMovePositions)
                        .paymentPowerupsID(builder.paymentPowerupsID)
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
        private int senderID;
        private int powerupID;
        private Ammo ammoColor;

        private ArrayList<Integer> targetPlayersID;
        private ArrayList<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private ArrayList<Integer> paymentPowerupsID;

        public PowerupRequestBuilder(int senderID, int powerupID) {
            this.senderID = senderID;
            this.powerupID = powerupID;
        }

        public PowerupRequestBuilder targetPlayersID(ArrayList<Integer> targetPlayersID) {
            this.targetPlayersID = targetPlayersID;
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

        public PowerupRequestBuilder paymentPowerupsID(ArrayList<Integer> paymentPowerupsID) {
            this.paymentPowerupsID = paymentPowerupsID;
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
