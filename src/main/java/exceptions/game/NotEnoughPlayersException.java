package exceptions.game;

import exceptions.AdrenalinaException;

public class NotEnoughPlayersException extends AdrenalinaException {

    public NotEnoughPlayersException() {
        super("the minimum number of players is 3");
    }

}
