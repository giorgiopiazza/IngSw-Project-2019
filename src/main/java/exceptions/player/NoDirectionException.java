package exceptions.player;

import exceptions.AdrenalinaException;

public class NoDirectionException extends AdrenalinaException {
    private static final long serialVersionUID = -8832286611754717622L;

    public NoDirectionException() {
        super("The target square is not in a valid direction");
    }
}
