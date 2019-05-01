package network.message;

import enumerations.Ammo;
import enumerations.Color;
import model.player.PlayerPosition;

import java.util.ArrayList;

public class PowerupRequest extends EffectRequest {
    public final int powerupID;
    public final Ammo ammoColor;

    public PowerupRequest(PowerupRequestBuilder builder) {
        super(builder.senderID, builder.targetPlayersID, builder.targetPositions,
                builder.targetRoomColor, builder.senderMovePosition, builder.targetPlayersMovePositions, builder.powerupsID);

        this.powerupID = builder.powerupID;
        this.ammoColor = builder.ammoColor;
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

        private ArrayList<Integer> powerupsID;

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

        public PowerupRequestBuilder powerupsID(ArrayList<Integer> powerupsID) {
            this.powerupsID = powerupsID;
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
