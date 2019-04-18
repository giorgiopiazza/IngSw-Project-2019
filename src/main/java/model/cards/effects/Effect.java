package model.cards.effects;

import enumerations.Ammo;

public abstract class Effect {
    /**
     * Method that executes the effect of a Weapon dealing, marking or moving TargetPlayers
     *
     * @param command that will be executed
     */
    public abstract void execute(String command);
}
