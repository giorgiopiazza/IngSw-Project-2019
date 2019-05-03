package exceptions.game;

import exceptions.AdrenalinaException;

public class GameNotReadyException extends AdrenalinaException {
    public GameNotReadyException() {
        super("Game is not ready to start");
    }
}
