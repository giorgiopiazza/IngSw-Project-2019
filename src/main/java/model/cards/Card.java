package model.cards;

import java.io.File;

public abstract class Card {
    private final File image;

    public Card(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }
}
