package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class PlayerNotFoundException extends AdrenalinaRuntimeException {
    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException() {
        this("Player not found");
    }
}
