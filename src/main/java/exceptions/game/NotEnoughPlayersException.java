package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class NotEnoughPlayersException extends AdrenalinaRuntimeException {

    public NotEnoughPlayersException() {
        super("the minimum number of players is 3");
    }
}
