package utility.persistency;

import enumerations.PossibleAction;
import enumerations.PossiblePlayerState;
import model.cards.PowerupCard;
import model.player.Bot;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.*;

public class NotTransientPlayer implements Serializable {
    private static final long serialVersionUID = -9213961725005653060L;

    private String userName;

    private int points;
    private EnumSet<PossibleAction> possibleActions;
    private PossiblePlayerState playerState;
    private List<PowerupCard> powerups;
    private PowerupCard spawningCard;

    NotTransientPlayer(UserPlayer userPlayer) {
        this.userName = userPlayer.getUsername();
        this.points = userPlayer.getPoints();

        this.possibleActions = EnumSet.copyOf(userPlayer.getPossibleActions());
        this.playerState = userPlayer.getPlayerState();
        this.powerups = new ArrayList<>(Arrays.asList(userPlayer.getPowerups()));
        this.spawningCard = userPlayer.getSpawningCard();
    }

    NotTransientPlayer(Bot bot) {
        this.userName = bot.getUsername();
        this.points = bot.getPoints();
    }

    public String getUserName() {
        return userName;
    }

    public int getPoints() {
        return points;
    }

    public Set<PossibleAction> getPossibleActions() {
        return possibleActions;
    }

    public PossiblePlayerState getPlayerState() {
        return playerState;
    }

    public List<PowerupCard> getPowerups() {
        return powerups;
    }

    public PowerupCard getSpawningCard() {
        return spawningCard;
    }
}
