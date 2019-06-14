package network.message;

import enumerations.MessageContent;
import model.Game;
import model.player.Player;

import java.util.ArrayList;

public class WinnersResponse extends Message {
    private static final long serialVersionUID = -1441012929477935469L;

    private final ArrayList<Player> winners;

    public WinnersResponse(ArrayList<Player> winners) {
        super(Game.GOD, null,MessageContent.WINNER);

        this.winners = winners;
    }

    public ArrayList<Player> getWinners() {
        return this.winners;
    }
}
