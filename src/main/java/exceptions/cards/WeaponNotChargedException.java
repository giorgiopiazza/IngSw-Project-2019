package exceptions.cards;

import exceptions.AdrenalinaRuntimeException;

public class WeaponNotChargedException extends AdrenalinaRuntimeException {
    public WeaponNotChargedException(String wn) {
        super("The weapon " + wn + " is not charged");
    }
}
