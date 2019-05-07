package model.player;

import enumerations.Color;
import exceptions.game.InvalidMapNumberException;
import exceptions.player.CardAlreadyInHandException;
import exceptions.player.MaxCardsInHandException;
import model.Game;
import model.cards.WeaponCard;
import model.map.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserPlayerTest {
    private UserPlayer[] players;
    private PlayerBoard board = mock(PlayerBoard.class);

    @BeforeEach
    void before() {
        players = new UserPlayer[5];

        for (int i = 0; i < 3; ++i) {
            players[i] = new UserPlayer("player", Color.values()[i], board);
            players[i].setPosition(new PlayerPosition(0, 0));
        }

        players[3] = new UserPlayer("player", Color.values()[3], board);
        players[3].setFirstPlayer();
        players[3].setPosition(new PlayerPosition(0, 0));
    }

    @Test
    void distanceOf() throws InvalidMapNumberException {
        Player p1 = new UserPlayer("p1", Color.YELLOW, new PlayerBoard());
        Player p2 = new UserPlayer("p2", Color.GREEN, new PlayerBoard());

        Game.getInstance().setGameMap(Map.MAP_3);

        p1.setPosition(new PlayerPosition(0, 0));
        p2.setPosition(new PlayerPosition(0, 0));

        assertEquals(0, p1.distanceOf(p2));

        p1.setPosition(new PlayerPosition(1, 1));
        p2.setPosition(new PlayerPosition(0, 1));

        assertEquals(1, p1.distanceOf(p2));

        p1.setPosition(new PlayerPosition(0, 2));
        p2.setPosition(new PlayerPosition(1, 3));

        assertEquals(2, p1.distanceOf(p2));

        p1.setPosition(new PlayerPosition(1, 0));
        p2.setPosition(new PlayerPosition(1, 1));

        assertEquals(3, p1.distanceOf(p2));

        p1.setPosition(new PlayerPosition(2, 0));
        p2.setPosition(new PlayerPosition(1, 3));

        assertEquals(4, p1.distanceOf(p2));

        p1.setPosition(new PlayerPosition(0, 0));
        p2.setPosition(new PlayerPosition(1, 3));

        assertEquals(4, p1.distanceOf(p2));

        p1.setPosition(new PlayerPosition(0, 3));
        p2.setPosition(new PlayerPosition(2, 0));

        assertEquals(5, p1.distanceOf(p2));
    }

    @Test
    void firstPlaying() {
        assertTrue(players[3].isFirstPlayer());
        for (int i = 0; i < 2; ++i) {
            assertFalse(players[i].isFirstPlayer());
        }
        assertEquals("player", players[3].getUsername());
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
    }

    @Test
    void positionChange() {
        players[1].changePosition(1, 1);
        players[2].changePosition(2, 3);
        players[3].changePosition(2, 3);

        assertEquals(0, players[0].getPosition().getCoordX());
        assertEquals(0, players[0].getPosition().getCoordY());
        assertEquals(1, players[1].getPosition().getCoordX());
        assertEquals(1, players[1].getPosition().getCoordY());
        assertEquals(2, players[2].getPosition().getCoordX());
        assertEquals(3, players[2].getPosition().getCoordY());
        assertEquals(2, players[3].getPosition().getCoordX());
        assertEquals(3, players[3].getPosition().getCoordY());

        assertThrows(IndexOutOfBoundsException.class, () -> players[1].changePosition(-1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> players[1].changePosition(5, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> players[1].changePosition(1, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> players[1].changePosition(1, 6));
    }

    @Test
    void addWeapon() throws MaxCardsInHandException {
        WeaponCard railGun = new WeaponCard("Railgun", null, null, 0, null, null, null);
        WeaponCard shotGun = new WeaponCard("Shotgun", null, null, 1, null, null, null);

        players[0].addWeapon(mock(WeaponCard.class));
        players[0].addWeapon(mock(WeaponCard.class));
        players[0].addWeapon(railGun);
        players[0].addWeapon(mock(WeaponCard.class), railGun);

        assertEquals(3, players[0].getWeapons().length);
        players[2].addWeapon(railGun);

        assertThrows(MaxCardsInHandException.class, () -> players[0].addWeapon(mock(WeaponCard.class)));

        assertThrows(NullPointerException.class, () -> players[1].addWeapon(null));
        assertThrows(NullPointerException.class, () -> players[1].addWeapon(mock(WeaponCard.class), null));
        assertThrows(NullPointerException.class, () -> players[1].addWeapon(null, shotGun));
        assertThrows(NullPointerException.class, () -> players[1].addWeapon(null, null));

        assertThrows(CardAlreadyInHandException.class, () -> players[2].addWeapon(railGun));
        assertThrows(CardAlreadyInHandException.class, () -> players[2].addWeapon(railGun, shotGun));

        assertTrue(players[2].hasWeapon(railGun));
    }
}