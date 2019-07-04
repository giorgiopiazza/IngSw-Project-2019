package model.game;

import enumerations.PlayerColor;
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
    void before() throws InvalidMapNumberException {
        this.instance = Game.getInstance();
        Game.getInstance().init();
        Game.getInstance().setGameMap(1);
    }

    @Test
    void getInstance() {
        assertFalse(instance.isGameStarted());
        assertFalse(instance.isBotPresent());

        assertEquals(instance, Game.getInstance());
    }

    @Test
    void addPlayer() throws AdrenalinaException {
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        // assertNotNull(instance.setBot(true));

        instance.addPlayer(mock(UserPlayer.class));

        // assertThrows(MaxPlayerException.class, () -> instance.addPlayer(mock(UserPlayer.class)));

        // assertNull(instance.setBot(false));
        instance.addPlayer(mock(UserPlayer.class));

        assertEquals(5, instance.playersNumber());

        assertThrows(NullPointerException.class, () -> instance.addPlayer(null));
        assertThrows(MaxPlayerException.class, () -> instance.addPlayer(mock(UserPlayer.class)));

        instance.setKillShotNum(8);
        instance.startGame();

        assertThrows(GameAlreadyStartedException.class, () -> instance.addPlayer(null));

        instance.stopGame();
    }

    @Test
    void startGame() throws AdrenalinaException {
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.setKillShotNum(8);
        instance.startGame();

        assertTrue(instance.isGameStarted());
        assertThrows(GameAlreadyStartedException.class, () -> instance.startGame());

        instance.stopGame();
    }

    @Test
    void stopGame() throws AdrenalinaException {
        assertThrows(GameAlreadyStartedException.class, instance::stopGame);

        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        instance.setKillShotNum(8);
        instance.startGame();
        assertTrue(instance.isGameStarted());

        instance.stopGame();
        assertFalse(instance.isGameStarted());
    }

    @Test
    void various() throws AdrenalinaException {
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertThrows(InvalidKillshotNumberException.class, () -> instance.setKillShotNum(9));
        assertThrows(InvalidMapNumberException.class, () -> instance.setGameMap(6));

        instance.setKillShotNum(5);
        instance.startGame();

        instance.addKillShot(mock(KillShot.class));
        assertEquals(4, instance.remainingSkulls());

        instance.addKillShot(mock(KillShot.class));
        instance.addKillShot(mock(KillShot.class));
        instance.addKillShot(mock(KillShot.class));
        instance.addKillShot(mock(KillShot.class));

        assertThrows(NullPointerException.class, () -> instance.addKillShot(null));
        assertThrows(KillShotsTerminatedException.class, () -> instance.addKillShot(mock(KillShot.class)));

        instance.stopGame();
    }

    @Test
    void terminator() throws AdrenalinaException {
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertThrows(MaxPlayerException.class, () -> instance.setBot(true));

        instance.setKillShotNum(8);
        instance.startGame();

        assertThrows(GameAlreadyStartedException.class, () -> instance.setBot(true));

        instance.stopGame();
        instance.init();

        UserPlayer player = new UserPlayer("tose", PlayerColor.YELLOW, new PlayerBoard());
        instance.addPlayer(player);
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        // assertNull(instance.setBot(false));

        instance.setBot(true);
        instance.buildTerminator();
        Player terminator = instance.getBot();
        assertNotNull(terminator);
        assertEquals(terminator, instance.getBot());
    }

    @Test
    void spawnPlayer() throws AdrenalinaException {
        UserPlayer player = new UserPlayer("tose", PlayerColor.YELLOW, new PlayerBoard()  );
        UserPlayer notContained = new UserPlayer("gio", PlayerColor.GREEN, new PlayerBoard());

        instance.addPlayer(player);
        instance.addPlayer(mock(UserPlayer.class));
        instance.addPlayer(mock(UserPlayer.class));

        assertThrows(GameAlreadyStartedException.class, () -> instance.spawnPlayer(player, mock(PlayerPosition.class)));

        instance.setKillShotNum(8);
        instance.startGame();

        assertThrows(NullPointerException.class, () -> instance.spawnPlayer(null, mock(PlayerPosition.class)));
        assertThrows(NullPointerException.class, () -> instance.spawnPlayer(player, null));
        assertThrows(UnknownPlayerException.class, () -> instance.spawnPlayer(notContained, mock(PlayerPosition.class)));

        instance.spawnPlayer(player, mock(PlayerPosition.class));

        instance.stopGame();
    }
}