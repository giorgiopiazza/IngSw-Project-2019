package network.message;

import enumerations.MessageContent;
import model.Game;
import model.GameSerialized;

public class GameStateMessage extends Message {

    private GameSerialized gameSerialized;

    public GameStateMessage(String userName) {
        super (Game.GOD, null, MessageContent.GAME_STATE);
        this.gameSerialized = new GameSerialized(userName);
    }

    public GameSerialized getGameSerialized() {
        return gameSerialized;
    }
}
