package model.cards.effects;

import model.player.Player;

public abstract class ExtraEffectDecorator extends Effect {
    protected Effect effect;

    @Override
    public abstract void execute(Player playerDealer);
}
