package exceptions.player;

public class FinalFrenzyException extends Throwable {
    private static final long serialVersionUID = 4809463560625353201L;

    public FinalFrenzyException(String message) {
        super(message);
    }

    public FinalFrenzyException() {
        this("The next move is a final frenzy action");
    }
}
