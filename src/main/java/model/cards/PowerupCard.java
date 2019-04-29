package model.cards;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import model.cards.effects.Effect;

import java.io.File;
import java.util.Objects;

public class PowerupCard extends UsableCard {
    private final Ammo value;

    public Ammo getValue() {
        return this.value;
    }

    public PowerupCard(String name, File image, Ammo value, Effect baseEffect) {
        super(name, image, baseEffect);
        this.value = value;
    }

    @Override
    public void use(String command) throws AdrenalinaException {
        // TODO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PowerupCard that = (PowerupCard) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
