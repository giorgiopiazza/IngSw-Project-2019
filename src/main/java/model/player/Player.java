package model.player;

import enumerations.Color;

public abstract class Player {
    private final String nickname;
    private final Color color;
    private final boolean firstPlayer;
    private final PlayerBoard playerBoard;

    private PlayerPosition position;
    private int points;

    public Player(String nickname, Color color, boolean firstPlayer, PlayerPosition position, PlayerBoard playerBoard) {

        this.nickname = nickname;
        this.color = color;
        this.firstPlayer = firstPlayer;
        this.position = position;
        this.playerBoard = playerBoard;

        points = 0;
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

    public int getPoints() {
        return points;
    }

    public boolean isFirstPlayer() {
        return firstPlayer;
    }

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

    public void addPoints(int pointsGained) {
        points = this.points + pointsGained;
    }
}
