package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class GameAlreadyStartedException extends AdrenalinaRuntimeException {
    public GameAlreadyStartedException(String s) {
        super(s);
    }
}
