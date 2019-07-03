package exceptions.file;

import exceptions.AdrenalinaRuntimeException;

public class JsonFileNotFoundException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -8584055366741332953L;

    public JsonFileNotFoundException() {
        super("file not found");
    }

    public JsonFileNotFoundException(String message) {
        super(message);
    }
}
