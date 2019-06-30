package model.player;

import enumerations.Direction;
import enumerations.SquareAdjacency;
import exceptions.player.NoDirectionException;
import exceptions.player.SamePositionException;
import model.Game;
import model.map.GameMap;
import model.map.Square;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerPosition implements Serializable {
    private static final long serialVersionUID = 358503478548284014L;

    private int coordX;
    private int coordY;

    public PlayerPosition(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public PlayerPosition(PlayerPosition another) {
        this.coordX = another.coordX;
        this.coordY = another.coordY;
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

        if(this.getCoordX() != endingPos.getCoordX() && this.getCoordY() != endingPos.getCoordY()) {
            throw new NoDirectionException();
        }

        if(this.getCoordY() == endingPos.getCoordY()) {
            return this.getCoordX() > endingPos.getCoordX() ? Direction.NORTH : Direction.SOUTH;
        } else {
            return this.getCoordY() > endingPos.getCoordY() ? Direction.WEST : Direction.EAST;
        }
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

    @Override
    public String toString() {
        return "(" + coordX + "," + coordY + ")";
    }

    public boolean canSee(PlayerPosition pos, GameMap map) {
        if (pos == null) {
            throw new NullPointerException("Target can't be null");
        }

        Square targetSquare = map.getSquare(pos.getCoordX(), pos.getCoordY());
        Square playerSquare = map.getSquare(getCoordX(), getCoordY());

        if (targetSquare.getRoomColor().equals(playerSquare.getRoomColor())) {
            return true;
        }

        Square tempSquare;

        if (playerSquare.getNorth() == SquareAdjacency.DOOR) {
            tempSquare = map.getSquare(getCoordX() - 1, getCoordY());
            if (tempSquare.getRoomColor() == targetSquare.getRoomColor()) {
                return true;
            }
        }

        if (playerSquare.getEast() == SquareAdjacency.DOOR) {
            tempSquare = map.getSquare(getCoordX(), getCoordY() + 1);
            if (tempSquare.getRoomColor() == targetSquare.getRoomColor()) {
                return true;
            }
        }

        if (playerSquare.getSouth() == SquareAdjacency.DOOR) {
            tempSquare = map.getSquare(getCoordX() + 1, getCoordY());
            if (tempSquare.getRoomColor() == targetSquare.getRoomColor()) {
                return true;
            }
        }

        if (playerSquare.getWest() == SquareAdjacency.DOOR) {
            tempSquare = map.getSquare(getCoordX(), getCoordY() - 1);
            return tempSquare.getRoomColor() == targetSquare.getRoomColor();
        }

        return false;
    }

    public boolean canSee(PlayerPosition pos) {
        return canSee(pos, Game.getInstance().getGameMap());
    }

    /**
     * Method that verifies if a position can see any other target verifying that this target is not
     * the same that is "shooting"
     *
     * @param bot          the {@link Bot Bot} shooting
     * @param actingPlayer the UserPlayer using the bot action
     * @return true if the position can see any other target, otherwise false
     */
    public boolean canSeeSomeone(Bot bot, Player actingPlayer) {
        List<UserPlayer> players = new ArrayList<>(Game.getInstance().getPlayers());

        for (UserPlayer target : players) {
            if (target.getPosition() != null && !target.equals(actingPlayer) && bot.getPosition().canSee(target.getPosition())) {
                return true;
            }
        }
        return false;
    }

    public int distanceOf(PlayerPosition other, GameMap map) {
        // list with possible paths, one for every path
        List<Integer> cases = new ArrayList<>();
        // list with the number of steps of a completed journey, the same for all paths
        List<Integer> stepsList = new ArrayList<>();
        // list with the squares already visited, so that you can't retrace your steps
        List<PlayerPosition> alreadyVisited = new ArrayList<>();
        // Create two new instances because I'm going to work on it
        PlayerPosition p1 = new PlayerPosition(this);
        PlayerPosition p2 = new PlayerPosition(other);

        if (p1.samePosition(p2)) {     // same positions have distance 0
            return 0;
        }

        int steps = 0; // number of steps for this path

        do {
            alreadyVisited.add(new PlayerPosition(p2));

            selectCases(cases, alreadyVisited, p2, map); // need to minify the code complexity

            // increment the counter of the steps performed
            steps++;
            // if not even a step has been taken it is because all the roads for that path have been tried with a negative outcome
            if (cases.isEmpty()) {
                steps = 1000;
                break;
            } else {
                subProcessSwitches(alreadyVisited, stepsList, cases, p1, p2, steps, map);
            }
            cases.clear();
        } while (!p1.equals(p2));

        stepsList.add(steps);
        int minSteps;

        minSteps = 999;

        for (Integer integer : stepsList) {
            if (minSteps > integer) minSteps = integer;
        }

        return minSteps;
    }

    /**
     * This method calculates the minimum distance between {@code this} position and {@code other} position
     *
     * @param other another position
     * @return the minimum distance between two players
     */
    public int distanceOf(PlayerPosition other) {
        return distanceOf(other, Game.getInstance().getGameMap());
    }

    /**
     * This method return true if {@code this} player is in the same position as {@code other} player, otherwise false
     *
     * @param other another player in game
     * @return true if {@code this} player is in the same position as {@code other} player, otherwise false
     */
    boolean samePosition(PlayerPosition other) {
        return other.coordX == this.coordX && other.coordY == this.coordY;
    }

    /**
     * This static method needs to the other paths that are not executed by the distanceOf
     *
     * @param alreadyVisited list of already visited squares
     * @param stepsList      list with the number of steps of all paths
     * @param p1             player 1 position
     * @param p2             player 2 position
     */
    private static void subProcessDistanceOf(List<PlayerPosition> alreadyVisited, List<Integer> stepsList, PlayerPosition p1, PlayerPosition p2, int steps, GameMap map) {
        List<Integer> cases = new ArrayList<>();

        while (!p1.equals(p2)) {
            alreadyVisited.add(new PlayerPosition(p2));
            // increment the counter of the steps performed
            selectCases(cases, alreadyVisited, p2, map);
            steps++;

            if (cases.isEmpty()) {
                stepsList.add(1000);
                return;
            } else {
                subProcessSwitches(alreadyVisited, stepsList, cases, p1, p2, steps, map);
            }
            cases.clear();
        }
        stepsList.add(steps);
    }

    /**
     * Static method created to simplify distanceOf code
     *
     * @param alreadyVisited list of already visited squares
     * @param stepsList      list with the number of steps of all paths
     * @param cases          list containing the cases to be processed by the switch
     * @param p1             player 1 position
     * @param p2             player 2 position
     * @param steps          number of steps already made
     */
    private static void subProcessSwitches(List<PlayerPosition> alreadyVisited, List<Integer> stepsList, List<Integer> cases, PlayerPosition p1, PlayerPosition p2, int steps, GameMap map) {
        // other paths
        for (int i = 1; i < cases.size(); i++) {
            switch (cases.get(i)) {
                case 1:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX() + 1, p2.getCoordY()), steps, map);
                    break;
                case 2:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX(), p2.getCoordY() + 1), steps, map);
                    break;
                case 3:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX() - 1, p2.getCoordY()), steps, map);
                    break;
                default:
                    subProcessDistanceOf(new ArrayList<>(alreadyVisited), stepsList, p1, new PlayerPosition(p2.getCoordX(), p2.getCoordY() - 1), steps, map);
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
            default:
                p2.setCoordY(p2.getCoordY() - 1);
        }
    }

    /**
     * Adds to the list {@code cases} the possible ways to go
     *
     * @param cases list with cases accepted
     * @param pos   the position of player
     */
    private static void selectCases(List<Integer> cases, List<PlayerPosition> alreadyVisited, PlayerPosition pos, GameMap map) {
        Square current = map.getSquare(pos.getCoordX(), pos.getCoordY());

        if ((current.getSouth() == SquareAdjacency.DOOR || current.getSouth() == SquareAdjacency.SQUARE) && !alreadyVisited.contains(new PlayerPosition(pos.getCoordX() + 1, pos.getCoordY()))) {
            cases.add(1);
        }
        if ((current.getEast() == SquareAdjacency.DOOR || current.getEast() == SquareAdjacency.SQUARE) && !alreadyVisited.contains(new PlayerPosition(pos.getCoordX(), pos.getCoordY() + 1))) {
            cases.add(2);
        }
        if ((current.getNorth() == SquareAdjacency.DOOR || current.getNorth() == SquareAdjacency.SQUARE) && !alreadyVisited.contains(new PlayerPosition(pos.getCoordX() - 1, pos.getCoordY()))) {
            cases.add(3);
        }
        if ((current.getWest() == SquareAdjacency.DOOR || current.getWest() == SquareAdjacency.SQUARE) && !alreadyVisited.contains(new PlayerPosition(pos.getCoordX(), pos.getCoordY() - 1))) {
            cases.add(4);
        }
    }
}
