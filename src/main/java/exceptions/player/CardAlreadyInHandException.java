package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class CardAlreadyInHandException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -1874201616595122154L;

    public CardAlreadyInHandException(String cn) {
        super("You already have the " + cn + "in your hand!");
    }
}
