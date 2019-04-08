package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class CardAlreadyInHandException extends AdrenalinaRuntimeException {
    public CardAlreadyInHandException(String cn) {
        super("You already have the " + cn + "in your hand!");
    }
}
