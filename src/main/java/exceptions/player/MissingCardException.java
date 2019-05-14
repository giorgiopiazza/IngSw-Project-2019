package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class MissingCardException extends AdrenalinaRuntimeException {
    public MissingCardException(String cn) {
        super("Acting player has not " + cn + " card!");
    }
}
