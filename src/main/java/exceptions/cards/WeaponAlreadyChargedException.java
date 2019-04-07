package exceptions.cards;

import exceptions.AdrenalinaException;

public class WeaponAlreadyChargedException extends AdrenalinaException {
    public WeaponAlreadyChargedException() {
        super("The weapon is already charged");
    }

    public WeaponAlreadyChargedException(String wn) {
        super("The weapon " + wn + " is already charged");
    }
}
