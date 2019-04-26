package model.cards;

import model.cards.effects.Effect;

import java.io.File;

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
        if (!(o instanceof UsableCard)) return false;
        UsableCard that = (UsableCard) o;
        return getName().equals(that.getName()) &&
                getBaseEffect().equals(that.getBaseEffect());
    }
}
