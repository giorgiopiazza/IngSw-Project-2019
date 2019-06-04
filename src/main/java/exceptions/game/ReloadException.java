package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class ReloadException extends AdrenalinaRuntimeException {
    public ReloadException() {
        super("Something went wrong while trying to reload a Game!");
    }
}
