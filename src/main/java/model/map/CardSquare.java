package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import model.cards.AmmoTile;

import java.util.Objects;

public class CardSquare extends Square {
    private AmmoTile ammoTile;

    public CardSquare(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west) {
        super(color, north, east, south, west);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardSquare)) return false;
        if (!super.equals(o)) return false;
        CardSquare that = (CardSquare) o;
        return Objects.equals(getAmmoTile(), that.getAmmoTile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAmmoTile());
    }
}
