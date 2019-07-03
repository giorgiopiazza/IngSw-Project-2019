package exceptions.cards;

import exceptions.AdrenalinaException;

public class DamageDistributionException extends AdrenalinaException {
    private static final long serialVersionUID = -3394597832593892452L;

    public DamageDistributionException() {
        super("Target list and Damage Distribution are incongruous");
    }
}
