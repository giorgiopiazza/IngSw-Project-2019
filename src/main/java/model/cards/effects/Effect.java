package model.cards.effects;

import enumerations.TargetType;

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
     * @param command that will be executed
     */
    public abstract void execute(String command);

    /**
     * Executes the target validation of the command
     *
     * @param command sent
     * @return {@code true} if the command is valid, {@code false} otherwise
     */
    public abstract boolean validate(String command);
}
