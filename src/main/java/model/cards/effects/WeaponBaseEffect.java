package model.cards.effects;

import model.player.AmmoQuantity;

import java.util.Map;

public class WeaponBaseEffect extends Effect {
    // TODO we can add a description of the effect to give a better understanding of the weapon while playing with CLI
    private AmmoQuantity cost;

    public WeaponBaseEffect(AmmoQuantity cost, Map<String, String> properties) {
        setCost(cost);
        setProperties(properties);
    }

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

    @Override
    public void execute(String command) {
        // Basic Effect does nothing
    }

    @Override
    public boolean validate(String command) {
        return true;
    }
}
