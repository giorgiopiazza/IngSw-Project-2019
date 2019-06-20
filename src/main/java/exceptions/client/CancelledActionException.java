package exceptions.client;

import exceptions.AdrenalinaRuntimeException;

public class CancelledActionException extends AdrenalinaRuntimeException {
    public CancelledActionException() {
        super("Action was cancelled!");
    }
}
