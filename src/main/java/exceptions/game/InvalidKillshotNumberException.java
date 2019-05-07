package exceptions.game;

import exceptions.AdrenalinaException;

public class InvalidKillshotNumberException extends AdrenalinaException {
    public InvalidKillshotNumberException() {
        super("Killshot number must be >= 5 and <= 8");
    }
}
