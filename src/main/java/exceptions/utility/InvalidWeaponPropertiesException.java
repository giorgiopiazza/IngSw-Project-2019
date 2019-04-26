package exceptions.utility;

import exceptions.AdrenalinaRuntimeException;

public class InvalidWeaponPropertiesException extends AdrenalinaRuntimeException {
    public InvalidWeaponPropertiesException() {
        super("The weapon properties parsed are not valid");
    }
}
