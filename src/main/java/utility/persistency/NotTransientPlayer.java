package utility.persistency;

import enumerations.PossibleAction;
import enumerations.PossiblePlayerState;
import model.cards.PowerupCard;
import model.player.Terminator;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class NotTransientPlayer implements Serializable {
    private static final long serialVersionUID = -9213961725005653060L;

    private String userName;

    private int points;
    private EnumSet<PossibleAction> possibleActions;
    private PossiblePlayerState playerState;
    private List<PowerupCard> powerups;
    private PowerupCard spawningCard;

    public NotTransientPlayer(UserPlayer userPlayer) {
        this.userName = userPlayer.getUsername();
        this.points = userPlayer.getPoints();

        this.possibleActions = EnumSet.copyOf(userPlayer.getPossibleActions());
        this.playerState = userPlayer.getPlayerState();
        this.powerups = new ArrayList<>(Arrays.asList(userPlayer.getPowerups()));
        this.spawningCard = userPlayer.getSpawningCard();
    }

    public NotTransientPlayer(Terminator terminator) {
        this.userName = terminator.getUsername();
        this.points = terminator.getPoints();
    }

    public String getUserName() {
        return userName;
    }

    public int getPoints() {
        return points;
    }

    public EnumSet<PossibleAction> getPossibleActions() {
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
