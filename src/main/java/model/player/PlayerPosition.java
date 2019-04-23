package model.player;

import enumerations.Direction;
import enumerations.SquareAdjacency;
import exceptions.player.NoDirectionException;
import exceptions.player.SamePositionException;
import model.Game;
import model.map.Map;
import model.map.Square;

import java.util.Objects;

public class PlayerPosition {

    private int coordX;
    private int coordY;

    public PlayerPosition(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public int getCoordX() {
        return this.coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return this.coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public void setPosition(PlayerPosition position) {
        this.coordX = position.getCoordX();
        this.coordY = position.getCoordY();
    }

    /**
     * Method that returns the direction in which the position passed is
     *
     * @param endingPos the position you need to know her direction
     * @return the Direction in which the endingPos position is situated
     */
    public Direction getDirection(PlayerPosition endingPos) throws NoDirectionException {
        if (this.equals(endingPos)) throw new SamePositionException();

        PlayerPosition tempPos = new PlayerPosition(0, 0);

        tempPos.setPosition(this);
        for (int i = 0; i < (Map.MAX_ROWS - this.getCoordX()); ++i) {
            tempPos.setCoordX(this.getCoordX() - i);
            if (tempPos.equals(endingPos)) {
                return Direction.NORTH;
            }
        }

        tempPos.setPosition(this);
        for (int i = 0; i < (Map.MAX_COLUMNS - this.getCoordY()); ++i) {
            tempPos.setCoordY(this.getCoordY() + i);
            if (tempPos.equals(endingPos)) {
                return Direction.EAST;
            }
        }

        tempPos.setPosition(this);
        for (int i = 0; i < (Map.MAX_ROWS - this.getCoordX()); ++i) {
            tempPos.setCoordX(this.getCoordX() + i);
            if (tempPos.equals(endingPos)) {
                return Direction.EAST;
            }
        }

        tempPos.setPosition(this);
        for (int i = 0; i < (Map.MAX_COLUMNS - this.getCoordX()); ++i) {
            tempPos.setCoordY(this.getCoordY() - i);
            if (tempPos.equals(endingPos)) {
                return Direction.WEST;
            }
        }

        throw new NoDirectionException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerPosition that = (PlayerPosition) o;
        return coordX == that.coordX &&
                coordY == that.coordY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordX, coordY);
    }
}
