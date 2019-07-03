package exceptions.actions;

import exceptions.AdrenalinaException;

public class InvalidActionException extends AdrenalinaException {
    private static final long serialVersionUID = 7221304466680329512L;

    public InvalidActionException() {
        super("This action is not Valid!");
    }
}
