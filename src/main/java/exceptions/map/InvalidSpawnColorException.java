package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class InvalidSpawnColorException extends AdrenalinaRuntimeException {
    public InvalidSpawnColorException() {
        super("There are no spawn square in a room of this color");
    }
}
