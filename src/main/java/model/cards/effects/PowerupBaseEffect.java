package model.cards.effects;

import enumerations.TargetType;

import java.util.Map;

public class PowerupBaseEffect extends Effect {
    private final int cost;     // or a boolean, in this way is more general for effects that cost more than one ammo

    public PowerupBaseEffect(Map<String, String> properties, TargetType[] targets) {
        this.cost = 0;
        setTargets(targets);
        setProperties(properties);
    }

    public PowerupBaseEffect(int generalCost, Map<String, String> properties, TargetType[] targets) {
        this.cost = generalCost;
        setTargets(targets);
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
