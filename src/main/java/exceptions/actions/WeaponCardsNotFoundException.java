package exceptions.actions;

import exceptions.AdrenalinaException;

public class WeaponCardsNotFoundException extends AdrenalinaException {

    private static final long serialVersionUID = -8388237800033454282L;

    public WeaponCardsNotFoundException(String message) {
        super(message);
    }

    public WeaponCardsNotFoundException() {
        super("no weapon cards found");
    }

}
