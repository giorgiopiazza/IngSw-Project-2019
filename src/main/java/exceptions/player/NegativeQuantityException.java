package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class NegativeQuantityException extends AdrenalinaRuntimeException {
    public NegativeQuantityException() {
        super("The quantity of an Ammo can not be negative");
    }
}
