package model.player;

import enumerations.PlayerColor;

import java.io.Serializable;

/**
 * This class represents a player and the points he earned during the game. It is used to handle the declaration of
 * the winners following the tie rules
 */
public class PlayerPoints implements Serializable {
    private final String userName;
    private final PlayerColor playerColor;
    private final int points;

    private boolean winner;

    /**
     * Builds an object containing the name of the player, his color and the points earned
     *
     * @param userName String containing the name of the player
     * @param playerColor the color of the player
     * @param points the points earned
     */
    public PlayerPoints(String userName, PlayerColor playerColor, int points) {
        this.userName = userName;
        this.playerColor = playerColor;
        this.points = points;

        this.winner = false;
    }

    /**
     * @return the name of the player
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the color of the player
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    /**
     * @return the points earned by the player
     */
    public int getPoints() {
        return points;
    }

    /**
     * Method that sets the corresponding player to be a winner
     */
    public void setWinner() {
        winner = true;
    }

    /**
     * @return {@code true} if the player is a winner, otherwise {@code false}
     */
    public boolean isWinner() {
        return winner;
    }
}
