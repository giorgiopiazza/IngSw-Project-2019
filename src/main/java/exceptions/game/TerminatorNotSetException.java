package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class TerminatorNotSetException extends AdrenalinaRuntimeException {
    public TerminatorNotSetException(String message) {
        super(message);
    }

    public TerminatorNotSetException() {
        this("Terminator not present in this game instance");
    }
}
