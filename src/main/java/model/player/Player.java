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

        int x1 = this.position.getCoordX();
        int x2 = other.position.getCoordX();
        int y1 = this.position.getCoordY();
        int y2 = other.position.getCoordY();

        int steps = 0;

        do {
            alreadyVisited.add(new PlayerPosition(x2, y2));

            selectCases(cases, alreadyVisited, x2, y2); // need to beautify the code

            // increment the counter of the steps performed
            steps++;
            // if not even a step has been taken it is because all the roads for that path have been tried with a negative outcome
            if(cases.isEmpty()) {
                steps = 1000;
                break;
            } else {
                Intero xP1 = new Intero(x1);
                Intero xP2 = new Intero(x2);
                Intero yP1 = new Intero(y1);
                Intero yP2 = new Intero(y2);

                subProcessSwitches(alreadyVisited, stepsList, cases, xP1, yP1, xP2, yP2, steps);

                x1 = xP1.n;
                x2 = xP2.n;
                y1 = yP1.n;
                y2 = yP2.n;
            }
           cases.clear();
        } while(x1 != x2 || y1 != y2);

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
     * @param xp1 x position of player 1
     * @param yp1 y position of player 1
     * @param xp2 x position of player 2
     * @param yp2 y position of player 2
     */
    private static void subProcessDistanceOf(List<PlayerPosition> alreadyVisited, List<Integer> stepsList, int xp1, int yp1, int xp2, int yp2, int steps) {
        List<Integer> cases = new ArrayList<>();

        while (xp1 != xp2 || yp1 != yp2) {
            alreadyVisited.add(new PlayerPosition(xp2, yp2));
            // increment the counter of the steps performed
            selectCases(cases, alreadyVisited, xp2, yp2);
            steps++;

            if(cases.isEmpty()) {
                stepsList.add(1000);
                return;
            } else {
                Intero xP1 = new Intero(xp1);
                Intero xP2 = new Intero(xp2);
                Intero yP1 = new Intero(yp1);
                Intero yP2 = new Intero(yp2);

                subProcessSwitches(alreadyVisited, stepsList, cases, xP1, yP1, xP2, yP2, steps);

                xp1 = xP1.n;
                xp2 = xP2.n;
                yp1 = yP1.n;
                yp2 = yP2.n;
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
     * @param xp1 x position of player 1
     * @param yp1 y position of player 1
     * @param xp2 x position of player 2
     * @param yp2 y position of player 2
     * @param steps number of steps already made
     */
    private static void subProcessSwitches(List<PlayerPosition> alreadyVisited, List<Integer> stepsList, List<Integer> cases, Intero xp1, Intero yp1, Intero xp2, Intero yp2, int steps) {
        // other paths
        for (int i = 1; i < cases.size(); i++) {
            switch (cases.get(i)) {
                case 1:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, xp1.n, yp1.n, xp2.n + 1, yp2.n, steps);
                    break;
                case 2:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, xp1.n, yp1.n, xp2.n, yp2.n + 1, steps);
                    break;
                case 3:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, xp1.n, yp1.n, xp2.n - 1, yp2.n, steps);
                    break;
                case 4:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, xp1.n, yp1.n, xp2.n, yp2.n - 1, steps);
                    break;
            }
        }
        // path that is examined by this process
        switch (cases.get(0)) {
            case 1:
                xp2.n++;
                break;
            case 2:
                yp2.n++;
                break;
            case 3:
                xp2.n--;
                break;
            case 4:
                yp2.n--;
                break;
        }
    }

    /**
     * Adds to the list {@code cases} the possible ways to go
     *
     * @param cases list with cases accepted
     * @param x the x pos of player
     * @param y the y pos of player
     */
    private static void selectCases(List<Integer> cases, List<PlayerPosition> alreadyVisited, int x, int y) {
        Square current = Game.getInstance().getGameMap().getSquare(x, y);

        if ((current.getSouth() == SquareAdjacency.DOOR || current.getSouth() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, x + 1, y)) {
            cases.add(1);
        }
        if ((current.getEast() == SquareAdjacency.DOOR || current.getEast() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, x, y + 1)) {
            cases.add(2);
        }
        if ((current.getNorth() == SquareAdjacency.DOOR || current.getNorth() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, x - 1, y)) {
            cases.add(3);
        }
        if ((current.getWest() == SquareAdjacency.DOOR || current.getWest() == SquareAdjacency.SQUARE) && validateVisitedPosition(alreadyVisited, x, y - 1)) {
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

    /**
     * created class because sonar was acting up
     */
    private static class Intero {
        private int n;
        public Intero(int n) { this.n = n; }
    }
}
