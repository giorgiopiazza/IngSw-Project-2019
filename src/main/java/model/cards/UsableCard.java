package model.cards;

import exceptions.AdrenalinaException;
import model.cards.effects.Effect;
import network.message.EffectRequest;

import java.io.File;
import java.util.Objects;

public abstract class UsableCard extends Card {
    private final String name;
    private final Effect baseEffect;

    public UsableCard(String name, File image, Effect baseEffect) {
        super(image);
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
