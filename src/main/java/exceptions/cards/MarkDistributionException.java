package exceptions.cards;

import exceptions.AdrenalinaException;

public class MarkDistributionException extends AdrenalinaException {
    private static final long serialVersionUID = 8344343314191625852L;

    public MarkDistributionException() {
        super("Target list and Mark Distribution are incongruous");
    }
}
