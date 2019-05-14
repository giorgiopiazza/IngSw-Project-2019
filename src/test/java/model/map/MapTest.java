package model.map;

import enumerations.PlayerColor;
import enumerations.RoomColor;
import exceptions.game.*;
import exceptions.map.MapUnknowException;
import model.Game;
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
        assertEquals(RoomColor.RED, map.getSquare(1, 2).getRoomColor());
        assertEquals(RoomColor.GREY, map.getSquare(2, 1).getRoomColor());

        map = new Map(Map.MAP_2);
        assertEquals(RoomColor.BLUE, map.getSquare(0, 1).getRoomColor());
        assertEquals(RoomColor.YELLOW, map.getSquare(1, 2).getRoomColor());
        assertNull(map.getSquare(2, 0));

        map = new Map(Map.MAP_3);
        assertEquals(RoomColor.RED, map.getSquare(0, 0).getRoomColor());
        assertEquals(RoomColor.PURPLE, map.getSquare(1, 1).getRoomColor());
        assertEquals(RoomColor.YELLOW, map.getSquare(2, 2).getRoomColor());

        map = new Map(Map.MAP_4);
        assertEquals(RoomColor.BLUE, map.getSquare(0, 1).getRoomColor());
        assertEquals(RoomColor.BLUE, map.getSquare(0, 2).getRoomColor());
        assertNull(map.getSquare(new PlayerPosition(0, 3)));

        assertThrows(MapUnknowException.class, () -> { new Map(0); });
        assertThrows(MapUnknowException.class, () -> { new Map(5); });
    }

    @Test
    void playersOnMap() throws MaxPlayerException, GameAlreadyStartedException, NotEnoughPlayersException, GameNotReadyException, InvalidMapNumberException {
        Game instance = Game.getInstance();

        instance.init();
        instance.setGameMap(Map.MAP_2);

        Map map = instance.getGameMap();

        assertEquals(0, map.getPlayersInRoom(RoomColor.RED).size());
        assertEquals(0, map.getPlayersInSquare(new PlayerPosition(0, 0)).size());

        UserPlayer p1 = new UserPlayer("tose", PlayerColor.YELLOW, mock(PlayerBoard.class));
        UserPlayer p2 = new UserPlayer("gio", PlayerColor.GREEN, mock(PlayerBoard.class));
        UserPlayer p3 = new UserPlayer("piro", PlayerColor.BLUE, mock(PlayerBoard.class));

        PlayerPosition myPos = new PlayerPosition(0, 1);

        instance.addPlayer(p1);
        instance.addPlayer(p2);
        instance.addPlayer(p3);

        instance.startGame();

        instance.spawnPlayer(p1, myPos);
        instance.spawnPlayer(p2, new PlayerPosition(2, 2));
        instance.spawnPlayer(p3, new PlayerPosition(2, 3));

        Logger.getGlobal().log(Level.INFO, map.toString());

        assertEquals(p1, map.getPlayersInSquare(myPos).get(0));
        assertEquals(p1, map.getPlayersInRoom(RoomColor.BLUE).get(0));

        instance.stopGame();
        instance.init();

        instance.addPlayer(p1);
        instance.addPlayer(p2);
        instance.addPlayer(p3);
        instance.setTerminator(true);
        instance.buildTerminator();
        instance.setGameMap(Map.MAP_2);
        instance.startGame();

        instance.spawnPlayer(p1, myPos);
        instance.spawnPlayer(p2, new PlayerPosition(2, 2));
        instance.spawnPlayer(p3, new PlayerPosition(2, 3));
        instance.spawnTerminator(new PlayerPosition(1, 0));

        assertEquals(p1, map.getPlayersInRoom(RoomColor.BLUE).get(0));
        assertEquals(p1, map.getPlayersInSquare(myPos).get(0));
        assertEquals(instance.getTerminator(), map.getPlayersInSquare(new PlayerPosition(1, 0)).get(0));
        assertEquals(instance.getTerminator(), map.getPlayersInRoom(RoomColor.RED).get(0));
    }

    @Test
    void squares() {
        Map map = new Map(Map.MAP_3);

        assertEquals(1, map.getRoom(RoomColor.GREEN).size());
        assertEquals( 4, map.getRoom(RoomColor.YELLOW).size());
        assertEquals( 2, map.getRoom(RoomColor.GREY).size());
        assertEquals( 1, map.getRoom(RoomColor.PURPLE).size());
    }
}
