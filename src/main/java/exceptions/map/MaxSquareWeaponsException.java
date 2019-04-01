package exceptions.map;

import exceptions.AdrenalinaException;

public class MaxSquareWeaponsException extends AdrenalinaException {
    public MaxSquareWeaponsException() {
        super("Reached max number of weapons for the square");
    }
}
