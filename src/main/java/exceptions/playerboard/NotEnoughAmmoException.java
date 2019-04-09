package exceptions.playerboard;

import exceptions.AdrenalinaException;

public class NotEnoughAmmoException extends AdrenalinaException {
    public NotEnoughAmmoException() {
        super("Not enough ammo to do this action");
    }
}
