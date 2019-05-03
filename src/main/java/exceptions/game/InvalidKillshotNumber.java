package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class InvalidKillshotNumber extends AdrenalinaRuntimeException {
    public InvalidKillshotNumber() {
        super("Killshot number must be >= 5 and <=8");
    }
}
