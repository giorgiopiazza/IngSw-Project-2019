package model.cards;

import enumerations.Color;

public abstract class Card {
    private final String name;
    private final Color color;
    private final Effect baseEffect;

    public Card(String name, Color color, Effect baseEffect) {
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
