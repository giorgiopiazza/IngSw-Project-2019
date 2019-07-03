package exceptions.playerboard;

import exceptions.AdrenalinaRuntimeException;

public class InvalidDamageException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 5063734218231002521L;

    public InvalidDamageException() {
        super("A playerBoard can have maximum 12 damages!");
    }
}
