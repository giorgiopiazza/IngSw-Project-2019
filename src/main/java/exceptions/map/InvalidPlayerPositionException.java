package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class InvalidPlayerPositionException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 2520579111584088248L;

    public InvalidPlayerPositionException() {
        super("Invalid square from PlayerPosition");
    }
}
