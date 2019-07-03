package exceptions.map;

import exceptions.AdrenalinaRuntimeException;

public class MapUnknowException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 1785131836661844584L;

    public MapUnknowException() {
        super("map number shall be 1 <= n <= 4");
    }
}
