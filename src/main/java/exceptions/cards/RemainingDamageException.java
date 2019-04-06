package exceptions.cards;

import exceptions.AdrenalinaException;

public class RemainingDamageException extends AdrenalinaException {
    public RemainingDamageException(int dd) {
        super("This weapon does more damage than: " + dd);
    }
}
