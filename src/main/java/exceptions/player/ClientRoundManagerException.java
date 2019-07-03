package exceptions.player;

public class ClientRoundManagerException extends RuntimeException {
    private static final long serialVersionUID = 6177400492461045207L;

    public ClientRoundManagerException() {
        this("Exception in ClientRoundManager");
    }

    public ClientRoundManagerException(String message) {
        super(message);
    }
}
