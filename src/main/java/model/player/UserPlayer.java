package model.player;

import enumerations.*;
import exceptions.game.InexistentColorException;
import exceptions.player.CardAlreadyInHandException;
import exceptions.player.EmptyHandException;
import exceptions.player.MaxCardsInHandException;
import exceptions.player.MissingCardException;
import model.cards.PowerupCard;
import model.cards.WeaponCard;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class UserPlayer extends Player {
    private transient EnumSet<PossibleAction> possibleActions;
    private transient PossiblePlayerState playerState;
    private List<WeaponCard> weapons;
    private transient List<PowerupCard> powerups;
    private transient PowerupCard spawningCard;
    private boolean firstPlayer;

    public UserPlayer(String nickname) {
        super(nickname);
        this.playerState = PossiblePlayerState.FIRST_SPAWN;
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();
    }

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

    public void setSpawningCard(PowerupCard spawningCard) {
        this.spawningCard = spawningCard;
    }

    public PowerupCard getSpawningCard() {
        return this.spawningCard;
    }

    public void setPossibleActions(EnumSet<PossibleAction> possibleActions) {
        this.possibleActions = possibleActions;
    }

    public void setPlayerState(PossiblePlayerState playerState) {
        this.playerState = playerState;
    }

    public void setWeapons(List<WeaponCard> weapons) {
        this.weapons = weapons;
    }

    public void setPowerups(List<PowerupCard> powerups) {
        this.powerups = powerups;
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

    /**
     * Gives an array representation of the powerups of a player
     *
     * @return the array of powerups
     */
    public PowerupCard[] getPowerups() {
        return powerups.toArray(PowerupCard[]::new);
    }

    /**
     * Method used to receive a powerup given his name and color in case two are possessed
     *
     * @param powerupName String containing the powerup name
     * @param color       the color of the powerup to be returned
     * @return the powerup specified in the player's hand
     * @throws InexistentColorException in case the color passed does not exist
     */
    public int getPowerupByName(String powerupName, RoomColor color) throws InexistentColorException {
        if (powerups.isEmpty()) throw new NullPointerException(this.getUsername() + " has no powerups!");

        for (int i = 0; i < powerups.size(); ++i) {
            try {
                if (powerups.get(i).getName().equals(powerupName) && color == null) {
                    return i;
                }

                if (powerups.get(i).getName().equals(powerupName) && RoomColor.getColor(powerups.get(i).getValue().name()).equals(color)) {
                    return i;
                }
            } catch (InexistentColorException e) {
                throw new InexistentColorException(color.name());
            }
        }

        throw new MissingCardException(powerupName);
    }

    /**
     * Method that returns the number of the specified powerup in the players hand
     *
     * @param powerupName String containing the powerup's name
     * @return an integer containing the number of the occurrences of the powerup in the specified name
     */
    public int getPowerupOccurrences(String powerupName) {
        int occurrences = 0;
        for (PowerupCard powerup : powerups) {
            if (powerup.getName().equals(powerupName)) {
                ++occurrences;
            }
        }

        return occurrences;
    }

    public Set<PossibleAction> getPossibleActions() {
        return this.possibleActions;
    }

    public void setActions(EnumSet<PossibleAction> possibleActions) {
        this.possibleActions = possibleActions;
    }

    public void addAction(PossibleAction addingAction) {
        this.possibleActions.add(addingAction);
    }

    public void removeAction(PossibleAction removingAction) {
        this.possibleActions.remove(removingAction);
    }


    @Override
    public String toString() {
        return "UserPlayer{" +
                "weapons=" + weapons +
                ", playerBoard=" + getPlayerBoard() +
                ", firstPlayer=" + firstPlayer +
                ", color=" + color +
                '}';
    }
}