package network.message;

import enumerations.MessageContent;
import model.Game;
import model.GameSerialized;

public class GameStateMessage extends Message {
    private final GameSerialized gameSerialized;
    private final String turnOwner;

    public GameStateMessage(String userName, String turnOwner) {
        super (Game.GOD, null, MessageContent.GAME_STATE);
        this.gameSerialized = new GameSerialized(userName);
        this.turnOwner = turnOwner;
    }

    public GameSerialized getGameSerialized() {
        return gameSerialized;
    }

    public String getTurnOwner() {
        return turnOwner;
    }
}
