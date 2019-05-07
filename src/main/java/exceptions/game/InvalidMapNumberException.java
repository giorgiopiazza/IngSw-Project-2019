package exceptions.game;

import exceptions.AdrenalinaException;

public class InvalidMapNumberException extends AdrenalinaException {
    public InvalidMapNumberException() {
        super("You can choose the map only with numbers from 0 to 4!");
    }
}
