package exceptions.player;

import exceptions.AdrenalinaException;

public class EmptyHandException extends AdrenalinaException {
    public EmptyHandException(String cardType) {
        super("Your hand has no " + cardType);
    }
}
