package model.player;

import enumerations.*;
import model.Game;

import java.util.EnumSet;
import java.util.Set;

public abstract class Player {
    private final String nickname;
    private static int uniqueID = 0;
    private final int id;
    protected Color color;
    private EnumSet<PossibleAction> possibleActions;
    private PlayerState playerState;
    private final PlayerBoard playerBoard;
    private PlayerPosition position;
    private int points;
    private boolean winner;

    public Player(String nickname, Color color, PlayerBoard playerBoard) {
        this.nickname = nickname;
        this.id = uniqueID;
        this.color = color;
        this.position = null;
        this.playerBoard = playerBoard;
        this.winner = false;
        this.playerState = new PlayerState(PossibleState.FIRST_SPAWN);

        points = 0;
        ++uniqueID;
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

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public PlayerState getPlayerState() {
        return this.playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        if(playerState == null) {
            throw new NullPointerException("A player must always have a state!");
        }

        this.playerState = playerState;
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

    public Set<PossibleAction> getPossibleActions() {
        return this.possibleActions;
    }

    public void setPossibleActions() {
        PlayerBoardState currentPlayerBoardState = getPlayerBoard().getBoardState();

        switch (currentPlayerBoardState) {
            case NORMAL:
                possibleActions = EnumSet.of(PossibleAction.MOVE, PossibleAction.MOVE_AND_PICK, PossibleAction.SHOOT);
                break;
            case FIRST_ADRENALINE:
                possibleActions = EnumSet.of(PossibleAction.MOVE, PossibleAction.ADRENALINE_PICK, PossibleAction.SHOOT);
                break;
            default:    // second adrenaline
                possibleActions = EnumSet.of(PossibleAction.MOVE, PossibleAction.ADRENALINE_PICK, PossibleAction.ADRENALINE_SHOOT);
        }
    }

    public void setFrenzyPossibleActions(int frenzyActivator) {
        if(Game.getInstance().getBeforeFirstFrenzyPlayers(frenzyActivator).contains(this)) {
            possibleActions = EnumSet.of(PossibleAction.FRENZY_MOVE, PossibleAction.FRENZY_PICK, PossibleAction.FRENZY_SHOOT);
        } else {
            possibleActions = EnumSet.of(PossibleAction.LIGHT_FRENZY_SHOOT, PossibleAction.LIGHT_FRENZY_PICK);
        }
    }
}
