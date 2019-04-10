package model.cards.effects;

import model.cards.Target;
import model.player.Player;

public abstract class Effect {

    protected Target target;

    public Target getTarget() {
        return this.target;
    }

    /**
     * Method that executes the effect of a Weapon dealing, marking or moving TargetPlayers
     *
     * @param playerDealer the Player who uses the Weapon's effect
     */
    public abstract void execute(Player playerDealer);
}
