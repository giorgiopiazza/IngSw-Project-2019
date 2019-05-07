package model.map;

import enumerations.Color;
import exceptions.game.GameAlreadyStartedException;
import exceptions.game.GameNotReadyException;
import exceptions.game.MaxPlayerException;
import exceptions.game.NotEnoughPlayersException;
import exceptions.map.MapUnknowException;
import model.Game;
import model.player.Player;
import model.player.PlayerBoard;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MapTest {
    @Test
    void mapInstances() {
        Map map = new Map(Map.MAP_1);
        assertNull(map.getSquare(0, 3));
        assertEquals(Color.RED, map.getSquare(1, 2).getColor());
        assertEquals(Color.GREY, map.getSquare(2, 1).getColor());

        map = new Map(Map.MAP_2);
        assertEquals(Color.BLUE, map.getSquare(0, 1).getColor());
        assertEquals(Color.YELLOW, map.getSquare(1, 2).getColor());
        assertNull(map.getSquare(2, 0));

        map = new Map(Map.MAP_3);
        assertEquals(Color.RED, map.getSquare(0, 0).getColor());
        assertEquals(Color.PURPLE, map.getSquare(1, 1).getColor());
        assertEquals(Color.YELLOW, map.getSquare(2, 2).getColor());

        map = new Map(Map.MAP_4);
        assertEquals(Color.BLUE, map.getSquare(0, 1).getColor());
        assertEquals(Color.BLUE, map.getSquare(0, 2).getColor());
        assertNull(map.getSquare(new PlayerPosition(0, 3)));

        assertThrows(MapUnknowException.class, () -> { new Map(0); });
        assertThrows(MapUnknowException.class, () -> { new Map(5); });
    }

    @Test
    void playersOnMap() throws MaxPlayerException, GameAlreadyStartedException, NotEnoughPlayersException, GameNotReadyException {
        Game instance = Game.getInstance();

        instance.init();
        instance.setGameMap(Map.MAP_2);

        Map map = instance.getGameMap();

        assertEquals(0, map.getPlayersInRoom(Color.RED).size());
        assertEquals(0, map.getPlayersInSquare(new PlayerPosition(0, 0)).size());

        UserPlayer p1 = new UserPlayer("tose", Color.YELLOW, mock(PlayerBoard.class));
        UserPlayer p2 = new UserPlayer("gio", Color.RED, mock(PlayerBoard.class));
        UserPlayer p3 = new UserPlayer("piro", Color.BLUE, mock(PlayerBoard.class));

        PlayerPosition myPos = new PlayerPosition(0, 1);

        instance.addPlayer(p1);
        instance.addPlayer(p2);
        instance.addPlayer(p3);

        instance.startGame(8);

        instance.spawnPlayer(p1, myPos);
        instance.spawnPlayer(p2, new PlayerPosition(2, 2));
        instance.spawnPlayer(p3, new PlayerPosition(2, 3));

        Logger.getGlobal().log(Level.INFO, map.toString());

        assertEquals(p1, map.getPlayersInSquare(myPos).get(0));
        assertEquals(p1, map.getPlayersInRoom(Color.BLUE).get(0));

        instance.stopGame();
        instance.init();

        instance.addPlayer(p1);
        instance.addPlayer(p2);
        instance.addPlayer(p3);
        instance.setTerminator(true);
        instance.setGameMap(Map.MAP_2);
        instance.startGame(8);

        instance.spawnPlayer(p1, myPos);
        instance.spawnPlayer(p2, new PlayerPosition(2, 2));
        instance.spawnPlayer(p3, new PlayerPosition(2, 3));
        instance.spawnTerminator(new PlayerPosition(1, 0));

        assertEquals(p1, map.getPlayersInRoom(Color.BLUE).get(0));
        assertEquals(p1, map.getPlayersInSquare(myPos).get(0));
        assertEquals(instance.getTerminator(), map.getPlayersInSquare(new PlayerPosition(1, 0)).get(0));
        assertEquals(instance.getTerminator(), map.getPlayersInRoom(Color.RED).get(0));
    }

    @Test
    void squares() {
        Map map = new Map(Map.MAP_3);

        assertEquals(1, map.getRoom(Color.GREEN).size());
        assertEquals( 4, map.getRoom(Color.YELLOW).size());
        assertEquals( 2, map.getRoom(Color.GREY).size());
        assertEquals( 1, map.getRoom(Color.PURPLE).size());
    }
}
