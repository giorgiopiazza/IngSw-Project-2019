package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class NegativeQuantityException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -4775356771923095261L;

    public NegativeQuantityException() {
        super("The quantity of an Ammo can not be negative");
    }
}
