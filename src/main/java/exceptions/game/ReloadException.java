package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class ReloadException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -1580784122397571697L;

    public ReloadException() {
        super("Something went wrong while trying to reload a Game!");
    }
}
