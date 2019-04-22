package model.cards.effects;

import java.util.Map;

public class PowerupBaseEffect extends Effect {
    private final int cost;     // or a boolean, in this way is more general for effects that cost more than one ammo

    public PowerupBaseEffect(Map<String, String> properties) {
        this.cost = 0;
        setProperties(properties);
    }

    public PowerupBaseEffect(int generalCost, Map<String, String> properties) {
        this.cost = generalCost;
        setProperties(properties);
    }

    public int getCost() {
        return this.cost;
    }

    @Override
    public void execute(String command) {
        // basic effect does nothing
    }

    @Override
    public boolean validate(String command) {
        return true;
    }
}
