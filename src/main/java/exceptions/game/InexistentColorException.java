package exceptions.game;

import exceptions.AdrenalinaException;

public class InexistentColorException extends AdrenalinaException {
    private static final long serialVersionUID = 7141398020647838859L;

    public InexistentColorException(String c) {
        super("The color " + c + " you are trying to use does not exist!");
    }
}
