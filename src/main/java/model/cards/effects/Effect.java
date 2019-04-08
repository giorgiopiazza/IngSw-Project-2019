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

    /**
     * Method that executes the effect of a Weapon dealing, marking or moving TargetPlayers
     *
     * @param firingAction contains informations of how and on who the effect is executed
     * @param playerDealer the Player who uses the Weapon's effect
     * @throws AdrenalinaException exception thrown in case the Weapon is not charged
     */
    public abstract void execute(FiringAction firingAction, Player playerDealer) throws AdrenalinaException;
}
