package model.cards.effects;

import exceptions.AdrenalinaException;
import network.message.EffectRequest;

public abstract class ExtraEffectDecorator extends Effect {
    /**
     * Decorator of the Effect used to add functionalities to a BaseEffect
     */
    protected Effect effect;

    @Override
    public abstract void execute(EffectRequest request);

    @Override
    public boolean validate(EffectRequest request) throws AdrenalinaException {
        return effect.validate(request);
    }
}
