package exceptions;

public abstract class AdrenalinaException extends Exception {
    public AdrenalinaException() { super(); }

    public AdrenalinaException(String message) {
        super(message);
    }

    public AdrenalinaException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdrenalinaException(Throwable cause) {
        super(cause);
    }

    public AdrenalinaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
