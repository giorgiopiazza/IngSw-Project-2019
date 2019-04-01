package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class KillShotsTerminatedException extends AdrenalinaRuntimeException {
    public KillShotsTerminatedException() {
        super("there are no more skulls left");
    }
}
