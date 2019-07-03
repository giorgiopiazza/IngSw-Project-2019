package exceptions.cards;

import exceptions.AdrenalinaException;

public class WeaponNotChargedException extends AdrenalinaException {
    private static final long serialVersionUID = 5202042146297511204L;

    public WeaponNotChargedException() {
        super("The weapon is not charged");
    }

    public WeaponNotChargedException(String wn) {
        super("The weapon " + wn + " is not charged");
    }
}
