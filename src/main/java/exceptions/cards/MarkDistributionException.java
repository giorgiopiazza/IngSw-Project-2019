package exceptions.cards;

import exceptions.AdrenalinaException;

public class MarkDistributionException extends AdrenalinaException {
    public MarkDistributionException() {
        super("Target list and Mark Distribution are incongruous");
    }
}
