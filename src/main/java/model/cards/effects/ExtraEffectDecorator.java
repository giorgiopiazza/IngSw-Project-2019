package model.cards.effects;

import network.message.EffectRequest;

public abstract class ExtraEffectDecorator extends Effect {
    private static final long serialVersionUID = -3143503212164027705L;
    /**
     * Decorator of the Effect used to add functionalities to a BaseEffect
     */
    protected Effect effect;

    @Override
    public abstract void execute(EffectRequest request);

    @Override
    public boolean validate(EffectRequest request) {
        return effect.validate(request);
    }
}
