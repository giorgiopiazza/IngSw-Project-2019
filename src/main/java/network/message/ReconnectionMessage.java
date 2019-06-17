package network.message;

import enumerations.MessageContent;
import model.Game;

public class ReconnectionMessage extends Message {
    private static final long serialVersionUID = -3306576176165651299L;

    private final String newToken;
    private final GameStateMessage gameStateMessage;

    public ReconnectionMessage(String newToken, GameStateMessage gameStateMessage) {
        super(Game.GOD, null, MessageContent.RECONNECTION);
        this.newToken = newToken;
        this.gameStateMessage = gameStateMessage;
    }

    public String getNewToken() {
        return newToken;
    }

    public GameStateMessage getGameStateMessage() {
        return gameStateMessage;
    }
}