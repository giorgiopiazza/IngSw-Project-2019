package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class InvalidPlayerPositionException extends AdrenalinaRuntimeException {
    public InvalidPlayerPositionException() {
        super("Invalid square from PlayerPosition");
    }
}
