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
    public boolean terminator;

    public UserPlayer(String nickname, Color color, boolean firstPlayer, PlayerPosition position,
                            PlayerBoard playerBoard, boolean terminator) {

        super(nickname, color, position, playerBoard);
        this.firstPlayer = firstPlayer;
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();
        this.terminator = terminator;
    }

    public boolean hasTerminator() { return terminator; }

    public void setTerminator(boolean terminator) { this.terminator = terminator; }

    public boolean isFirstPlayer() {
        return this.firstPlayer;
    }

    /**
     * Adds a weapon to your hand when you do not have to discard one
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
     * @param addedWeapon
     * @param discardWeapon
     */
    public void addWeapon(WeaponCard addedWeapon, WeaponCard discardWeapon ) {
        weapons.set(weapons.indexOf(discardWeapon), addedWeapon);
    }

    public boolean hasWeapon (WeaponCard weapon) { return weapons.contains(weapon); }

    public int weaponsNum () { return weapons.size(); }

    public WeaponCard[] getWeapons() { return weapons.toArray(new WeaponCard[0]); }

    /**
     * Adds a powerup to your hand if you do not have 3, in this case you can't pick an other powerup
     * @param powerup
     */
    public void addPowerup(PowerupCard powerup) {
        if (powerups.size() == 3) {
            return;
        }
        powerups.add(powerup);
    }

    public void discardPowerup (PowerupCard powerup) throws EmptyHandException {
        if (powerups.isEmpty()) {
            throw new EmptyHandException("powerups");
        }
        powerups.remove(powerup);
    }

    public boolean hasPowerup (PowerupCard powerup) { return powerups.contains(powerup); }

    public PowerupCard[] getPowerups() { return powerups.toArray(new PowerupCard[0]); }

}
