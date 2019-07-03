package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class MissingCardException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 8795085580782091493L;

    public MissingCardException(String cn) {
        super("Acting player has not " + cn + " card!");
    }
}
