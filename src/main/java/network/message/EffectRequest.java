package network.message;

import enumerations.Color;
import enumerations.MessageContent;
import model.player.PlayerPosition;

import java.util.ArrayList;
import java.util.Objects;

public abstract class EffectRequest extends ActionRequest {
    private final ArrayList<String> targetPlayersUsernames;
    private final ArrayList<PlayerPosition> targetPositions;
    private final Color targetRoomColor;
    private final ArrayList<PlayerPosition> targetPlayersMovePositions;

    public EffectRequest(EffectRequestBuilder builder) {
        super(builder.username, builder.content, builder.senderMovePosition, builder.paymentPowerups);

        this.targetPlayersUsernames = Objects.requireNonNullElse(builder.targetPlayersUsernames, new ArrayList<>());
        this.targetPositions = Objects.requireNonNullElse(builder.targetPositions, new ArrayList<>());
        this.targetRoomColor = builder.targetRoomColor;
        this.targetPlayersMovePositions = Objects.requireNonNullElse(builder.targetPlayersMovePositions, new ArrayList<>());
    }

    public ArrayList<String> getTargetPlayersUsernames() {
        return targetPlayersUsernames;
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
        private String username;
        private MessageContent content;

        private ArrayList<String> targetPlayersUsernames;
        private ArrayList<PlayerPosition> targetPositions;
        private Color targetRoomColor;

        private PlayerPosition senderMovePosition;
        private ArrayList<PlayerPosition> targetPlayersMovePositions;

        private ArrayList<Integer> paymentPowerups;

        public EffectRequestBuilder(String username, MessageContent content) {
            this.username = username;
            this.content = content;
        }

        public EffectRequestBuilder targetPlayersUsernames(ArrayList<String> targetPlayersUsernames) {
            this.targetPlayersUsernames = targetPlayersUsernames;
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

        public EffectRequestBuilder paymentPowerups(ArrayList<Integer> paymentPowerups) {
            this.paymentPowerups = paymentPowerups;
            return this;
        }
    }
}
