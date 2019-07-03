package exceptions.cards;

import exceptions.AdrenalinaException;

public class WeaponAlreadyChargedException extends AdrenalinaException {
    private static final long serialVersionUID = 8517735937126961770L;

    public WeaponAlreadyChargedException() {
        super("The weapon is already charged");
    }

    public WeaponAlreadyChargedException(String wn) {
        super("The weapon " + wn + " is already charged");
    }
}
