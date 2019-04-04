package exceptions.game;

import exceptions.AdrenalinaException;

public class TerminatorNotPresentException extends AdrenalinaException {

    public TerminatorNotPresentException() {
        super("Terminator not present in this game instance");
    }

}
