package model.player;

import enumerations.Direction;
import enumerations.PlayerColor;
import exceptions.player.NoDirectionException;
import exceptions.player.SamePositionException;
import model.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerPositionTest {
    private GameMap gameMap = new GameMap(3);
    private PlayerPosition shooter;
    private PlayerPosition target;

    @BeforeEach
    void before() {
        shooter = new PlayerPosition(0,0);
        target = new PlayerPosition(shooter);
    }

    @Test
    void playerPoints() {
        PlayerPoints points = new PlayerPoints("ciao", PlayerColor.GREEN, 2);

        points.getPoints();
        points.getUserName();
        points.getPlayerColor();
        points.setWinner();
        assertTrue(points.isWinner());
    }

    @Test
    void defaultMethods() {
        shooter.setRow(1);
        shooter.setColumn(1);

        assertEquals(1, shooter.getRow());
        assertEquals(1, shooter.getColumn());

        target.setPosition(shooter);

        assertEquals(1, target.getRow());
        assertEquals(1, target.getColumn());
    }

    @Test
    void direction() throws NoDirectionException {
        // NORTH
        shooter.setPosition(new PlayerPosition(2,3));
        target.setPosition(new PlayerPosition(0,3));

        assertEquals(Direction.NORTH, shooter.getDirection(target));

        // SOUTH
        shooter.setRow(0);
        target.setRow(2);

        assertEquals(Direction.SOUTH, shooter.getDirection(target));

        // WEST
        shooter.setPosition(new PlayerPosition(0,3));
        target.setPosition(new PlayerPosition(0,0));

        assertEquals(Direction.WEST, shooter.getDirection(target));

        // EAST
        shooter.setColumn(0);
        target.setColumn(3);

        assertEquals(Direction.EAST, shooter.getDirection(target));

        // exceptions
        shooter.setPosition(new PlayerPosition(0,0));
        target.setPosition(new PlayerPosition(0,0));

        assertThrows(SamePositionException.class, () -> shooter.getDirection(target));

        target.setPosition(new PlayerPosition(1,1));
        assertThrows(NoDirectionException.class, () -> shooter.getDirection(target));
    }

    @Test
    void visibility() {
        // same position is always visible
        assertTrue(shooter.canSee(target, gameMap));

        // same room is always visible
        shooter.setColumn(1);
        target.setColumn(2);

        assertTrue(shooter.canSee(target, gameMap));

        // adjacent rooms if shooter on door is always visible
        shooter.setColumn(0);

        assertTrue(shooter.canSee(target, gameMap));

        // not visible
        assertFalse(target.canSee(shooter, gameMap));
    }

    @Test
    void dIstanceOf() {
        // same position
        assertEquals(0, shooter.distanceOf(target, gameMap));

        // distance 1
        target.setColumn(1);
        assertEquals(1, shooter.distanceOf(target, gameMap));

        // max position
        target.setPosition(new PlayerPosition(2,3));
        assertEquals(5, shooter.distanceOf(target, gameMap));

        // reflexivity
        assertEquals(shooter.distanceOf(target, gameMap), target.distanceOf(shooter, gameMap));
    }

}
