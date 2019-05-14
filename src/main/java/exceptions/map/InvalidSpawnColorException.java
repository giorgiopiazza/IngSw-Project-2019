package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class InvalidSpawnColorException extends AdrenalinaRuntimeException {
    public InvalidSpawnColorException(String color) {
        super("There are no spawn square in a room of color: " + color);
    }
}
