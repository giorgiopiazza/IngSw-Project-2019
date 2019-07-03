package exceptions.actions;

import exceptions.AdrenalinaException;

public class PowerupCardsNotFoundException extends AdrenalinaException {

    private static final long serialVersionUID = 8670458241856292104L;

    public PowerupCardsNotFoundException(String message) {
        super(message);
    }

    public PowerupCardsNotFoundException() {
        super("no powerup cards found");
    }

}
