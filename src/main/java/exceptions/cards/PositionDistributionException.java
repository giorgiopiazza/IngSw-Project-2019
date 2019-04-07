package exceptions.cards;

import exceptions.AdrenalinaException;

public class PositionDistributionException extends AdrenalinaException {
    public PositionDistributionException() {
        super("Target list and Position Distribution are incongruous");
    }
}
