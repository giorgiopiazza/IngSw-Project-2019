package exceptions.game;

import exceptions.AdrenalinaException;

public class InexistentColorException extends AdrenalinaException {
    public InexistentColorException(String c) {
        super("The color " + c + " you are trying to use does not exist!");
    }
}
