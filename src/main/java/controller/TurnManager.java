package controller;

import enumerations.PossibleGameState;
import model.player.Player;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This Class is the One that handles the Turn Assignment to a {@link UserPlayer UserPlayer} during each phase of the
 * game, using specific parameters when the Turn Assignment does not follow the standard one performed with the method
 * {@link #nextTurn() nextTurn}
 */
public class TurnManager implements Serializable {
    private static final long serialVersionUID = -6048602661465079910L;

    private UserPlayer turnOwner;
    private UserPlayer lastPlayer;
    private Player frenzyActivator;
    private final UserPlayer lastRoundPlayer;

    private List<UserPlayer> players;
    private List<Player> damagedPlayers;
    private List<UserPlayer> deathPlayers;

    private UserPlayer markedByGrenadePlayer;
    private boolean markingTerminator;

    private boolean firstTurn;
    private boolean secondAction;

    private List<UserPlayer> afterFrenzy;
    private List<UserPlayer> beforeFrenzy;

    private PossibleGameState arrivingGameState;

    private int count;
    private int turnCount;

    /**
     * Creates an Instance of {@link TurnManager TurnManager} binding to it the List of {@link UserPlayer UserPlayers}
     * that have joined the {@link model.Game Game}
     *
     * @param players the List of {@link UserPlayer UserPlayers} in the {@link model.Game Game}
     */
    TurnManager(List<UserPlayer> players) {
        this.players = new ArrayList<>(players);
        this.damagedPlayers = new ArrayList<>();
        this.firstTurn = true;
        this.lastRoundPlayer = players.get(players.size() - 1);
        this.turnOwner = players.get(count);
        this.afterFrenzy = new ArrayList<>();
        this.beforeFrenzy = new ArrayList<>();
    }

    /**
     * Creates an Instance of {@link TurnManager TurnManager} from an already existing one used to reload a started {@link model.Game Game}
     *
     * @param other the other {@link TurnManager TurnManager}
     */
    TurnManager(TurnManager other) {
        this.turnOwner = other.turnOwner;
        this.lastPlayer = other.lastPlayer;
        this.lastRoundPlayer = other.lastRoundPlayer;

        this.players = other.players;
        this.frenzyActivator = other.frenzyActivator;
        this.damagedPlayers = other.damagedPlayers;
        this.deathPlayers = other.deathPlayers;

        this.markedByGrenadePlayer = other.markedByGrenadePlayer;
        this.markingTerminator = other.markingTerminator;

        this.firstTurn = other.firstTurn;
        this.secondAction = other.secondAction;

        this.afterFrenzy = other.afterFrenzy;
        this.beforeFrenzy = other.beforeFrenzy;

        this.arrivingGameState = other.arrivingGameState;

        this.count = other.count;
        this.turnCount = other.turnCount;
    }

    /**
     * @return the {@link UserPlayer UserPlayer} owning the Turn
     */
    public UserPlayer getTurnOwner() {
        return turnOwner;
    }

    /**
     * Method used to set the Last {@link UserPlayer Player} of the Game, used to understand when it ends
     */
    void setLastPlayer() {
        this.lastPlayer = turnOwner;
    }

    /**
     * @return the Last {@link UserPlayer Player} of the Game
     */
    UserPlayer getLastPlayer() {
        return this.lastPlayer;
    }

    /**
     * @return the {@link Player Player} who activated the frenzy
     */
    Player getFrenzyActivator() {
        return this.frenzyActivator;
    }

    /**
     * the turn owner as the frenzy activator
     */
    void setFrenzyActivator() {
        this.frenzyActivator = turnOwner;
    }

    /**
     * Method that sets the ArrayList of {@link UserPlayer UserPlayers} that took damage after a damaging {@link model.actions.Action Action}
     *
     * @param damaged the ArrayList of damaged {@link UserPlayer UserPlayers}
     */
    void setDamagedPlayers(ArrayList<Player> damaged) {
        damagedPlayers = Objects.requireNonNullElse(damaged, new ArrayList<>());
    }

    /**
     * @return the ArrayList of damaged {@link UserPlayer UserPlayers}, used to verify when TARGETING SCOPE
     * can be used
     */
    List<Player> getDamagedPlayers() {
        return this.damagedPlayers;
    }

    /**
     * @return the ArrayList of the {@link UserPlayer UserPlayers} that can use a TAGBACK GRENADE
     */
    List<UserPlayer> getGrenadePossibleUsers() {
        ArrayList<UserPlayer> grenadePossessors = new ArrayList<>();

        for(Player player : damagedPlayers) {
            if(((UserPlayer) player).getPowerupOccurrences("TAGBACK GRENADE") > 0) {
                grenadePossessors.add((UserPlayer) player);
            }
        }

        return grenadePossessors;
    }

    /**
     * Method that sets the {@link UserPlayer UserPlayers} that died during a {@link UserPlayer Player's} turn
     *
     * @param deaths the ArrayList of dead {@link UserPlayer UserPlayers}
     */
    void setDeathPlayers(List<UserPlayer> deaths) {
        deathPlayers = Objects.requireNonNullElse(deaths, new ArrayList<>());
        for(UserPlayer deathPlayer : deathPlayers) {
            deathPlayer.setPosition(null);
        }
    }

    /**
     * @return an ArrayList of dead {@link UserPlayer UserPlayers}, it is used to give them the turn to Respawn
     */
    List<UserPlayer> getDeathPlayers() {
        return this.deathPlayers;
    }

    /**
     * Sets the {@link model.player.Player Player} that can be
     *
     * @param markedByGrenadePlayer the {@link UserPlayer UserPlayer} on which turn a TAGBACK GRENADE can be used
     */
    void setMarkedByGrenadePlayer(UserPlayer markedByGrenadePlayer) {
        this.markedByGrenadePlayer = markedByGrenadePlayer;
    }

    /**
     * @return the "real" turn owner while a TAGBACK GRENADE usage is getting maneged
     */
    UserPlayer getMarkedByGrenadePlayer() {
        return this.markedByGrenadePlayer;
    }

    /**
     * Used to set the attribute {@link #markingTerminator markingTerminator} when in a TAGBACK GRENADE usage he is the
     * one to be marked by the powerup
     *
     * @param markingTerminator true if the {@link model.player.Bot Bot} is the one to be marked, otherwise false
     */
    void setMarkingTerminator(boolean markingTerminator) {
        this.markingTerminator = markingTerminator;
    }

    /**
     * @return true if the  {@link model.player.Bot Bot} is the one to be marked by the TAGBACK GRENADE, otherwise false
     */
    boolean getMarkingTerminator() {
        return this.markingTerminator;
    }

    /**
     * Method used to temporary set a the parameter {@link #secondAction secondAction} used by the {@link RoundManager RoundManager}
     * to handle the next State of the {@link GameManager GameManager}
     *
     * @param secondAction Boolean that specifies if the performing action is the second
     */
    void setSecondAction(boolean secondAction) {
        this.secondAction = secondAction;
    }

    /**
     * @return a Boolean that specifies if the performing action is the second
     */
    boolean isSecondAction() {
        return this.secondAction;
    }

    /**
     * Method used to know when the first turn has ended
     */
    void endOfFirstTurn() {
        this.firstTurn = false;
    }

    /**
     * @return if the turn of the turnOwner is its very first one or not
     */
    boolean isFirstTurn() {
        return this.firstTurn;
    }

    /**
     * @return the ArrayList of the {@link UserPlayer UserPlayers} that are after the FrenzyActivator but before the FirstPlayer;
     * these players will have two FrenzyActions during the FinalFrenzy
     */
    List<UserPlayer> getAfterFrenzy() {
        return this.afterFrenzy;
    }

    /**
     * Method used to set the {@link PossibleGameState PossibleGameState} from which the {@link GameManager GameManager}
     * is evolving
     *
     * @param arrivingGameState the {@link PossibleGameState PossibleGameState} to be set
     */
    void setArrivingGameState(PossibleGameState arrivingGameState) {
        this.arrivingGameState = arrivingGameState;
    }

    /**
     * @return the {@link PossibleGameState PossibleGameState} of the {@link GameManager GameManager} used to restore
     * the correct State after an extemporary action
     */
    PossibleGameState getArrivingGameState() {
        return this.arrivingGameState;
    }

    /**
     * Method that resets the parameter {@link #count} used to give the Turn to each {@link UserPlayer UserPlayer} during
     * the usage of a TAGBACK GRANADE or while Respawning
     */
    void resetCount() {
        this.turnCount = 0;
    }

    /**
     * Method that increases the parameter {@link #count}
     */
    void increaseCount() {
        ++this.turnCount;
    }

    /**
     * @return the parameter {@link #count}
     */
    int getTurnCount() {
        return this.turnCount;
    }

    /**
     * Method that gives the Turn to the {@link UserPlayer UserPlayer} that comes after the TurnOwner
     */
    void nextTurn() {
        count++;
        count = count % players.size();
        turnOwner = players.get(count);
    }

    /**
     * Method used before the FinalFrenzy is starting to set the two Sets of different FrenzyPlayers as they will be
     * given the correct number and type of FrenzyActions
     */
    void setFrenzyPlayers() {
        boolean beforeFirst = true;

        do {
            nextTurn();

            if (turnOwner.isFirstPlayer()) {
                beforeFirst = false;
            }

            if (beforeFirst) {
                afterFrenzy.add(turnOwner);
            } else {
                beforeFrenzy.add(turnOwner);
            }

        } while (!turnOwner.equals(frenzyActivator));
    }

    /**
     * @return true if the TurnOwner is the last of a Round, otherwise false
     */
    boolean endOfRound() {
        return turnOwner.equals(lastRoundPlayer);
    }

    /**
     * Method that gives the turn to the specified {@link UserPlayer UserPlayer}
     *
     * @param damagedPlayer the {@link UserPlayer UserPlayer} that is going to become the TurnOwner
     */
    void giveTurn(UserPlayer damagedPlayer) {
        this.turnOwner = damagedPlayer;
    }
}
