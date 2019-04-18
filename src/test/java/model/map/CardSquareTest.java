package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import model.cards.AmmoTile;
import model.cards.WeaponCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

public class CardSquareTest {

    private CardSquare noTileSquare;
    private CardSquare tileSquare;

    @BeforeEach
    void before() {
        noTileSquare = new CardSquare(Color.RED, SquareAdjacency.WALL, SquareAdjacency.SQUARE,
                                            SquareAdjacency.DOOR, SquareAdjacency.SQUARE);
        tileSquare = new CardSquare(Color.RED, SquareAdjacency.WALL, SquareAdjacency.SQUARE,
                SquareAdjacency.DOOR, SquareAdjacency.SQUARE, mock(AmmoTile.class));
    }

    @Test
    void defaultMethods() {
        assertNull(noTileSquare.getAmmoTile());
        assertNotNull(tileSquare.getAmmoTile());

        AmmoTile ammoTile = mock(AmmoTile.class);

        tileSquare.setAmmoTile(ammoTile);
        assertEquals(ammoTile, tileSquare.getAmmoTile());
    }

    @Test
    void spawnSquare() {
        SpawnSquare sq = new SpawnSquare(Color.GREEN, SquareAdjacency.WALL, SquareAdjacency.WALL, SquareAdjacency.DOOR, SquareAdjacency.SQUARE);
        WeaponCard[] weaponCards = new WeaponCard[SpawnSquare.MAX_WEAPONS];

        sq.setColor(Color.RED);
        sq.setNorth(SquareAdjacency.DOOR);
        sq.setEast(SquareAdjacency.WALL);
        sq.setSouth(SquareAdjacency.WALL);
        sq.setWest(SquareAdjacency.SQUARE);

        assertEquals(Color.RED, sq.getColor());
        assertEquals(SquareAdjacency.DOOR, sq.getNorth());
        assertEquals(SquareAdjacency.WALL, sq.getEast());
        assertEquals(SquareAdjacency.WALL, sq.getSouth());
        assertEquals(SquareAdjacency.SQUARE, sq.getWest());

        weaponCards[0] = mock(WeaponCard.class);
        weaponCards[1] = mock(WeaponCard.class);
        weaponCards[2] = mock(WeaponCard.class);

        sq.addWeapon(weaponCards[0]);
        sq.addWeapon(weaponCards[1]);
        sq.addWeapon(weaponCards[2]);

        assertArrayEquals(weaponCards, sq.getWeapons());
        assertTrue(sq.removeWeapon(weaponCards[2]));
        assertFalse(sq.removeWeapon(weaponCards[2]));
        assertThrows(NullPointerException.class, () -> sq.removeWeapon(null));
    }
}
