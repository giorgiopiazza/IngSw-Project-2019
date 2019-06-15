package model.cards;

import java.io.Serializable;

public abstract class Card implements Serializable  {
    private static final long serialVersionUID = -3506586118929270253L;

    protected final String imagePath;

    public Card(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
