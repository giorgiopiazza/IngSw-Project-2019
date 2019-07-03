package exceptions.cards;

import exceptions.AdrenalinaException;

public class PositionDistributionException extends AdrenalinaException {
    private static final long serialVersionUID = 5141010218580573869L;

    public PositionDistributionException() {
        super("Target list and Position Distribution are incongruous");
    }
}
