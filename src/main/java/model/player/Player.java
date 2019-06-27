package model.player;

import enumerations.*;
import model.map.GameMap;

import java.io.Serializable;
import java.util.Objects;

public abstract class Player implements Serializable, Comparable<Player> {
    private static final long serialVersionUID = 230386989158315558L;

    private final String username;
    protected PlayerColor color;
    private final PlayerBoard playerBoard;
    private PlayerPosition position;
    private transient int points;

    public Player(String username) {
        this.username = username;
        this.color = null;
        this.position = null;
        this.playerBoard = new PlayerBoard();

        points = 0;
    }

    public Player(String username, PlayerColor color, PlayerBoard playerBoard) {
        this.username = username;
        this.color = color;
        this.position = null;
        this.playerBoard = playerBoard;

        points = 0;
    }

    public Player(Player other) {
        this.username = other.username;
        this.color = other.color;
        this.position = new PlayerPosition(other.position);
        this.playerBoard = new PlayerBoard(other.playerBoard);
        this.points = other.points;
    }

    public String getUsername() {
        return this.username;
    }

    public PlayerColor getColor() {
        return this.color;
    }

    public PlayerPosition getPosition() {
        return this.position;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
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

    public int clientDistanceOf(Player other, GameMap map) {
        if (this.samePosition(other)) return 0;

        PlayerPosition p1 = new PlayerPosition(this.position);
        PlayerPosition p2 = new PlayerPosition(other.position);

        return p1.clientDistanceOf(p2, map);
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
     * Method that returns true if the {@link Player Player} is dead, otherwise false.
     * A {@link Player Player} is dead if and only if the damages on his {@link PlayerBoard PlayerBoard} are more than 10
     *
     * @return true if the {@link Player Player} is dead, otherwise false
     */
    public boolean isDead() {
        return playerBoard.getDamageCount() > 10;
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

    /**
     * @param savedPoints setting points to reload the game
     */
    public void setPoints(int savedPoints) {
        this.points = savedPoints;
    }

    public boolean canSee(Player other) {
        if (this.samePosition(other)) return true;

        PlayerPosition p1 = new PlayerPosition(this.position);
        PlayerPosition p2 = new PlayerPosition(other.position);

        return p1.canSee(p2);
    }

    @Override
    public int compareTo(Player otherPlayer) {
        int comparePoints = otherPlayer.getPoints();
        return comparePoints - this.points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return username.equals(player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
