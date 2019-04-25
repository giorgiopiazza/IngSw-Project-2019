package exceptions.file;

import exceptions.AdrenalinaRuntimeException;

public class JsonFileNotFoundException extends AdrenalinaRuntimeException {
    public JsonFileNotFoundException() {
        super("file not found");
    }

    public JsonFileNotFoundException(String message) {
        super(message);
    }
}
