package exceptions.client;

import exceptions.AdrenalinaException;

public class CancelledActionException extends AdrenalinaException {
    private static final long serialVersionUID = 6493045328876126704L;

    public CancelledActionException() {
        super("Action was cancelled!");
    }
}
