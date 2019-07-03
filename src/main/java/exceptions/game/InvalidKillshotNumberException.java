package exceptions.game;

import exceptions.AdrenalinaException;

public class InvalidKillshotNumberException extends AdrenalinaException {
    private static final long serialVersionUID = 4058769811714632919L;

    public InvalidKillshotNumberException() {
        super("Killshot number must be >= 5 and <= 8");
    }
}
