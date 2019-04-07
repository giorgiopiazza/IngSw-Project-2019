package model.cards;

import enumerations.Color;
import model.cards.effects.Effect;

import java.io.File;

public class PowerupCard extends UsableCard {
    public PowerupCard(String name, File image, Color color, Effect baseEffect) {
        super(name, image, color, baseEffect);
    }
}
