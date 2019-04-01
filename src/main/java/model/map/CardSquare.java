package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;

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
}
