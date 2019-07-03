package exceptions.utility;

import exceptions.AdrenalinaRuntimeException;

public class InvalidPropertiesException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -6539575517006392369L;

    public InvalidPropertiesException() {
        super("The weapon properties parsed are not valid");
    }
}
