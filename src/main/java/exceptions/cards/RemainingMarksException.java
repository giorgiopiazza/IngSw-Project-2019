package exceptions.cards;

import exceptions.AdrenalinaException;

public class RemainingMarksException extends AdrenalinaException {
    public RemainingMarksException(int ms) {
        super("This weapon sets more marks than: " + ms);
    }
}
