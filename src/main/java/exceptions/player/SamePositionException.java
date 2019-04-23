package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class SamePositionException extends AdrenalinaRuntimeException {
    public SamePositionException() {
        super("Same positions do not identify any direction");
    }
}
