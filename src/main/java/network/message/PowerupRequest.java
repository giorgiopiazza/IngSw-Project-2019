package network.message;

import enumerations.Ammo;
import enumerations.Color;
import model.player.PlayerPosition;

import java.util.List;

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

        private List<Integer> targetPlayersID;
        private List<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private List<PlayerPosition> targetPlayersMovePositions;

        private List<Integer> powerupsID;

        public PowerupRequestBuilder(int senderID, int powerupID) {
            this.senderID = senderID;
            this.powerupID = powerupID;
        }

        public PowerupRequestBuilder targetPlayersID(List<Integer> targetPlayersID) {
            this.targetPlayersID = targetPlayersID;
            return this;
        }

        public PowerupRequestBuilder targetPositions(List<PlayerPosition> targetPositions) {
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

        public PowerupRequestBuilder targetPlayersMovePositions(List<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public PowerupRequestBuilder powerupsID(List<Integer> powerupsID) {
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
