package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import enumerations.SquareType;

public abstract class Square {
    private final Color color;
    private final SquareType squareType;
    private final SquareAdjacency north;
    private final SquareAdjacency east;
    private final SquareAdjacency south;
    private final SquareAdjacency west;

    public Square(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west, SquareType squareType) {
        this.color = color;
        this.squareType = squareType;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public Color getColor() {
        return color;
    }

    public SquareType getSquareType() {
        return squareType;
    }

    public SquareAdjacency getNorth() {
        return north;
    }

    public SquareAdjacency getEast() {
        return east;
    }

    public SquareAdjacency getSouth() {
        return south;
    }

    public SquareAdjacency getWest() {
        return west;
    }
}
