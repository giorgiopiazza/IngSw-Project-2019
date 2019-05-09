package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class InvalidGameStateException extends AdrenalinaRuntimeException {
    public InvalidGameStateException() {
        super("The state of the game is not correct!");
    }
}
