package model.cards;

import java.io.File;
import java.util.Objects;

public abstract class Card {
    private final File image;

    public Card(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(image, card.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image);
    }
}
