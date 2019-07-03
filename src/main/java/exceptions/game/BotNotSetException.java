package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class BotNotSetException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -110421225347286817L;

    public BotNotSetException(String message) {
        super(message);
    }
    public BotNotSetException() {
        this("Terminator not present in this game instance");
    }
}
