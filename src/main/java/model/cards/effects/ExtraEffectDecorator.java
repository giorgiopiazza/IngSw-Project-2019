package model.cards.effects;

public abstract class ExtraEffectDecorator extends Effect {
    protected Effect effect;

    @Override
    public abstract void execute(String command);
}
