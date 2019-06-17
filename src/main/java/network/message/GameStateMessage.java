package network.message;

import enumerations.MessageContent;
import model.Game;
import model.GameSerialized;

public class GameStateMessage extends Message {
    private static final long serialVersionUID = 2725986184174583892L;

    private final GameSerialized gameSerialized;
    private final String turnOwner;
    private boolean grenadeUsage;

    public GameStateMessage(String userName, String turnOwner, boolean grenadeUsage) {
        super (Game.GOD, null, MessageContent.GAME_STATE);
        this.gameSerialized = new GameSerialized(userName);
        this.turnOwner = turnOwner;
        this.grenadeUsage = grenadeUsage;
    }

    public GameSerialized getGameSerialized() {
        return gameSerialized;
    }

    public String getTurnOwner() {
        return turnOwner;
    }

    public boolean isGrenadeUsage() {
        return grenadeUsage;
    }
}
