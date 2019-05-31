package exceptions.player;

public class ClientRoundManagerException extends RuntimeException {
    public ClientRoundManagerException() {
        this("Exception in ClientRoundManager");
    }

    public ClientRoundManagerException(String message) {
        super(message);
    }
}
