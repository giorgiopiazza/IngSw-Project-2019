package model.player;

import enumerations.PlayerColor;
import enumerations.PossibleAction;
import enumerations.PossiblePlayerState;
import exceptions.game.InvalidMapNumberException;
import exceptions.player.CardAlreadyInHandException;
import exceptions.player.EmptyHandException;
import exceptions.player.MaxCardsInHandException;
import model.Game;
import model.cards.Deck;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.PowerupParser;
import utility.WeaponParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserPlayerTest {
    private UserPlayer[] players;
    private PlayerBoard board = mock(PlayerBoard.class);

    @BeforeEach
    void before() {
        players = new UserPlayer[5];

        for (int i = 0; i < 3; ++i) {
            players[i] = new UserPlayer("player", PlayerColor.values()[i], board);
            players[i].setPosition(new PlayerPosition(0, 0));
        }

        players[3] = new UserPlayer("player", PlayerColor.values()[3], board);
        players[3].setFirstPlayer();
        players[3].setPosition(new PlayerPosition(0, 0));
    }

    @Test
    void player() throws InvalidMapNumberException {
        Player player = new UserPlayer("ciao");
        Player gio = new UserPlayer("gio");

        Game.getInstance().setGameMap(GameMap.MAP_3);

        player.setColor(PlayerColor.GREEN);

        player.setPosition(new PlayerPosition(0,0));
        gio.setPosition(new PlayerPosition(1,1));

        assertFalse(player.isDead());
        player.getPlayerBoard().addDamage(gio, 11);
        assertTrue(player.isDead());
        assertEquals(0, gio.compareTo(player));
        player.setPoints(10);
        assertEquals(-10, player.compareTo(gio));
        assertFalse(gio.canSee(player));
    }

    @Test
    void userPlayer() throws MaxCardsInHandException, EmptyHandException {
        UserPlayer p1 = new UserPlayer("1");
        UserPlayer p2 = new UserPlayer("2");

        Deck deck = PowerupParser.parseCards();
        Deck deck1 = WeaponParser.parseCards();

        p1.setPosition(new PlayerPosition(0,0));
        p2.setPossibleActions(null);
        p1.setPossibleActions(new HashSet<>(List.of(PossibleAction.SHOOT, PossibleAction.ADRENALINE_PICK)));
        p1.getPlayerState();
        p1.changePlayerState(PossiblePlayerState.PLAYING);
        p1.setPlayerState(PossiblePlayerState.PLAYING);
        p1.setWeapons(new ArrayList<>(List.of((WeaponCard) deck1.draw())));

        PowerupCard powerupCard = (PowerupCard) deck.draw();
        WeaponCard weaponCard = (WeaponCard) deck1.draw();

        p1.addWeapon(weaponCard);
        p1.setPowerups(new ArrayList<>(List.of((PowerupCard)deck.draw())));
        p1.addPowerup(powerupCard);
        p1.discardPowerup(powerupCard);

        p1.discardPowerupByIndex(0);

        assertThrows(EmptyHandException.class, () -> p1.discardPowerup(powerupCard));
        assertThrows(IllegalArgumentException.class, () -> p1.discardPowerupByIndex(-1));
        assertThrows(IllegalArgumentException.class, () -> p1.discardPowerupByIndex(100));
        assertThrows(EmptyHandException.class, () -> p1.discardPowerupByIndex(0));
    }

    @Test
    void distanceOf() throws InvalidMapNumberException {
        Player p1 = new UserPlayer("p1", PlayerColor.YELLOW, new PlayerBoard());
        Player p2 = new UserPlayer("p2", PlayerColor.GREEN, new PlayerBoard());

        Game.getInstance().setGameMap(GameMap.MAP_3);

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
        assertEquals(p1.distanceOf(p2), p1.distanceOf(p2, Game.getInstance().getGameMap()));
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

        assertEquals(0, players[0].getPosition().getRow());
        assertEquals(0, players[0].getPosition().getColumn());
        assertEquals(1, players[1].getPosition().getRow());
        assertEquals(1, players[1].getPosition().getColumn());
        assertEquals(2, players[2].getPosition().getRow());
        assertEquals(3, players[2].getPosition().getColumn());
        assertEquals(2, players[3].getPosition().getRow());
        assertEquals(3, players[3].getPosition().getColumn());

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