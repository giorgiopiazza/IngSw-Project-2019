package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class MissingPlayerIDException extends AdrenalinaRuntimeException {
    public MissingPlayerIDException(int id) {
        super("There exists no player with ID: " + id);
    }
}
