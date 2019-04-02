package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class UnknownPlayerException extends AdrenalinaRuntimeException {
    public UnknownPlayerException () {
        super("Player not found in game");
    }
}
