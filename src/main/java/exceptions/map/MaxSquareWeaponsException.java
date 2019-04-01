package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class MaxSquareWeaponsException extends AdrenalinaRuntimeException {
    public MaxSquareWeaponsException() {
        super("Reached max number of weapons for the square");
    }
}
