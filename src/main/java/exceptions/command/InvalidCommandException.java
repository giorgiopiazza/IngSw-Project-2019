package exceptions.command;

import exceptions.AdrenalinaRuntimeException;

public class InvalidCommandException extends AdrenalinaRuntimeException {
    public InvalidCommandException() {
        super("The command is not valid!");
    }
}

