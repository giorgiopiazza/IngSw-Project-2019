package exceptions.actions;

import exceptions.AdrenalinaRuntimeException;

public class InvalidActionException extends AdrenalinaRuntimeException {
    public InvalidActionException() {
        super("This action is not Valid!");
    }
}
