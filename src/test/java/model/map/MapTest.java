package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import exceptions.map.MaxSquareWeaponsException;
import model.cards.WeaponCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MapTest {
/*    private Map map;
    private Square[][] rooms;
    private SpawnSquare spawnSquare;

    @BeforeEach
    void before() {
        this.map = new Map(Map.MAP_1);
        rooms = new Square[Map.MAX_ROWS][Map.MAX_COLUMNS];
        for(int i=0;i<Map.MAX_ROWS;i++) {
            for(int j=0;j<Map.MAX_COLUMNS;j++) {
                if (i % 2 == 0) rooms[i][j] = mock(SpawnSquare.class);
                else rooms[i][j] = mock(CardSquare.class);
            }
        }

        spawnSquare = new SpawnSquare(Color.YELLOW, SquareAdjacency.WALL, SquareAdjacency.WALL, SquareAdjacency.DOOR, SquareAdjacency.WALL);
    }

    @Test
    void fillMap() {
        map = new Map(Map.MAP_1);
        Logger.getGlobal().log(Level.INFO, map.toString());
        map = new Map(Map.MAP_2);
        Logger.getGlobal().log(Level.INFO, map.toString());
        map = new Map(Map.MAP_3);
        Logger.getGlobal().log(Level.INFO, map.toString());
        map = new Map(Map.MAP_4);
        Logger.getGlobal().log(Level.INFO, map.toString());
    }

    @Test
    void spawnSquare() throws MaxSquareWeaponsException {
        assertEquals(0, spawnSquare.addWeapon(mock(WeaponCard.class)));
        assertEquals(1, spawnSquare.addWeapon(mock(WeaponCard.class)));
        assertEquals(2, spawnSquare.addWeapon(mock(WeaponCard.class)));

        assertThrows(MaxSquareWeaponsException.class, () -> spawnSquare.addWeapon(mock(WeaponCard.class)));

        assertTrue(spawnSquare.removeWeapon(2));
        assertTrue(spawnSquare.removeWeapon(1));
        assertTrue(spawnSquare.removeWeapon(0));

        assertFalse(spawnSquare.removeWeapon(2));
        assertFalse(spawnSquare.removeWeapon(1));
        assertFalse(spawnSquare.removeWeapon(0));
    }*/
}