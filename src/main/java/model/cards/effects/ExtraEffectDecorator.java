package model.cards.effects;

import network.message.EffectRequest;

import java.util.Objects;

/**
 * Decorator of the Effect used to add functionalities to a BaseEffect
 */
public abstract class ExtraEffectDecorator extends Effect {
    private static final long serialVersionUID = -3143503212164027705L;

    protected Effect effect;

    @Override
    public abstract void execute(EffectRequest request);

    @Override
    public boolean validate(EffectRequest request) {
        return effect.validate(request);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtraEffectDecorator that = (ExtraEffectDecorator) o;
        return Objects.equals(effect, that.effect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effect);
    }
}
