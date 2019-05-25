package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class MaxPlayerException extends AdrenalinaRuntimeException {
    public MaxPlayerException() {
        super("Reached max number of players");
    }
    public MaxPlayerException(String s) { super(s); }
}
