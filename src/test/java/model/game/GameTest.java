package model.game;

import exceptions.AdrenalinaException;
import exceptions.game.GameAlredyStartedException;
import exceptions.game.MaxPlayerException;
import exceptions.game.NotEnoughPlayersException;
import model.Game;
import model.player.Player;
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
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));

        assertEquals(5, instance.playersNumber());

        assertThrows(NullPointerException.class, () -> instance.addPlayer(null));
        assertThrows(MaxPlayerException.class, () -> instance.addPlayer(mock(Player.class)));

        instance.startGame();

        assertThrows(GameAlredyStartedException.class, () -> instance.addPlayer(null));
    }

    @Test
    void startGame() throws AdrenalinaException {
        assertThrows(NotEnoughPlayersException.class, instance::startGame);

        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.addPlayer(mock(Player.class));
        instance.startGame();

        assertTrue(instance.isStarted());
        assertThrows(GameAlredyStartedException.class, instance::startGame);
    }

}