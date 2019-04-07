package exceptions.cards;

import exceptions.AdrenalinaException;

public class DamageDistributionException extends AdrenalinaException {
    public DamageDistributionException() {
        super("Target list and Damage Distribution are incongruous");
    }
}
