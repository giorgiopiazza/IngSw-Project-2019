package model.cards.effects;

import model.player.AmmoQuantity;

public class BaseEffect extends Effect {
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
