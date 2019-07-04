package network.message;

import enumerations.RoomColor;
import enumerations.MessageContent;
import model.player.PlayerPosition;
import utility.NullObjectHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class EffectRequest extends ActionRequest {
    private static final long serialVersionUID = 2808691785528480885L;

    private ArrayList<String> targetPlayersUsername;
    private final ArrayList<PlayerPosition> targetPositions;
    private final RoomColor targetRoomColor;
    private final ArrayList<PlayerPosition> targetPlayersMovePositions;

    public EffectRequest(EffectRequestBuilder builder) {
        super(builder.username, builder.token, builder.content, builder.senderMovePosition, builder.paymentPowerups);

        this.targetPlayersUsername = NullObjectHelper.getNotNullArrayList(builder.targetPlayersUsernames);
        this.targetPositions = NullObjectHelper.getNotNullArrayList(builder.targetPositions);
        this.targetRoomColor = builder.targetRoomColor;
        this.targetPlayersMovePositions = NullObjectHelper.getNotNullArrayList(builder.targetPlayersMovePositions);
    }

    public List<String> getTargetPlayersUsername() {
        return targetPlayersUsername;
    }

    public List<PlayerPosition> getTargetPositions() {
        return targetPositions;
    }

    public RoomColor getTargetRoomColor() {
        return targetRoomColor;
    }

    public List<PlayerPosition> getTargetPlayersMovePositions() {
        return targetPlayersMovePositions;
    }

    public void setGrenadeTarget(String grenadeTarget) {
        this.targetPlayersUsername = new ArrayList<>(List.of(grenadeTarget));
    }

    public static class EffectRequestBuilder {
        private String username;
        private String token;
        private MessageContent content;

        private List<String> targetPlayersUsernames;
        private List<PlayerPosition> targetPositions;
        private RoomColor targetRoomColor;

        private PlayerPosition senderMovePosition;
        private List<PlayerPosition> targetPlayersMovePositions;

        private List<Integer> paymentPowerups;

        EffectRequestBuilder(String username, String token, MessageContent content) {
            this.username = username;
            this.token = token;
            this.content = content;
        }

        public EffectRequestBuilder targetPlayersUsernames(List<String> targetPlayersUsernames) {
            this.targetPlayersUsernames = targetPlayersUsernames;
            return this;
        }

        public EffectRequestBuilder targetPositions(List<PlayerPosition> targetPositions) {
            this.targetPositions = targetPositions;
            return this;
        }

        public EffectRequestBuilder targetRoomColor(RoomColor targetRoomColor) {
            this.targetRoomColor = targetRoomColor;
            return this;
        }

        public EffectRequestBuilder senderMovePosition(PlayerPosition senderMovePosition) {
            this.senderMovePosition = senderMovePosition;
            return this;
        }

        public EffectRequestBuilder targetPlayersMovePositions(List<PlayerPosition> targetPlayersMovePositions) {
            this.targetPlayersMovePositions = targetPlayersMovePositions;
            return this;
        }

        public EffectRequestBuilder paymentPowerups(List<Integer> paymentPowerups) {
            this.paymentPowerups = paymentPowerups;
            return this;
        }
    }
}
