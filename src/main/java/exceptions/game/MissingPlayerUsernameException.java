package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class MissingPlayerUsernameException extends AdrenalinaRuntimeException {
    public MissingPlayerUsernameException(String username) {
        super("There exists no player with username: " + username);
    }
}
