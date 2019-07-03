package exceptions.command;

import exceptions.AdrenalinaRuntimeException;

public class InvalidCommandException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -2767685304255542031L;

    public InvalidCommandException() {
        super("The command is not valid!");
    }
}

