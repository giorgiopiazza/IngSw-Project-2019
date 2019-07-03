package exceptions.network;

import exceptions.AdrenalinaRuntimeException;

public class ClassAdrenalinaNotFoundException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 34862966065681624L;

    public ClassAdrenalinaNotFoundException() {
        this("Class not found in default package");
    }

    public ClassAdrenalinaNotFoundException(String message) {
        super(message);
    }
}
