package network.message;

import enumerations.MessageContent;
import utility.GameCostants;

public class GameStartMessage extends Message {
    private static final long serialVersionUID = -5671092105322763783L;

    private final String firstPlayer;

    public GameStartMessage(String firstPlayer) {
        super(GameCostants.GOD_NAME, null, MessageContent.READY);
        this.firstPlayer = firstPlayer;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }

    @Override
    public String toString() {
        return "GameStartMessage{" +
                "senderUsername=" + getSenderUsername() + ", " +
                "firstPlayer='" + firstPlayer + '\'' +
                '}';
    }
}
