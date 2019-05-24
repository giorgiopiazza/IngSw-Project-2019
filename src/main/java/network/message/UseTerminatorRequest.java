package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;

public class UseTerminatorRequest extends Message {
    private PlayerPosition movingPosition;
    private String targetPlayer;

    public UseTerminatorRequest(String username, String token, PlayerPosition movingPosition, String targetPlayer) {
        super(username, token, MessageContent.TERMINATOR);
        this.movingPosition = movingPosition;
        this.targetPlayer = targetPlayer;
    }

    public PlayerPosition getMovingPosition() {
        return this.movingPosition;
    }

    public String getTargetPlayer() {
        return this.targetPlayer;
    }
}
