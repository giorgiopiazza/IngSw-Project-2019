package exceptions.game;

import exceptions.AdrenalinaException;

public class InvalidMapNumberException extends AdrenalinaException {
    private static final long serialVersionUID = 6487916800451201276L;

    public InvalidMapNumberException() {
        super("You can choose the map only with numbers from 0 to 4!");
    }
}
