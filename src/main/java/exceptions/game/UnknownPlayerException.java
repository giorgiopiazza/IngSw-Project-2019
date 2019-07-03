package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class UnknownPlayerException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -8936937817256131335L;

    public UnknownPlayerException () {
        super("Player not found in game");
    }
}
