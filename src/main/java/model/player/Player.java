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

        List<Integer> cases = new ArrayList<>();
        List<Integer> stepsList = new ArrayList<>();
        List<PlayerPosition> alreadyVisited = new ArrayList<>();

        PlayerPosition p1 = new PlayerPosition(this.position.getCoordX(), this.position.getCoordY());
        PlayerPosition p2 = new PlayerPosition(other.position.getCoordX(), other.position.getCoordY());

        int steps = 0;

        do {
            alreadyVisited.add(p2);

            selectCases(cases, alreadyVisited, p2); // need to beautify the code

            // increment the counter of the steps performed
            steps++;
            // if not even a step has been taken it is because all the roads for that path have been tried with a negative outcome
            if(cases.isEmpty()) {
                steps = 1000;
                break;
            } else {
                subProcessSwitches(alreadyVisited, stepsList, cases, p1, p2, steps);
            }
           cases.clear();
        } while(p1.getCoordX() != p2.getCoordX() || p1.getCoordY() != p2.getCoordY());

        stepsList.add(steps);

        int minSteps;

        minSteps = 999;

        for (Integer integer : stepsList) {
            if (minSteps > integer) minSteps = integer;
        }

        return minSteps;
    }

    /**
     * This method return true if {@code this} player is in the same position as {@code other} player, otherwise false
     *
     * @param other another player in game
     * @return true if {@code this} player is in the same position as {@code other} player, otherwise false
     */
    public boolean samePosition(Player other) {
        return other.position.getCoordX() == this.position.getCoordX() && other.position.getCoordY() == this.position.getCoordY();
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

    public boolean canSee(Player target) {
        if (target == null) {
            throw new NullPointerException("Target can't be null");
        }

        PlayerPosition pos = target.getPosition();

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
            if (tempSquare.getColor() == playerSquare.getColor()) {
                return true;
            }
        }

        return false;
    }

    /**
     * This static method needs to the other paths that are not executed by the distanceOf
     *
     * @param alreadyVisited list of already visited squares
     * @param stepsList list with the number of steps of all paths
     * @param p1 player 1 position
     * @param p2 player 2 position
     */
    private static void subProcessDistanceOf(List<PlayerPosition> alreadyVisited, List<Integer> stepsList, PlayerPosition p1, PlayerPosition p2, int steps) {
        List<Integer> cases = new ArrayList<>();

        while (p1.getCoordX() != p2.getCoordX() || p1.getCoordY() != p2.getCoordY()) {
            alreadyVisited.add(p2);
            // increment the counter of the steps performed
            selectCases(cases, alreadyVisited, p2);
            steps++;

            if(cases.isEmpty()) {
                stepsList.add(1000);
                return;
            } else {
                subProcessSwitches(alreadyVisited, stepsList, cases, p1, p2, steps);
            }
            cases.clear();
        }

        stepsList.add(steps);
    }

    /**
     * Static method created to simplify distanceOf code
     *
     * @param alreadyVisited list of already visited squares
     * @param stepsList list with the number of steps of all paths
     * @param cases list containing the cases to be processed by the switch
     * @param p1 player 1 position
     * @param p2 player 2 position
     * @param steps number of steps already made
     */
    private static void subProcessSwitches(List<PlayerPosition> alreadyVisited, List<Integer> stepsList, List<Integer> cases, PlayerPosition p1, PlayerPosition p2, int steps) {
        // other paths
        for (int i = 1; i < cases.size(); i++) {
            switch (cases.get(i)) {
                case 1:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX() + 1, p2.getCoordY()), steps);
                    break;
                case 2:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX(), p2.getCoordY() + 1), steps);
                    break;
                case 3:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX() - 1, p2.getCoordY()), steps);
                    break;
                case 4:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX(), p2.getCoordY() - 1), steps);
                    break;
            }
        }
        // path that is examined by this process
        switch (cases.get(0)) {
            case 1:
                p2.setCoordX(p2.getCoordX() + 1);
                break;
            case 2:
                p2.setCoordY(p2.getCoordY() + 1);
                break;
            case 3:
                p2.setCoordX(p2.getCoordX() - 1);
                break;
            case 4:
                p2.setCoordY(p2.getCoordY() - 1);
                break;
        }
    }

    /**
     * Adds to the list {@code cases} the possible ways to go
     *
     * @param cases list with cases accepted
     * @param pos the position of player
     */
    private static void selectCases(List<Integer> cases, List<PlayerPosition> alreadyVisited, PlayerPosition pos) {
        Square current = Game.getInstance().getGameMap().getSquare(pos.getCoordX(), pos.getCoordY());

        if ((current.getSouth() == SquareAdjacency.DOOR || current.getSouth() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, pos.getCoordX() + 1, pos.getCoordY())) {
            cases.add(1);
        }
        if ((current.getEast() == SquareAdjacency.DOOR || current.getEast() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, pos.getCoordX(), pos.getCoordY() + 1)) {
            cases.add(2);
        }
        if ((current.getNorth() == SquareAdjacency.DOOR || current.getNorth() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, pos.getCoordX() - 1, pos.getCoordY())) {
            cases.add(3);
        }
        if ((current.getWest() == SquareAdjacency.DOOR || current.getWest() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, pos.getCoordX(), pos.getCoordY() - 1)) {
            cases.add(4);
        }
    }

    /**
     * If the newPos has never been traveled, it is true again, otherwise false
     *
     * @param alreadyVisited list of already visited squares
     * @param newX new x position
     * @param newY new y position
     * @return {@code}true if the new position has never been visited, otherwise {@code}false
     */
    private static boolean validateVisitedPosition(List<PlayerPosition> alreadyVisited, int newX, int newY) {
        PlayerPosition newPos = new PlayerPosition(newX, newY);

        for (PlayerPosition pos : alreadyVisited) {
            if (pos.equals(newPos)) return false;
        }
        // new position never visited
        return true;
    }
}
