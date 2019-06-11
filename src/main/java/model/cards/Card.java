package model.cards;

import java.io.File;
import java.io.Serializable;

public abstract class Card implements Serializable  {
    private static final long serialVersionUID = -3506586118929270253L;

    private final File image;

    public Card(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }
}
