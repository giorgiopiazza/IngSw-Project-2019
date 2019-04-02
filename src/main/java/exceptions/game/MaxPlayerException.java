package exceptions.game;

import exceptions.AdrenalinaException;

public class MaxPlayerException extends AdrenalinaException {
    public MaxPlayerException() {
        super("Reached max number of players");
    }
    public MaxPlayerException(String s) { super(s); }
}
