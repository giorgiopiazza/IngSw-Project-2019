package model.cards;

import exceptions.cards.WeaponNotChargedException;
import model.cards.effects.Effect;
import model.player.Player;

/**
 * Interface used to manage the state of a Weapon
 */
public interface WeaponState {

    /**
     * Returns if the WeaponState is charged or not
     *
     * @param weapon the Weapon you want to know if is charged
     * @return true if the WeaponState is charged, otherwise false
     */
    boolean charged(WeaponCard weapon);

    /**
     * Returns if the WeaponState can be charged or not
     *
     * @param weapon the Weapon you want to know if is rechargeable
     * @return true if the WeaponState is rechargeable, otherwise false
     */
    boolean rechargeable(WeaponCard weapon);

    /**
     * Returns the state of a Weapon
     *
     * @return an integer related to the Weapon's state
     */
    int status();

    /**
     * Method that executes the effect of the WeaponState
     *
     * @param effect the effect of the Weapon to be executed
     * @param target contains informations of how and on who the effect is executed
     * @param playerDealer the Player who uses the Weapon's effect
     * @throws WeaponNotChargedException exception thrown in case the Weapon is not charged
     */
    void use(Effect effect, Target target, Player playerDealer) throws WeaponNotChargedException;
}
