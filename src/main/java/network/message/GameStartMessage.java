package network.message;

import enumerations.MessageContent;
import model.Game;

public class GameStartMessage extends Message {
    private static final long serialVersionUID = -5671092105322763783L;

    private final String firstPlayer;

    public GameStartMessage(String firstPlayer) {
        super(Game.GOD, null, MessageContent.READY);
        this.firstPlayer = firstPlayer;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }
}
