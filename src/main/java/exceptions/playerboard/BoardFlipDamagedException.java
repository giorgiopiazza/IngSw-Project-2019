package exceptions.playerboard;

import exceptions.AdrenalinaException;

public class BoardFlipDamagedException extends AdrenalinaException {
    private static final long serialVersionUID = 7852625309062766490L;

    public BoardFlipDamagedException() {
        super("Can't flip a board of a damaged player");
    }
}
