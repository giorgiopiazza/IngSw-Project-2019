package exceptions.game;

import exceptions.AdrenalinaException;

public class GameAlreadyStartedException extends AdrenalinaException {
    public GameAlreadyStartedException(String s) {
        super(s);
    }
}
