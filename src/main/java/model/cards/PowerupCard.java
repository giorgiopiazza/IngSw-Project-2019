package model.cards;

import enumerations.Color;
import model.cards.effects.Effect;

import java.io.File;

public class PowerupCard extends UsableCard {
    private Color color;

    public PowerupCard(String name, File image, Color color, Effect baseEffect) {
        super(name, image, baseEffect);
        this.color = color;
    }
}
