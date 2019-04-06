package exceptions.cards;

import exceptions.AdrenalinaException;

public class TooManyDamageException extends AdrenalinaException {
    public TooManyDamageException(int wd) {
        super("You are trying to do more than the weapons' damage: " + wd);
    }
}
