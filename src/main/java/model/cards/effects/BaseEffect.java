package model.cards.effects;

import model.player.AmmoQuantity;

public class BaseEffect extends Effect {
    // TODO we can add a description of the effect to give a better understanding of the weapon while playing with CLI

    public BaseEffect(AmmoQuantity cost) {
        setCost(cost);
    }

    @Override
    public void execute(String command) {
        // Basic Effect does nothing
    }

    @Override
    public boolean validate(String command) {
        return true;
    }
}
