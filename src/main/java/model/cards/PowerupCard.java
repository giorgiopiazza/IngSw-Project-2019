package model.cards;

import enumerations.Ammo;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PowerupCard)) return false;
        PowerupCard that = (PowerupCard) o;
        return super.equals(that) && this.value.equals(that.value);
    }
}
