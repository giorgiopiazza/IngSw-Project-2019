package exceptions.player;

import exceptions.AdrenalinaRuntimeException;

public class PlayerNotFoundException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 4550517641321007604L;

    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException() {
        this("Player not found");
    }
}
