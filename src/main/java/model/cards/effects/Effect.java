package model.cards.effects;

import enumerations.TargetType;
import model.player.AmmoQuantity;
import network.message.EffectRequest;

import java.io.Serializable;
import java.util.*;

/**
 * This is the main abstract class from which starts all the Decorator Pattern used to
 * implement the effects of the {@link model.cards.WeaponCard Weapons} and {@link model.cards.PowerupCard
 * Powerups}
 */
public abstract class Effect implements Serializable {
    private static final long serialVersionUID = 992667210434983695L;

    protected AmmoQuantity cost;
    private Map<String, String> properties;
    protected TargetType[] targets;
    protected String description;

    /**
     * @return the Properties of the effect
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * @return an Array containing the kind of targets of the effect
     */
    public TargetType[] getTargets() {
        return this.targets;
    }

    /**
     * @return the description of the effect
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the cost of the effect represented with the {@link AmmoQuantity AmmoQuantity}
     */
    public AmmoQuantity getCost() {
        return cost;
    }

    /**
     * Method that sets the properties to the decoratinf effect
     * @param properties
     */
    public void setProperties(Map<String, String> properties) {
        if (properties != null) {
            this.properties = new LinkedHashMap<>(properties);
        } else {
            this.properties = new LinkedHashMap<>();
        }
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
