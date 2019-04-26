package model.player;

import enumerations.Color;
import enumerations.SquareAdjacency;
import model.Game;
import model.map.Square;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    private final String nickname;
    private static int uniqueID = 0;
    private final int id;
    protected Color color;
    private final PlayerBoard playerBoard;
    private PlayerPosition position;
    private int points;

    public Player(String nickname, Color color, PlayerBoard playerBoard) {
        this.nickname = nickname;
        this.id = uniqueID;
        this.color = color;
        this.position = null;
        this.playerBoard = playerBoard;

        points = 0;
        ++uniqueID;
    }

    // constructor to create a dummy
    public Player(PlayerPosition position) {
        this.nickname = null;
        this.id = -1;
        this.color = null;
        this.playerBoard = null;
        this.position = position;
        this.points = -1;
    }

    public String getNickname() {
        return this.nickname;
    }

    public int getId() {
        return this.id;
    }

    public Color getColor() {
        return this.color;
    }

    public PlayerPosition getPosition() {
        return this.position;
    }

    /**
     * This method calculates the minimum distance between {@code this} player and {@code other} player
     *
     * @param other another player
     * @return the minimum distance between two players
     */
    public int distanceOf(Player other) {
        if(this.samePosition(other)) return 0;

        PlayerPosition p1 = new PlayerPosition(this.position);
        PlayerPosition p2 = new PlayerPosition(other.position);

        return p1.distanceOf(p2);
    }

    /**
     * This method return true if {@code this} player is in the same position as {@code other} player, otherwise false
     *
     * @param other another player in game
     * @return true if {@code this} player is in the same position as {@code other} player, otherwise false
     */
    public boolean samePosition(Player other) {
        return this.position.samePosition(other.position);
    }

    public void setPosition(PlayerPosition position) {
        this.position = position;
    }

    public int getPoints() {
        return this.points;
    }

    public PlayerBoard getPlayerBoard() {
        return this.playerBoard;
    }

    /**
     * Changes the position of a player throwing a runtime exception if the position is not in the map
     *
     * @param newX the new X where to move the player
     * @param newY the new Y where to move the player
     */
    public void changePosition(int newX, int newY) {

        if ((newX < 0) || (newX > 4)) {
            throw new IndexOutOfBoundsException("The X you wanted to change is not in the map");
        }
        if ((newY < 0) || (newY > 5)) {
            throw new IndexOutOfBoundsException("The Y you wanted to change is not in the map");
        }
        this.position.setCoordX(newX);
        this.position.setCoordY(newY);
    }

    /**
     * Adds points to a player
     *
     * @param pointsGained new points to be added
     */
    public void addPoints(int pointsGained) {
        points = this.points + pointsGained;
    }

    public boolean canSee(PlayerPosition pos) {
        if (pos == null) {
            throw new NullPointerException("Target can't be null");
        }

        Square targetSquare = Game.getInstance().getGameMap().getSquare(pos.getCoordX(), pos.getCoordY());
        Square playerSquare = Game.getInstance().getGameMap().getSquare(getPosition().getCoordX(), getPosition().getCoordY());

        if (targetSquare.getColor() == playerSquare.getColor()) {
            return true;
        }

        Square tempSquare;

        if (playerSquare.getNorth() == SquareAdjacency.DOOR) {
            tempSquare = Game.getInstance().getGameMap().getSquare(pos.getCoordX(), pos.getCoordY() - 1);
            if (tempSquare.getColor() == playerSquare.getColor()) {
                return true;
            }
        }

        if (playerSquare.getEast() == SquareAdjacency.DOOR) {
            tempSquare = Game.getInstance().getGameMap().getSquare(pos.getCoordX() + 1, pos.getCoordY());
            if (tempSquare.getColor() == playerSquare.getColor()) {
                return true;
            }
        }

        if (playerSquare.getSouth() == SquareAdjacency.DOOR) {
            tempSquare = Game.getInstance().getGameMap().getSquare(pos.getCoordX(), pos.getCoordY() + 1);
            if (tempSquare.getColor() == playerSquare.getColor()) {
                return true;
            }
        }

        if (playerSquare.getWest() == SquareAdjacency.DOOR) {
            tempSquare = Game.getInstance().getGameMap().getSquare(pos.getCoordX() - 1, pos.getCoordY());
            return tempSquare.getColor() == playerSquare.getColor();
        }

        return false;
    }

}
