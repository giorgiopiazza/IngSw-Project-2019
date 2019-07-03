package exceptions.actions;

import exceptions.AdrenalinaRuntimeException;

public class WeaponChargementException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 1307638808733745023L;

    public WeaponChargementException() {
        super("Weapon should be charged in an other way!");
    }
}
