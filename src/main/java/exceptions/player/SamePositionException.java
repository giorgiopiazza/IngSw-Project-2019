package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class SamePositionException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -2442970646255002340L;

    public SamePositionException() {
        super("Same positions do not identify any direction");
    }
}
