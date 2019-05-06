package network.message;

import enumerations.Color;
import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;

public abstract class EffectRequest extends ActionRequest {
    private final ArrayList<Integer> targetPlayersID;
    private final ArrayList<PlayerPosition> targetPositions;
    private final Color targetRoomColor;
    private final ArrayList<PlayerPosition> targetPlayersMovePositions;

    public EffectRequest(EffectRequestBuilder builder) {
        super(builder.senderID, builder.content, builder.senderMovePosition, builder.paymentPowerupsID);

        this.targetPlayersID = builder.targetPlayersID;
        this.targetPositions = builder.targetPositions;
        this.targetRoomColor = builder.targetRoomColor;
        this.targetPlayersMovePositions = builder.targetPlayersMovePositions;
    }

    public ArrayList<Integer> getTargetPlayersID() {
        return targetPlayersID;
    }

    public ArrayList<PlayerPosition> getTargetPositions() {
        return targetPositions;
    }

    public Color getTargetRoomColor() {
        return targetRoomColor;
    }

    public ArrayList<PlayerPosition> getTargetPlayersMovePositions() {
        return targetPlayersMovePositions;
    }

    public static class EffectRequestBuilder {
        private int senderID;
        private MessageContent content;

        private ArrayList<Integer> targetPlayersID;
        private ArrayList<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private ArrayList<Integer> paymentPowerupsID;

        public EffectRequestBuilder(int senderID, MessageContent content) {
            this.senderID = senderID;
            this.content = content;
        }

        public EffectRequestBuilder targetPlayersID(ArrayList<Integer> targetPlayersID) {
            this.targetPlayersID = targetPlayersID;
            return this;
        }

        public EffectRequestBuilder targetPositions(ArrayList<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public EffectRequestBuilder targetRoomColor(Color targetRoomColor) {
            this.targetRoomColor = targetRoomColor;
            return this;
        }

        public EffectRequestBuilder senderMovePosition(PlayerPosition senderMovePosition) {
            this.senderMovePosition = senderMovePosition;
            return this;
        }

        public EffectRequestBuilder targetPlayersMovePositions(ArrayList<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public EffectRequestBuilder paymentPowerupsID(ArrayList<Integer> paymentPowerupsID) {
            this.paymentPowerupsID = paymentPowerupsID;
            return this;
        }
    }
}
