package model.player;

import enumerations.Color;
import exceptions.player.CardAlreadyInHandException;
import exceptions.player.EmptyHandException;
import exceptions.player.MaxCardsInHandException;
import model.cards.PowerupCard;
import model.cards.WeaponCard;

import java.util.ArrayList;
import java.util.List;

public class UserPlayer extends Player {
    private List<WeaponCard> weapons;
    private List<PowerupCard> powerups;
    private boolean firstPlayer;
    private boolean terminator;

    public UserPlayer(String nickname, Color color,
                      PlayerBoard playerBoard, boolean terminator) {

        super(nickname, color, playerBoard);
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();
        this.terminator = terminator;
    }

    public void setTerminator(boolean terminator) {
        this.terminator = terminator;
    }

    public boolean hasTerminator() {
        return this.terminator;
    }

    public void setFirstPlayer() {
        this.firstPlayer = true;
    }

    public boolean isFirstPlayer() {
        return this.firstPlayer;
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
     * Returns true if you can add the powerup to your hand, false instead
     *
     * @param powerup the powerup to be added
     * @return true if the powerup can be added to your hand, false if not (your hand already has 3 powerups)
     */
    public boolean addPowerup(PowerupCard powerup) {
        if (powerups.size() == 3) {
            return false;
        }
        powerups.add(powerup);
        return true;
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
     * Discards the powerup of the specified index from your hand
     *
     * @param i the index of the powerup you want to discard
     * @return true if the powerup has been discarded
     * @throws EmptyHandException if your hand has no powerups
     */
    public boolean discardPowerupByIndex(int i) throws EmptyHandException {
        if (i > powerups.size()) {
            throw new IllegalArgumentException("The index of the powerup you are trying to discard is too high!");
        }

        if (powerups.isEmpty()) {
            throw new EmptyHandException("powerups");
        }

        powerups.remove(i);
        return true;
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

    @Override
    public String toString() {
        return "UserPlayer{" +
                "weapons=" + weapons +
                ", playerBoard=" + getPlayerBoard() +
                ", powerups=" + powerups +
                ", firstPlayer=" + firstPlayer +
                ", terminator=" + terminator +
                ", color=" + color +
                '}';
    }
}