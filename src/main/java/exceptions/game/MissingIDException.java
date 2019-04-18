package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class MissingIDException extends AdrenalinaRuntimeException {
    public MissingIDException(int id) {
        super("There exists no player with ID: " + id);
    }
}
