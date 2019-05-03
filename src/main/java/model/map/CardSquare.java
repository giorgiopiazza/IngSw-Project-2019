package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import enumerations.SquareType;
import model.cards.AmmoTile;

public class CardSquare extends Square {
    private AmmoTile ammoTile;
    private boolean hasAmmoTile;

    public CardSquare(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west) {
        super(color, north, east, south, west, SquareType.TILE);
        this.ammoTile = null;
        this.hasAmmoTile = false;
    }

    public CardSquare(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west, AmmoTile ammoTile) {
        super(color, north, east, south, west, SquareType.TILE);
        this.ammoTile = ammoTile;
        this.hasAmmoTile = true;
    }

    public boolean isAmmoTilePresent() {
        return this.hasAmmoTile;
    }

    public AmmoTile pickAmmoTile() {
        this.hasAmmoTile = false;
        return ammoTile;
    }

    public void setAmmoTile(AmmoTile ammoTile) {
        this.ammoTile = ammoTile;
        this.hasAmmoTile = true;
    }
}
