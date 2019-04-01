package model.player;

import enumerations.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserPlayerTest {
    private UserPlayer[] players;

    @BeforeEach
    void before() {
        boolean first = false;
        boolean terminator = false;

        players = new UserPlayer[5];

        for (int i = 0; i < 5; ++i) {
            if (i == 4) first = true;
            if (i == 2) terminator = true;
            players[i] = new UserPlayer("player", Color.values()[i], first,
                    new PlayerPosition(0, 0), mock(PlayerBoard.class), terminator);
            terminator = false;
        }
    }

    @Test
    void terminator() {
        assertTrue(players[2].hasTerminator());
        assertFalse(players[0].hasTerminator());

        players[2].setTerminator(false);
        assertFalse(players[2].hasTerminator());
        players[0].setTerminator(true);
        assertTrue(players[0].hasTerminator());
    }

    @Test
    void firstPlaying() {
        assertTrue(players[4].isFirstPlayer());
        for (int i = 0; i < 3; ++i) {
            assertFalse(players[i].isFirstPlayer());
        }
    }

    @Test
    void points() {
        for (int i = 0; i < 5; ++i) {
            players[1].addPoints(i);
            players[3].addPoints(2 * i);
        }
        assertEquals(10, players[1].getPoints());
        assertEquals(20, players[3].getPoints());

        assertEquals(0, players[0].getPoints());
        assertEquals(0, players[2].getPoints());
        assertEquals(0, players[4].getPoints());
    }

    @Test
    void positionChange() {
        players[1].changePosition(1, 1);
        players[2].changePosition(2, 3);
        players[3].changePosition(2, 3);
        players[4].changePosition(1, 2);

        assertEquals(0, players[0].getPosition().getCoordX());
        assertEquals(0, players[0].getPosition().getCoordY());
        assertEquals(1, players[1].getPosition().getCoordX());
        assertEquals(1, players[1].getPosition().getCoordY());
        assertEquals(2, players[2].getPosition().getCoordX());
        assertEquals(3, players[2].getPosition().getCoordY());
        assertEquals(2, players[3].getPosition().getCoordX());
        assertEquals(3, players[3].getPosition().getCoordY());
        assertEquals(1, players[4].getPosition().getCoordX());
        assertEquals(2, players[4].getPosition().getCoordY());
    }

}
