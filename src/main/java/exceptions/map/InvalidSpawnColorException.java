package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class InvalidSpawnColorException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 5693597162495335305L;

    public InvalidSpawnColorException() {
        super("There are no spawn square in a room of this color");
    }
}
