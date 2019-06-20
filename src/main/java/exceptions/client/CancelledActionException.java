package exceptions.client;

import exceptions.AdrenalinaException;

public class CancelledActionException extends AdrenalinaException {
    public CancelledActionException() {
        super("Action was cancelled!");
    }
}
