package exceptions.playerboard;

import exceptions.AdrenalinaException;

public class NotEnoughAmmoException extends AdrenalinaException {
    private static final long serialVersionUID = -2654175616636045856L;

    public NotEnoughAmmoException() {
        super("Not enough ammo to do this action");
    }
}
