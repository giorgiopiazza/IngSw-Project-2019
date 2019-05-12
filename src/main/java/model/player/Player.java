package model.player;

import enumerations.*;

public abstract class Player {
    private final String username;
    protected Color color;
    private final PlayerBoard playerBoard;
    private PlayerPosition position;
    private int points;
    private boolean winner;

    public Player(String username, Color color, PlayerBoard playerBoard) {
        this.username = username;
        this.color = color;
        this.position = null;
        this.playerBoard = playerBoard;
        this.winner = false;

        points = 0;
    }

    public Player(Player other) {
        this.username = other.username;
        this.color = other.color;
        this.position = new PlayerPosition(other.position);
        this.playerBoard = new PlayerBoard(other.playerBoard);
        this.winner = other.winner;
        this.points = other.points;
    }

    public String getUsername() {
        return this.username;
    }

    public Color getColor() {
        return this.color;
    }

    public PlayerPosition getPosition() {
        return this.position;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /**
     * This method calculates the minimum distance between {@code this} player and {@code other} player
     *
     * @param other another player
     * @return the minimum distance between two players
     */
    public int distanceOf(Player other) {
        if (this.samePosition(other)) return 0;

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

    public boolean canSee(Player other) {
        if (this.samePosition(other)) return true;

        PlayerPosition p1 = new PlayerPosition(this.position);
        PlayerPosition p2 = new PlayerPosition(other.position);

        return p1.canSee(p2);
    }
}
