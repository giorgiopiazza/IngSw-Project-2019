package exceptions.cards;

import exceptions.AdrenalinaException;

public class InvalidPowerupActionException extends AdrenalinaException {
    public InvalidPowerupActionException() {
        super("The action of the powerup is invalid!");
    }
}
