package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

public class BotUseRequest extends Message {
    private static final long serialVersionUID = -2468038742086444870L;

    private final PlayerPosition movingPosition;
    private final String targetPlayer;

    public BotUseRequest(String username, String token, PlayerPosition movingPosition, String targetPlayer) {
        super(username, token, MessageContent.BOT_ACTION);
        this.movingPosition = movingPosition;
        this.targetPlayer = targetPlayer;
    }

    public PlayerPosition getMovingPosition() {
        return this.movingPosition;
    }

    public String getTargetPlayer() {
        return this.targetPlayer;
    }

    @Override
    public String toString() {
        return "UseTerminatorRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                ", movingPosition=" + movingPosition +
                ", targetPlayer='" + targetPlayer + '\'' +
                '}';
    }
}
