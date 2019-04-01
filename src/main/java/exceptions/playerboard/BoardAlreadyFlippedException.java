package exceptions.playerboard;

import exceptions.AdrenalinaException;

public class BoardAlreadyFlippedException extends AdrenalinaException {
    public BoardAlreadyFlippedException() {
        super("Can't flip the board because is already flipped");
    }
}
