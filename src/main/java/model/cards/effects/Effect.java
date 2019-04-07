package model.cards.effects;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import model.cards.FiringAction;
import model.player.Player;

public abstract class Effect {

    private final Ammo[] cost;

    public Effect(Ammo[] cost) {
        this.cost = cost;
    }

    public Ammo[] getCost() {
        return this.cost;
    }

    public abstract void execute(FiringAction firingAction, Player playerDealer) throws AdrenalinaException;
}
