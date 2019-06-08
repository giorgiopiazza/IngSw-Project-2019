package model.cards.effects;

import enumerations.TargetType;
import network.message.EffectRequest;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public abstract class Effect implements Serializable {
    private static final long serialVersionUID = 992667210434983695L;

    private Map<String, String> properties;
    private TargetType[] targets;
    private String description;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Effect effect = (Effect) o;
        return Objects.equals(properties, effect.properties) &&
                Arrays.equals(targets, effect.targets) &&
                Objects.equals(description, effect.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(properties, description);
        result = 31 * result + Arrays.hashCode(targets);
        return result;
    }
}
