package model.cards.effects;

import enumerations.Ammo;

public class BaseEffect extends Effect {
    private final Ammo[] cost;

    public BaseEffect(Ammo[] cost) {
        this.cost = cost;
    }

    public Ammo[] getCost() {
        return this.cost;
    }

    @Override
    public void execute(String command) {

    }
}
