package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class MapUnknowException extends AdrenalinaRuntimeException {

    public MapUnknowException() {
        super("map number shall be 1 <= n <= 4");
    }

}
