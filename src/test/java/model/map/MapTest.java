package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import exceptions.map.MaxSquareWeaponsException;
import model.cards.AmmoCard;
import model.cards.WeaponCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MapTest {
    private Map map;
    private Square[][] rooms;
    private SpawnSquare spawnSquare;

    @BeforeEach
    void before() {
        map = new Map();
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
        assertTrue(map.fillMap(rooms));
    }

    @Test
    void spawnSquare() throws MaxSquareWeaponsException {
        WeaponCard mock1, mock2, mock3;

        mock1 = mock(WeaponCard.class);
        mock2 = mock(WeaponCard.class);
        mock3 = mock(WeaponCard.class);

        assertEquals(0, spawnSquare.addWeapon(mock1));
        assertEquals(1, spawnSquare.addWeapon(mock2));
        assertEquals(2, spawnSquare.addWeapon(mock3));

        assertThrows(MaxSquareWeaponsException.class, () -> spawnSquare.addWeapon(mock1));

        assertTrue(spawnSquare.removeWeapon(2));
        assertTrue(spawnSquare.removeWeapon(1));
        assertTrue(spawnSquare.removeWeapon(0));

        assertFalse(spawnSquare.removeWeapon(2));
        assertFalse(spawnSquare.removeWeapon(1));
        assertFalse(spawnSquare.removeWeapon(0));
    }
}