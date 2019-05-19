package exceptions.map;

import exceptions.AdrenalinaException;

public class InvalidSpawnColorException extends AdrenalinaException {
    public InvalidSpawnColorException() {
        super("There are no spawn square in a room of this color");
    }
}
