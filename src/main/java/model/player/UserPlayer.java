package model.player;

import enumerations.PlayerColor;
import enumerations.PlayerBoardState;
import enumerations.PossibleAction;
import enumerations.PossiblePlayerState;
import exceptions.player.CardAlreadyInHandException;
import exceptions.player.EmptyHandException;
import exceptions.player.MaxCardsInHandException;
import model.Game;
import model.cards.PowerupCard;
import model.cards.WeaponCard;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class UserPlayer extends Player {
    private transient EnumSet<PossibleAction> possibleActions;
    private transient PossiblePlayerState playerState;
    private transient List<WeaponCard> weapons;
    private transient List<PowerupCard> powerups;
    private boolean firstPlayer;

    public UserPlayer(String nickname, PlayerColor color,
                      PlayerBoard playerBoard) {

        super(nickname, color, playerBoard);
        this.playerState = PossiblePlayerState.FIRST_SPAWN;
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();
    }

    public void setFirstPlayer() {
        this.firstPlayer = true;
    }

    public boolean isFirstPlayer() {
        return this.firstPlayer;
    }

    public PossiblePlayerState getPlayerState() {
        return this.playerState;
    }

    public void changePlayerState(PossiblePlayerState playerState) {
        this.playerState = playerState;
    }

    /**
     * Adds a weapon to your hand when you do not have to discard one
     *
     * @param weapon the weapon you want to add
     * @throws MaxCardsInHandException if you already have 3 cards but you have not decided to discard one
     */
    public void addWeapon(WeaponCard weapon) throws MaxCardsInHandException {
        if (weapons.size() == 3) {
            throw new MaxCardsInHandException("weapons");
        }
        if (weapon == null) throw new NullPointerException("You can not add a null WeaponCard to your hand!");
        if (this.weapons.contains(weapon)) throw new CardAlreadyInHandException(weapon.getName());
        weapons.add(weapon);
    }

    /**
     * Adds a weapon in the position of the one you want to discharge
     *
     * @param addedWeapon   the weapon to be added
     * @param discardWeapon the weapon to be replaced
     */
    public void addWeapon(WeaponCard addedWeapon, WeaponCard discardWeapon) {
        if ((addedWeapon == null) || (discardWeapon == null)) {
            throw new NullPointerException("You can not add or throw a null WeaponCard in your hand!");
        }
        if (this.weapons.contains(addedWeapon)) throw new CardAlreadyInHandException(addedWeapon.getName());
        weapons.set(weapons.indexOf(discardWeapon), addedWeapon);
    }

    public boolean hasWeapon(WeaponCard weapon) {
        return weapons.contains(weapon);
    }

    /**
     * Gives an array representation of the weapons of a player
     *
     * @return the array of weapons
     */
    public WeaponCard[] getWeapons() {
        return weapons.toArray(new WeaponCard[0]);
    }

    /**
     * Method to add a powerup to add a powerup to a player's jhand
     *
     * @param powerup the powerup to be added
     * @throws MaxCardsInHandException in case the player already has 3 powerups on his hand
     */
    public void addPowerup(PowerupCard powerup) throws MaxCardsInHandException {
        if (powerups.size() == 3) {
            throw new MaxCardsInHandException(powerup.toString());
        }
        powerups.add(powerup);
    }

    /**
     * Discards the specified powerup from your hand
     *
     * @param powerup the powerup to be discarded
     * @return true if the powerup has been discarded
     * @throws EmptyHandException if your hand has no powerups
     */
    public boolean discardPowerup(PowerupCard powerup) throws EmptyHandException {
        if (powerups.isEmpty()) {
            throw new EmptyHandException("powerups");
        }
        powerups.remove(powerup);
        return true;
    }

    /**
     * Method that discards the powerup in the specified index from the current player
     *
     * @param i the index of the powerup to discard
     * @throws EmptyHandException in case the current player has no powerups in his hand
     */
    public void discardPowerupByIndex(int i) throws EmptyHandException {
        if (i < 0) {
            throw new IllegalArgumentException("An index can never be negative!");
        }

        if (i > powerups.size()) {
            throw new IllegalArgumentException("The index of the powerup you are trying to discard is too high!");
        }

        if (powerups.isEmpty()) {
            throw new EmptyHandException("powerups");
        }

        powerups.remove(i);
    }

    public boolean hasPowerup(PowerupCard powerup) {
        return powerups.contains(powerup);
    }

    /**
     * Gives an array representation of the powerups of a player
     *
     * @return the array of powerups
     */
    public PowerupCard[] getPowerups() {
        return powerups.toArray(new PowerupCard[0]);
    }

    public Set<PossibleAction> getPossibleActions() {
        return this.possibleActions;
    }

    /**
     * Method that sets the possible actions for a player whose state is FIRST_SPAWN
     *
     * @param isTerminatorPresent boolean that specifies if the terminator is present in the game
     */
    public void setStartingPossibleActions(boolean isTerminatorPresent) {
        if (isFirstPlayer() && isTerminatorPresent) {
            possibleActions = EnumSet.of(PossibleAction.SPAWN_TERMINATOR, PossibleAction.CHOOSE_SPAWN);
        } else if (isTerminatorPresent) {
            possibleActions = EnumSet.of(PossibleAction.CHOOSE_SPAWN, PossibleAction.TERMINATOR_ACTION);
        } else {
            possibleActions = EnumSet.of(PossibleAction.CHOOSE_SPAWN);
        }
    }

    /**
     * Method that sets the possible actions a player has due to his state, when the game is in NORMAL state
     * If the game has the terminator, every player in his turn must always do also the terminator action
     */
    public void setPossibleActions() {
        PlayerBoardState currentPlayerBoardState = getPlayerBoard().getBoardState();

        switch (currentPlayerBoardState) {
            case NORMAL:
                possibleActions = EnumSet.of(PossibleAction.MOVE, PossibleAction.MOVE_AND_PICK, PossibleAction.SHOOT);
                break;
            case FIRST_ADRENALINE:
                possibleActions = EnumSet.of(PossibleAction.MOVE, PossibleAction.ADRENALINE_PICK, PossibleAction.SHOOT);
                break;
            default:    // second adrenaline
                possibleActions = EnumSet.of(PossibleAction.MOVE, PossibleAction.ADRENALINE_PICK, PossibleAction.ADRENALINE_SHOOT);
        }

        // in each state the player is he can always recharge his weapons (AT THE END OF THE TURN!)
        possibleActions.add(PossibleAction.RELOAD);

        if (Game.getInstance().isTerminatorPresent()) {
            possibleActions.add(PossibleAction.TERMINATOR_ACTION);
        }
    }

    /**
     * Method that sets the possible actions a player has due to his position in the round turn,
     * when the game is in FRENZY state
     * If the game has the terminator, every player in his turn must always do also the terminator action
     *
     * @param frenzyActivator the player who activated the final frenzy mode
     */
    public void setFrenzyPossibleActions(UserPlayer frenzyActivator) {
        if (Game.getInstance().getDoubleActionFrenzyPlayers(frenzyActivator).contains(this)) {
            possibleActions = EnumSet.of(PossibleAction.FRENZY_MOVE, PossibleAction.FRENZY_PICK, PossibleAction.FRENZY_SHOOT);
        } else {
            possibleActions = EnumSet.of(PossibleAction.LIGHT_FRENZY_SHOOT, PossibleAction.LIGHT_FRENZY_PICK);
        }

        if (Game.getInstance().isTerminatorPresent()) {
            possibleActions.add(PossibleAction.TERMINATOR_ACTION);
        }
    }

    @Override
    public String toString() {
        return "UserPlayer{" +
                "weapons=" + weapons +
                ", playerBoard=" + getPlayerBoard() +
                ", powerups=" + powerups +
                ", firstPlayer=" + firstPlayer +
                ", color=" + color +
                '}';
    }
}