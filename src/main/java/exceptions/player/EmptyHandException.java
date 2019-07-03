package exceptions.player;

import exceptions.AdrenalinaException;

public class EmptyHandException extends AdrenalinaException {
    private static final long serialVersionUID = 6377255740259624L;

    public EmptyHandException(String cardType) {
        super("Your hand has no " + cardType);
    }
}
