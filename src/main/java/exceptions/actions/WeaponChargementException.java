package exceptions.actions;

import exceptions.AdrenalinaRuntimeException;

public class WeaponChargementException extends AdrenalinaRuntimeException {
    public WeaponChargementException() {
        super("Weapon should be charged in an other way!");
    }
}
