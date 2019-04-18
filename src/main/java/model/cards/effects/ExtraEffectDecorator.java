package model.cards.effects;

import enumerations.Ammo;

public abstract class ExtraEffectDecorator extends Effect {
    protected Effect effect;

    @Override
    public abstract void execute(String command);
}
