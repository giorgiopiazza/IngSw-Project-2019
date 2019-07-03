package exceptions;

public abstract class AdrenalinaException extends Exception {
    private static final long serialVersionUID = 1798334692067855764L;

    public AdrenalinaException(String message) {
        super(message);
    }
}
