package model.player;

import enumerations.Color;
import model.cards.Target;

public abstract class Player {
    private final String nickname;
    protected Color color;
    private final PlayerBoard playerBoard;
    private PlayerPosition position;
    private int points;
    private Target target;

    public Player(String nickname, Color color, PlayerBoard playerBoard) {

        this.nickname = nickname;
        this.color = color;
        this.position = null;
        this.playerBoard = playerBoard;

        points = 0;

        target = new Target();
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }

    public PlayerPosition getPosition() {
        return position;
    }

    public void setPosition(PlayerPosition position) {
        this.position = position;
    }

    public int getPoints() {
        return points;
    }

    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    public Target getTarget() {
        return this.target;
    }

    public void setTarget(Target target) {
        this.target = target;
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
}
