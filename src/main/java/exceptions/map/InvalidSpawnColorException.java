package exceptions.map;

import enumerations.Color;
import exceptions.AdrenalinaRuntimeException;

public class InvalidSpawnColorException extends AdrenalinaRuntimeException {
    public InvalidSpawnColorException(Color sc) {
        super("There are no spawn square in a room of color: " + sc.toString());
    }
}
