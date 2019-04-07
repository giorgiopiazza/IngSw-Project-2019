package exceptions.cards;

import exceptions.AdrenalinaException;

public class WeaponNotChargedException extends AdrenalinaException {
    public WeaponNotChargedException() {
        super("The weapon is not charged");
    }

    public WeaponNotChargedException(String wn) {
        super("The weapon " + wn + " is not charged");
    }
}
