package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class InvalidGameStateException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -7065713036865432413L;

    public InvalidGameStateException() {
        super("The state of the game is not correct!");
    }
}
