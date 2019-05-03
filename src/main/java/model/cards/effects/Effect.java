package model.cards.effects;

import enumerations.TargetType;
import exceptions.AdrenalinaException;
import network.message.EffectRequest;

import java.util.Map;

public abstract class Effect {
    private Map<String, String> properties;
    private TargetType[] targets;

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void setTargets(TargetType[] targets) {
        this.targets = targets;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public TargetType[] getTargets() {
        return this.targets;
    }

    /**
     * Method that executes the effect of a Weapon dealing, marking or moving TargetPlayers
     *
     * @param request that will be executed
     */
    public abstract void execute(EffectRequest request);

    /**
     * Executes the target validation of the command
     *
     * @param request of effect
     * @return {@code true} if the command is valid, {@code false} otherwise
     */
    public abstract boolean validate(EffectRequest request);
}
