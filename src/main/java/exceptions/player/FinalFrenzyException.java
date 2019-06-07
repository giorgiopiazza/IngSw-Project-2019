package exceptions.player;

public class FinalFrenzyException extends Throwable {
    public FinalFrenzyException(String message) {
        super(message);
    }

    public FinalFrenzyException() {
        this("The next move is a final frenzy action");
    }
}
