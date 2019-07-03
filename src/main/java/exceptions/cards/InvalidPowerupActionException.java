package exceptions.cards;

import exceptions.AdrenalinaException;

public class InvalidPowerupActionException extends AdrenalinaException {
    private static final long serialVersionUID = -6337743962765353804L;

    public InvalidPowerupActionException() {
        super("The action of the powerup is invalid!");
    }
}
