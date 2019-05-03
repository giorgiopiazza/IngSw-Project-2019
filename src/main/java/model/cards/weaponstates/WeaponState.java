package model.cards.weaponstates;

import exceptions.cards.WeaponNotChargedException;
import model.cards.WeaponCard;
import model.cards.effects.Effect;
import network.message.EffectRequest;

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
     * @param weapon the Weapon you want to know if is isRechargeable
     * @return true if the WeaponState is isRechargeable, otherwise false
     */
    boolean isRechargeable(WeaponCard weapon);

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
     * @param request
     * @throws WeaponNotChargedException exception thrown in case the Weapon is not charged
     */
    void use(Effect effect, EffectRequest request) throws WeaponNotChargedException;
}
