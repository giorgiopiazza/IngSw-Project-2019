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

class GameMapTest {
    @Test
    void mapInstances() {
        GameMap gameMap = new GameMap(GameMap.MAP_1);
        assertNull(gameMap.getSquare(0, 3));
        assertEquals(RoomColor.RED, gameMap.getSquare(1, 2).getRoomColor());
        assertEquals(RoomColor.GREY, gameMap.getSquare(2, 1).getRoomColor());

        gameMap = new GameMap(GameMap.MAP_2);
        assertEquals(RoomColor.BLUE, gameMap.getSquare(0, 1).getRoomColor());
        assertEquals(RoomColor.YELLOW, gameMap.getSquare(1, 2).getRoomColor());
        assertNull(gameMap.getSquare(2, 0));

        gameMap = new GameMap(GameMap.MAP_3);
        assertEquals(RoomColor.RED, gameMap.getSquare(0, 0).getRoomColor());
        assertEquals(RoomColor.PURPLE, gameMap.getSquare(1, 1).getRoomColor());
        assertEquals(RoomColor.YELLOW, gameMap.getSquare(2, 2).getRoomColor());

        gameMap = new GameMap(GameMap.MAP_4);
        assertEquals(RoomColor.BLUE, gameMap.getSquare(0, 1).getRoomColor());
        assertEquals(RoomColor.BLUE, gameMap.getSquare(0, 2).getRoomColor());
        assertNull(gameMap.getSquare(new PlayerPosition(0, 3)));

        assertThrows(MapUnknowException.class, () -> { new GameMap(0); });
        assertThrows(MapUnknowException.class, () -> { new GameMap(5); });
    }

    @Test
    void playersOnMap() throws MaxPlayerException, GameAlreadyStartedException, NotEnoughPlayersException, GameNotReadyException, InvalidMapNumberException {
        Game instance = Game.getInstance();

        instance.init();
        instance.setGameMap(GameMap.MAP_2);

        GameMap gameMap = instance.getGameMap();

        assertEquals(0, gameMap.getPlayersInRoom(RoomColor.RED).size());
        assertEquals(0, gameMap.getPlayersInSquare(new PlayerPosition(0, 0)).size());

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

        Logger.getGlobal().log(Level.INFO, gameMap.toString());

        assertEquals(p1, gameMap.getPlayersInSquare(myPos).get(0));
        assertEquals(p1, gameMap.getPlayersInRoom(RoomColor.BLUE).get(0));

        instance.stopGame();
        instance.init();

        instance.addPlayer(p1);
        instance.addPlayer(p2);
        instance.addPlayer(p3);
        instance.setBot(true);
        instance.buildTerminator();
        instance.setGameMap(GameMap.MAP_2);
        instance.startGame();

        instance.spawnPlayer(p1, myPos);
        instance.spawnPlayer(p2, new PlayerPosition(2, 2));
        instance.spawnPlayer(p3, new PlayerPosition(2, 3));
        instance.spawnTerminator(new PlayerPosition(1, 0));

        assertEquals(p1, gameMap.getPlayersInRoom(RoomColor.BLUE).get(0));
        assertEquals(p1, gameMap.getPlayersInSquare(myPos).get(0));
        assertEquals(instance.getBot(), gameMap.getPlayersInSquare(new PlayerPosition(1, 0)).get(0));
        assertEquals(instance.getBot(), gameMap.getPlayersInRoom(RoomColor.RED).get(0));
    }

    @Test
    void squares() {
        GameMap gameMap = new GameMap(GameMap.MAP_3);

        assertEquals(1, gameMap.getRoom(RoomColor.GREEN).size());
        assertEquals( 4, gameMap.getRoom(RoomColor.YELLOW).size());
        assertEquals( 2, gameMap.getRoom(RoomColor.GREY).size());
        assertEquals( 1, gameMap.getRoom(RoomColor.PURPLE).size());
    }
}
