package network.message;

import enumerations.MessageContent;
import model.player.PlayerPoints;
import utility.GameCostants;
import utility.NullObjectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This message contains the information about the winners of the game.
 * More player can win the same game. To send this information to the
 * clients an ArrayList containing all the {@link PlayerPoints Points}
 * objects.
 */
public class WinnersResponse extends Message {
    private static final long serialVersionUID = -1441012929477935469L;

    private final ArrayList<PlayerPoints> winners;

    /**
     * Builds the response containing all the information needed by the view to show the winners and
     * the loosers of the game
     *
     * @param winners the ArrayList containing the {@link PlayerPoints PlayerPoints} of all the players
     */
    public WinnersResponse(List<PlayerPoints> winners) {
        super(GameCostants.GOD_NAME, null,MessageContent.WINNER);

        this.winners = NullObjectHelper.getNotNullArrayList(winners);
    }

    /**
     * @return the ArrayList containing the {@link PlayerPoints PlayerPoints} of all the players
     */
    public List<PlayerPoints> getWinners() {
        return this.winners;
    }

    @Override
    public String toString() {
        return "WinnersResponse{" +
                "winners=" + winners +
                '}';
    }
}
