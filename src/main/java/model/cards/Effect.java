package model.cards;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import model.player.Player;

public abstract class Effect {

    private final Ammo[] cost;
    private Target target;

    public Effect(Ammo[] cost, Target target) {
        this.cost = cost;
        this.target = target;
    }

    public Ammo[] getCost() {
        return this.cost;
    }

    public Target getTarget() {
        return this.target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void execute(Target target, Player playerDealer) throws AdrenalinaException {
    }
}
