package exceptions.playerboard;

import exceptions.AdrenalinaException;

public class BoardMaxAmmoException extends AdrenalinaException {
    private static final long serialVersionUID = 9091609882254371385L;

    public BoardMaxAmmoException() {
        super("Reached max ammo number for this color");
    }
}
