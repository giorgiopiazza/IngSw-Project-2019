package exceptions.playerboard;

import exceptions.AdrenalinaException;

public class BoardMaxAmmoException extends AdrenalinaException {
    public BoardMaxAmmoException() {
        super("Reached max ammo number for this color");
    }
}
