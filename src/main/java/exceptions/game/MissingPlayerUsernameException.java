package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class MissingPlayerUsernameException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = 5112244190828666678L;

    public MissingPlayerUsernameException(String username) {
        super("There exists no player with username: " + username);
    }
}
