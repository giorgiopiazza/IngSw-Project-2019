package exceptions.game;

import exceptions.AdrenalinaException;

public class GameNotReadyException extends AdrenalinaException {
    private static final long serialVersionUID = 2405986660882997264L;

    public GameNotReadyException() {
        super("Game is not ready to start");
    }
}
