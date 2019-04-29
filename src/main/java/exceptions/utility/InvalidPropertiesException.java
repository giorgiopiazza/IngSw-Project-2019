package exceptions.utility;

import exceptions.AdrenalinaRuntimeException;

public class InvalidPropertiesException extends AdrenalinaRuntimeException {
    public InvalidPropertiesException() {
        super("The weapon properties parsed are not valid");
    }
}
