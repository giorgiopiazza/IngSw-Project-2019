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
    }

    @Test
    void getInstance() {
        assertFalse(instance.isStarted());
        assertFalse(instance.isTerminator());

        assertEquals(instance, Game.getInstance());
    }

    @Test
    void addPlayer() throws AdrenalinaException {
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));

        assertNotNull(instance.setTerminator(true));

        instance.addPlayer(mock(Player.class));

        assertThrows(MaxPlayerException.class, () -> instance.addPlayer(mock(Player.class)));

        assertNull(instance.setTerminator(false));
        instance.addPlayer(mock(Player.class));

        assertEquals(5, instance.playersNumber());

        assertThrows(NullPointerException.class, () -> instance.addPlayer(null));
        assertThrows(MaxPlayerException.class, () -> instance.addPlayer(mock(Player.class)));

        instance.startGame();

        assertThrows(GameAlreadyStartedException.class, () -> instance.addPlayer(null));

        instance.stopGame();

        instance.flush();
    }

    @Test
    void startGame() throws AdrenalinaException {
        assertThrows(NotEnoughPlayersException.class, instance::startGame);

        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.startGame();

        assertTrue(instance.isStarted());
        assertThrows(GameAlreadyStartedException.class, instance::startGame);

        instance.stopGame();

        instance.flush();
    }

    @Test
    void stopGame() throws AdrenalinaException {
        assertThrows(GameAlreadyStartedException.class, instance::stopGame);

        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));

        instance.startGame();
        assertTrue(instance.isStarted());

        instance.stopGame();
        assertFalse(instance.isStarted());

        instance.flush();
    }

    @Test
    void various() throws AdrenalinaException {
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));

        assertThrows(MaximumKillshotExceededException.class, () -> instance.setKillShotNum(9));
        instance.setKillShotNum(3);
        assertEquals(3, instance.getKillShotNum());

        instance.startGame();

        assertThrows(GameAlreadyStartedException.class, instance::flush);
        assertThrows(GameAlreadyStartedException.class, instance::clearKillshots);

        instance.addKillShot(mock(KillShot.class));
        assertEquals(2, instance.remainingSkulls());

        instance.addKillShot(mock(KillShot.class));
        instance.addKillShot(mock(KillShot.class));

        assertThrows(NullPointerException.class, () -> instance.addKillShot(null));
        assertThrows(KillShotsTerminatedException.class, () -> instance.addKillShot(mock(KillShot.class)));
        assertThrows(GameAlreadyStartedException.class, () -> instance.setKillShotNum(4));

        instance.stopGame();
        instance.flush();
    }

    @Test
    void terminator() throws AdrenalinaException {
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));

        assertThrows(MaxPlayerException.class, () -> instance.setTerminator(true));
        assertThrows(TerminatorNotPresentException.class, instance::getTerminator);

        instance.startGame();

        assertThrows(GameAlreadyStartedException.class, () -> instance.setTerminator(true));

        instance.stopGame();
        instance.flush();

        Player player = new UserPlayer("tose", Color.YELLOW, true, new PlayerBoard(), false);
        instance.addPlayer(player);
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));

        assertNull(instance.setTerminator(false));

        Player terminator = instance.setTerminator(true);
        assertNotNull(terminator);
        assertEquals(terminator, instance.getTerminator());

        instance.flush();
    }

    @Test
    void spawnPlayer() throws AdrenalinaException {
        Player player = new UserPlayer("tose", Color.YELLOW, true, new PlayerBoard(), false);
        Player notContained = new UserPlayer("gio", Color.YELLOW, true, new PlayerBoard(), false);

        instance.addPlayer(player);
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));

        assertThrows(GameAlreadyStartedException.class, () -> instance.spawnPlayer(player, mock(PlayerPosition.class)));

        instance.startGame();

        assertThrows(NullPointerException.class, () -> instance.spawnPlayer(null, mock(PlayerPosition.class)));
        assertThrows(NullPointerException.class, () -> instance.spawnPlayer(player, null));
        assertThrows(UnknownPlayerException.class, () -> instance.spawnPlayer(notContained, mock(PlayerPosition.class)));

        instance.spawnPlayer(player, mock(PlayerPosition.class));

        instance.stopGame();
        instance.flush();
    }
}