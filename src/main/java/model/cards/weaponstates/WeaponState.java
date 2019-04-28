package model.cards.weaponstates;

import exceptions.cards.WeaponNotChargedException;
import model.cards.WeaponCard;
import model.cards.effects.Effect;

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
     * @param effect
     * @param command
     * @throws WeaponNotChargedException exception thrown in case the Weapon is not charged
     */
    void use(Effect effect, String command) throws WeaponNotChargedException;
}
