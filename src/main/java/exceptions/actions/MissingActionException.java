package exceptions.actions;

import exceptions.AdrenalinaRuntimeException;

public class MissingActionException extends AdrenalinaRuntimeException {
    public MissingActionException() {
        super("Action chosen is not available!");
    }
}
