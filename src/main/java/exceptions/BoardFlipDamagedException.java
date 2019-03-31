package exceptions;

public class BoardFlipDamagedException extends AdrenalinaException {
    public BoardFlipDamagedException() {
        super("Can't flip a board of a damaged player");
    }
}
