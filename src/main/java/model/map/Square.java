package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;

import java.util.Objects;

public abstract class Square {
    private Color color;
    private SquareAdjacency north;
    private SquareAdjacency east;
    private SquareAdjacency south;
    private SquareAdjacency west;

    public Square(Color color, SquareAdjacency north, SquareAdjacency east, SquareAdjacency south, SquareAdjacency west) {
        this.color = color;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public SquareAdjacency getNorth() {
        return north;
    }

    public void setNorth(SquareAdjacency north) {
        this.north = north;
    }

    public SquareAdjacency getEast() {
        return east;
    }

    public void setEast(SquareAdjacency east) {
        this.east = east;
    }

    public SquareAdjacency getSouth() {
        return south;
    }

    public void setSouth(SquareAdjacency south) {
        this.south = south;
    }

    public SquareAdjacency getWest() {
        return west;
    }

    public void setWest(SquareAdjacency west) {
        this.west = west;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square)) return false;
        Square square = (Square) o;
        return getColor() == square.getColor() &&
                getNorth() == square.getNorth() &&
                getEast() == square.getEast() &&
                getSouth() == square.getSouth() &&
                getWest() == square.getWest();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColor(), getNorth(), getEast(), getSouth(), getWest());
    }
}
