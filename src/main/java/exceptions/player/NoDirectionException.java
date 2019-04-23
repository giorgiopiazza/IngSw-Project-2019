package exceptions.player;

import exceptions.AdrenalinaException;

public class NoDirectionException extends AdrenalinaException {
    public NoDirectionException() {
        super("The target square is not in a valid direction");
    }
}
