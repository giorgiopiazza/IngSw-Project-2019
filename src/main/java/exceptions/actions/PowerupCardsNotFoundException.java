package exceptions.actions;

import exceptions.AdrenalinaException;

public class PowerupCardsNotFoundException extends AdrenalinaException {

    public PowerupCardsNotFoundException(String message) {
        super(message);
    }

    public PowerupCardsNotFoundException() {
        super("no powerup cards found");
    }

}
