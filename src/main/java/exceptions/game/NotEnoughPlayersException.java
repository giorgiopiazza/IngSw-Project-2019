package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class NotEnoughPlayersException extends AdrenalinaRuntimeException {

    private static final long serialVersionUID = 1202839744568608022L;

    public NotEnoughPlayersException() {
        super("the minimum number of players is 3");
    }
}
