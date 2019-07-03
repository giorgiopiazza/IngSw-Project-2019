package exceptions;

public abstract class AdrenalinaRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 7933138084749407097L;

    public AdrenalinaRuntimeException(String message) {
        super(message);
    }
}
