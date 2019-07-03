package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class MaxSquareWeaponsException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 3198361349384994701L;

    public MaxSquareWeaponsException() {
        super("Reached max number of weapons for the square");
    }
}
