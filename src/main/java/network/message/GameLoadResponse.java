package network.message;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import enumerations.UserPlayerState;
import model.Game;

public class GameLoadResponse extends Message {
    private static final long serialVersionUID = 4880522547664967982L;

    private final String newToken;
    private final String message;
    private final MessageStatus status;
    private final UserPlayerState userPlayerState;

    public GameLoadResponse(String message, String newToken, MessageStatus status, UserPlayerState userPlayerState) {
        super(Game.GOD, null, MessageContent.GAME_LOAD);
        this.message = message;
        this.newToken = newToken;
        this.status = status;
        this.userPlayerState = userPlayerState;
    }

    public String getMessage() {
        return message;
    }

    public String getNewToken() {
        return newToken;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public UserPlayerState getUserPlayerState() {
        return userPlayerState;
    }
}
