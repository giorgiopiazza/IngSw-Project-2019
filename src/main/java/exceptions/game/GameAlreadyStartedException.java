package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class GameAlreadyStartedException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -3659795793294203803L;

    public GameAlreadyStartedException(String s) {
        super(s);
    }
}
