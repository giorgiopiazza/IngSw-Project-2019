package model.cards;

import exceptions.AdrenalinaException;
import model.cards.effects.Effect;
import network.message.EffectRequest;

import java.util.Objects;

/**
 * This is the real class extended by a usable card as a WEAPON or POWERUP, remember that TILES are always only picked
 */
public abstract class UsableCard extends Card {
    private static final long serialVersionUID = 8862569195167166494L;

    private final String name;
    private final Effect baseEffect;

    public UsableCard(String name, String imagePath, Effect baseEffect) {
        super(imagePath);
        this.name = name;
        this.baseEffect = baseEffect;
    }

    public String getName() {
        return this.name;
    }

    public Effect getBaseEffect() {
        return this.baseEffect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsableCard that = (UsableCard) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, baseEffect);
    }

    public abstract void use(EffectRequest request) throws AdrenalinaException;
}
