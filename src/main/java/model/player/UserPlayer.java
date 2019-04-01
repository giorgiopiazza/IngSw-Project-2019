package model.player;

import enumerations.Color;
import exceptions.player.EmptyHandException;
import exceptions.player.MaxCardsInHandException;
import model.cards.PowerupCard;
import model.cards.WeaponCard;

import java.util.ArrayList;
import java.util.List;

public class UserPlayer extends Player {
    private List<WeaponCard> weapons;
    private List<PowerupCard> powerups;
    private final boolean firstPlayer;
    private boolean terminator;

    public UserPlayer(String nickname, Color color, boolean firstPlayer, PlayerPosition position,
                      PlayerBoard playerBoard, boolean terminator) {

        super(nickname, color, position, playerBoard);
        this.firstPlayer = firstPlayer;
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

    public boolean isFirstPlayer() {
        return this.firstPlayer;
    }

    /**
     * Adds a weapon to your hand when you do not have to discard one
     *
     * @param weapon
     * @throws MaxCardsInHandException
     */
    public void addWeapon(WeaponCard weapon) throws MaxCardsInHandException {
        if (weapons.size() == 3) {
            throw new MaxCardsInHandException("weapons");
        }
        weapons.add(weapon);
    }

    /**
     * Adds a weapond in the position of the one you want to discharge
     *
     * @param addedWeapon
     * @param discardWeapon
     */
    public void addWeapon(WeaponCard addedWeapon, WeaponCard discardWeapon) {
        weapons.set(weapons.indexOf(discardWeapon), addedWeapon);
    }

    public boolean hasWeapon(WeaponCard weapon) {
        return weapons.contains(weapon);
    }

    public int weaponsNum() {
        return weapons.size();
    }

    public WeaponCard[] getWeapons() {
        return weapons.toArray(new WeaponCard[0]);
    }

    /**
     * Returns true if you can add the powerup to your hand, false instead
     *
     * @param powerup
     * @return
     */
    public boolean addPowerup(PowerupCard powerup) {
        if (powerups.size() == 3) {
            return false;
        }
        powerups.add(powerup);
        return true;
    }

    public boolean discardPowerup(PowerupCard powerup) throws EmptyHandException {
        if (powerups.isEmpty()) {
            throw new EmptyHandException("powerups");
        }
        powerups.remove(powerup);
        return true;
    }

    public boolean hasPowerup(PowerupCard powerup) {
        return powerups.contains(powerup);
    }

    public PowerupCard[] getPowerups() {
        return powerups.toArray(new PowerupCard[0]);
    }

}
