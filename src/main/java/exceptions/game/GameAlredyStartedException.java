package exceptions.game;

import exceptions.AdrenalinaException;

public class GameAlredyStartedException extends AdrenalinaException {
    public GameAlredyStartedException(String s) {
        super(s);
    }
}
