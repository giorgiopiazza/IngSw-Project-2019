package exceptions;

public class BoardMaxAmmoException extends AdrenalinaException {
    public BoardMaxAmmoException() {
        super("Reached max ammo number for this color");
    }
}
