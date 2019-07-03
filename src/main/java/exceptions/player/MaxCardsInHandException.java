package exceptions.player;

import exceptions.AdrenalinaException;

public class MaxCardsInHandException extends AdrenalinaException {
    private static final long serialVersionUID = -8843520111621056974L;

    public MaxCardsInHandException(String cardType) {
        super("Reached max number of" + cardType + "cards in your hand");
    }
}
