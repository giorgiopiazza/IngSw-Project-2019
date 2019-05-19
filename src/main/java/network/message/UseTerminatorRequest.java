package network.message;

import enumerations.MessageContent;
import model.player.PlayerPosition;
import model.player.UserPlayer;

public class UseTerminatorRequest extends Message {
    private PlayerPosition movingPosition;
    private UserPlayer targetPlayer;

    public UseTerminatorRequest(String username, PlayerPosition movingPosition, UserPlayer targetPlayer) {
        super(username, MessageContent.TERMINATOR);
        this.movingPosition = movingPosition;
        this.targetPlayer = targetPlayer;
    }

    public PlayerPosition getMovingPosition() {
        return this.movingPosition;
    }

    public UserPlayer getTargetPlayer() {
        return this.targetPlayer;
    }
}
