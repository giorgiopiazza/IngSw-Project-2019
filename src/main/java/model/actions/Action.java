package model.actions;

import exceptions.AdrenalinaException;

/**
 * Interface implemented by all the possible actions that a player can do in his turn.
 * {@link model.cards.PowerupCard Powerups} are not implementing this interface because they have the same effect's
 * logic of the {@link model.cards.WeaponCard Weapons}
 */
public interface Action {
    /**
     * Method used to verify that the user player can do the action with the parameter
     * he decided and then set to the relative static action class
     *
     * @return true if the action can be done, otherwise false
     */
    boolean validate() throws AdrenalinaException;

    /**
     * Method that makes a player do the action he choosed
     */
    void execute() throws AdrenalinaException;
}
