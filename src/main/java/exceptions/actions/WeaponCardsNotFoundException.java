package exceptions.actions;

import exceptions.AdrenalinaException;

public class WeaponCardsNotFoundException extends AdrenalinaException {

    public WeaponCardsNotFoundException(String message) {
        super(message);
    }

    public WeaponCardsNotFoundException() {
        super("no weapon cards found");
    }

}
