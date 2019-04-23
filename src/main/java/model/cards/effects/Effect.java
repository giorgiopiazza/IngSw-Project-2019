package model.cards.effects;

import model.player.AmmoQuantity;

public abstract class Effect {
    private AmmoQuantity cost;

    /**
     * Setter of the cost of an Effect
     *
     * @param cost the cost of the effect
     */
    void setCost(AmmoQuantity cost) { this.cost = cost; }

    /**
     * @return the cost of the Effect
     */
    public AmmoQuantity getCost() {
        return this.cost;
    }

    /**
     * Method that executes the effect of a Weapon dealing, marking or moving TargetPlayers
     *
     * @param command that will be executed
     */
    public abstract void execute(String command);

    /**
     * Executes the target validation of the command
     * @param command sent
     * @return {@code true} if the command is valid, {@code false} otherwise
     */
    public abstract boolean validate (String command);
}
