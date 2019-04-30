package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class NoFirstPlayerException extends AdrenalinaRuntimeException {
    public NoFirstPlayerException() {
        super("The game must have a first player");
    }
}
