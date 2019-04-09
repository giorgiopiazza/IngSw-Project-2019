package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import model.cards.AmmoTile;

public class CardSquare extends Square {
    private AmmoTile ammoTile;

    public CardSquare(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west) {
        super(color, north, east, south, west);
        this.ammoTile = null;
    }

    public CardSquare(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west, AmmoTile ammoTile) {
        super(color, north, east, south, west);
        this.ammoTile = ammoTile;
    }

    public AmmoTile getAmmoTile() {
        return ammoTile;
    }

    public void setAmmoTile(AmmoTile ammoTile) {
        this.ammoTile = ammoTile;
    }
}
