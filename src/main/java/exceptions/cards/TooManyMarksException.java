package exceptions.cards;

import exceptions.AdrenalinaException;

public class TooManyMarksException extends AdrenalinaException {
    public TooManyMarksException(int wm) {
        super("You are trying to set more than the weapons' marks: " + wm);
    }
}
