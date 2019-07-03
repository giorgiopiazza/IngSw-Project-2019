package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class KillShotsTerminatedException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 7571825815609529031L;

    public KillShotsTerminatedException() {
        super("there are no more skulls left");
    }
}
