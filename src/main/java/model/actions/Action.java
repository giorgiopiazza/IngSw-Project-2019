package model.actions;

import exceptions.AdrenalinaException;

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
