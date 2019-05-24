package network.message;

import enumerations.MessageContent;
import model.Game;

public class GameStartMessage extends Message {
    private final String firstPlayer;

    public GameStartMessage(String firstPlayer) {
        super(Game.GOD, MessageContent.READY);
        this.firstPlayer = firstPlayer;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }
}
