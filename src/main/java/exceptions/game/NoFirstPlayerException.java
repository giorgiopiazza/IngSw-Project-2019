package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class NoFirstPlayerException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -7276777581651874832L;

    public NoFirstPlayerException() {
        super("The game must have a first player");
    }
}
