package model.cards;

import enumerations.Color;
import model.cards.effects.Effect;

import java.io.File;

public abstract class UsableCard extends Card {
    private final String name;
    private final Color color;
    private final Effect baseEffect;

    public UsableCard(String name, File image, Color color, Effect baseEffect) {
        super(image);
        this.name = name;
        this.color = color;
        this.baseEffect = baseEffect;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public Effect getBaseEffect() {
        return this.baseEffect;
    }
}
