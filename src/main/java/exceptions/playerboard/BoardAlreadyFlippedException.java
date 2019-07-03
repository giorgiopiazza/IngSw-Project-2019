package exceptions.playerboard;

import exceptions.AdrenalinaException;

public class BoardAlreadyFlippedException extends AdrenalinaException {
    private static final long serialVersionUID = -4817987502872593321L;

    public BoardAlreadyFlippedException() {
        super("Can't flip the board because is already flipped");
    }
}
