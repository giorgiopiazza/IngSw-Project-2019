package exceptions.actions;

import exceptions.AdrenalinaException;

public class InvalidActionException extends AdrenalinaException {
    public InvalidActionException() {
        super("This action is not Valid!");
    }
}
