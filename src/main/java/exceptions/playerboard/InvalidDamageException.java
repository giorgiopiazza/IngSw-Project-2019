package exceptions.playerboard;

import exceptions.AdrenalinaRuntimeException;

public class InvalidDamageException extends AdrenalinaRuntimeException {
    public InvalidDamageException() {
        super("A playerBoard can have maximum 12 damages!");
    }
}
