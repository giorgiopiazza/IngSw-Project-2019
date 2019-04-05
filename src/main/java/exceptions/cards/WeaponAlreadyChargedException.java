package exceptions.cards;

import exceptions.AdrenalinaRuntimeException;

public class WeaponAlreadyChargedException extends AdrenalinaRuntimeException {
    public WeaponAlreadyChargedException(String wn) {
        super("The weapon " + wn + " is already charged");
    }
}
