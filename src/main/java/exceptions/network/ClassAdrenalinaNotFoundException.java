package exceptions.network;

import exceptions.AdrenalinaRuntimeException;

public class ClassAdrenalinaNotFoundException extends AdrenalinaRuntimeException {
    public ClassAdrenalinaNotFoundException() {
        this("Class not found in default package");
    }

    public ClassAdrenalinaNotFoundException(String message) {
        super(message);
    }
}
