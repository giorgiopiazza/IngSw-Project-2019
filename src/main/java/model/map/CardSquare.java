package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;

import java.util.Objects;

public class CardSquare extends Square {
    private AmmoCard ammoCard;

    public CardSquare(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west) {
        super(color, north, east, south, west);
    }

    public CardSquare(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west, AmmoCard ammoCard) {
        super(color, north, east, south, west);
        this.ammoCard = ammoCard;
    }

    public AmmoCard getAmmoCard() {
        return ammoCard;
    }

    public void setAmmoCard(AmmoCard ammoCard) {
        this.ammoCard = ammoCard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardSquare)) return false;
        if (!super.equals(o)) return false;
        CardSquare that = (CardSquare) o;
        return Objects.equals(getAmmoCard(), that.getAmmoCard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAmmoCard());
    }
}
