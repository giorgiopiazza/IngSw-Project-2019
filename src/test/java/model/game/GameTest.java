package model.game;

import enumerations.Color;
import exceptions.AdrenalinaException;
import exceptions.game.*;
import model.Game;
import model.player.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameTest {
    private Game instance;

    @BeforeEach
    void before() {
        this.instance = Game.getInstance();
        Game.getInstance().init();
        Game.getInstance().setGameMap(1);
    }

    @Test
    void getInstance() {
        assertFalse(instance.isStarted());
        assertFalse(instance.isTerminatorPresent());

        assertEquals(instance, Game.getInstance());
    }

    @Test
    void addPlayer() throws AdrenalinaException {
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertNotNull(instance.setTerminator(true));

        instance.addPlayer(mock(UserPlayer.class));

        assertThrows(MaxPlayerException.class, () -> instance.addPlayer(mock(UserPlayer.class)));

        assertNull(instance.setTerminator(false));
        instance.addPlayer(mock(UserPlayer.class));

        assertEquals(5, instance.playersNumber());

        assertThrows(NullPointerException.class, () -> instance.addPlayer(null));
        assertThrows(MaxPlayerException.class, () -> instance.addPlayer(mock(UserPlayer.class)));

        instance.startGame(8);

        assertThrows(GameAlreadyStartedException.class, () -> instance.addPlayer(null));

        instance.stopGame();

        instance.flush();
    }

    @Test
    void startGame() throws AdrenalinaException {
        assertThrows(NotEnoughPlayersException.class, () -> instance.startGame(8));

        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.startGame(8);

        assertTrue(instance.isStarted());
        assertThrows(GameAlreadyStartedException.class, () -> instance.startGame(8));

        instance.stopGame();

        instance.flush();
    }

    @Test
    void stopGame() throws AdrenalinaException {
        assertThrows(GameAlreadyStartedException.class, instance::stopGame);

        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        instance.startGame(8);
        assertTrue(instance.isStarted());

        instance.stopGame();
        assertFalse(instance.isStarted());

        instance.flush();
    }

    @Test
    void various() throws AdrenalinaException {
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertThrows(InvalidKillshotNumber.class, () -> instance.startGame(9));

        instance.startGame(5);

        assertThrows(GameAlreadyStartedException.class, instance::flush);
        assertThrows(GameAlreadyStartedException.class, instance::clearKillshots);

        instance.addKillShot(mock(KillShot.class));
        assertEquals(4, instance.remainingSkulls());

        instance.addKillShot(mock(KillShot.class));
        instance.addKillShot(mock(KillShot.class));
        instance.addKillShot(mock(KillShot.class));
        instance.addKillShot(mock(KillShot.class));

        assertThrows(NullPointerException.class, () -> instance.addKillShot(null));
        assertThrows(KillShotsTerminatedException.class, () -> instance.addKillShot(mock(KillShot.class)));

        instance.stopGame();
        instance.flush();
    }

    @Test
    void terminator() throws AdrenalinaException {
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertThrows(MaxPlayerException.class, () -> instance.setTerminator(true));

        instance.startGame(8);

        assertThrows(GameAlreadyStartedException.class, () -> instance.setTerminator(true));

        instance.stopGame();
        instance.flush();

        UserPlayer player = new UserPlayer("tose", Color.YELLOW, new PlayerBoard(), false);
        instance.addPlayer(player);
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertNull(instance.setTerminator(false));

        Player terminator = instance.setTerminator(true);
        assertNotNull(terminator);
        assertEquals(terminator, instance.getTerminator());

        instance.flush();
    }

    @Test
    void spawnPlayer() throws AdrenalinaException {
        UserPlayer player = new UserPlayer("tose", Color.YELLOW, new PlayerBoard(), false);
        UserPlayer notContained = new UserPlayer("gio", Color.YELLOW, new PlayerBoard(), false);

        instance.addPlayer(player);
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertThrows(GameAlreadyStartedException.class, () -> instance.spawnPlayer(player, mock(PlayerPosition.class)));

        instance.startGame(8);

        assertThrows(NullPointerException.class, () -> instance.spawnPlayer(null, mock(PlayerPosition.class)));
        assertThrows(NullPointerException.class, () -> instance.spawnPlayer(player, null));
        assertThrows(UnknownPlayerException.class, () -> instance.spawnPlayer(notContained, mock(PlayerPosition.class)));

        instance.spawnPlayer(player, mock(PlayerPosition.class));

        instance.stopGame();
        instance.flush();
    }
}