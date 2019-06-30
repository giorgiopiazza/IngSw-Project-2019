package model.player;

import enumerations.Direction;
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
    void defaultMethods() {
        shooter.setCoordX(1);
        shooter.setCoordY(1);

        assertEquals(1, shooter.getCoordX());
        assertEquals(1, shooter.getCoordY());

        target.setPosition(shooter);

        assertEquals(1, target.getCoordX());
        assertEquals(1, target.getCoordY());
    }

    @Test
    void direction() throws NoDirectionException {
        // NORTH
        shooter.setPosition(new PlayerPosition(2,3));
        target.setPosition(new PlayerPosition(0,3));

        assertEquals(Direction.NORTH, shooter.getDirection(target));

        // SOUTH
        shooter.setCoordX(0);
        target.setCoordX(2);

        assertEquals(Direction.SOUTH, shooter.getDirection(target));

        // WEST
        shooter.setPosition(new PlayerPosition(0,3));
        target.setPosition(new PlayerPosition(0,0));

        assertEquals(Direction.WEST, shooter.getDirection(target));

        // EAST
        shooter.setCoordY(0);
        target.setCoordY(3);

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
        shooter.setCoordY(1);
        target.setCoordY(2);

        assertTrue(shooter.canSee(target, gameMap));

        // adjacent rooms if shooter on door is always visible
        shooter.setCoordY(0);

        assertTrue(shooter.canSee(target, gameMap));

        // not visible
        assertFalse(target.canSee(shooter, gameMap));
    }

    @Test
    void dIstanceOf() {
        // same position
        assertEquals(0, shooter.distanceOf(target, gameMap));

        // distance 1
        target.setCoordY(1);
        assertEquals(1, shooter.distanceOf(target, gameMap));

        // max position
        target.setPosition(new PlayerPosition(2,3));
        assertEquals(5, shooter.distanceOf(target, gameMap));

        // reflexivity
        assertEquals(shooter.distanceOf(target, gameMap), target.distanceOf(shooter, gameMap));
    }

}
