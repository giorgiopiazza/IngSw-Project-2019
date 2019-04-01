package exceptions.player;

import exceptions.AdrenalinaException;

public class MaxCardsInHandException extends AdrenalinaException {
    public MaxCardsInHandException(String cardType) {
        super("Reached max number of" + cardType +"cards in your hand");
    }
}
