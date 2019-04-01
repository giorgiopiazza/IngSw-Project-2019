package exceptions;

public abstract class AdrenalinaRuntimeException extends RuntimeException {
    public AdrenalinaRuntimeException() {
    }

    public AdrenalinaRuntimeException(String message) {
        super(message);
    }
}
