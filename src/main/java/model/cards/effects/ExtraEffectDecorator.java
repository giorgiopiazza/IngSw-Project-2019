package model.cards.effects;

public abstract class ExtraEffectDecorator extends Effect {
    /**
     * Decorator of the Effect used to add functionalities to a BaseEffect
     */
    protected Effect effect;

    @Override
    public abstract void execute(String command);

    @Override
    public boolean validate(String command) {
        return effect.validate(command);
    }
}
