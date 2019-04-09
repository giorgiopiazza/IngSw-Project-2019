package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import model.cards.AmmoTile;
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
}
