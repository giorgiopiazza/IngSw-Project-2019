package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class MaxPlayerException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 9017006313560938498L;

    public MaxPlayerException() {
        super("Reached max number of players");
    }
    public MaxPlayerException(String s) { super(s); }
}
