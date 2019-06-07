package network.message;

import enumerations.MessageContent;
import model.Game;

public class TurnOwnerChangeMessage extends Message {
    private static final long serialVersionUID = -1205336871283458033L;

    private final String turnOwner;

    public TurnOwnerChangeMessage(String userName, String turnOwner) {
        super (Game.GOD, null, MessageContent.GAME_STATE);
        this.turnOwner = turnOwner;
    }

    public String getTurnOwner() {
        return turnOwner;
    }
}
