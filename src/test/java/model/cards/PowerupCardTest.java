package model.cards;

import enumerations.Ammo;
import enumerations.PlayerColor;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.cards.effects.PowerupBaseEffect;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import network.message.EffectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static enumerations.MessageContent.POWERUP_USAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PowerupCardTest {
    Game game;
    UserPlayer pl1;
    UserPlayer pl2;
    UserPlayer pl3;

    @BeforeEach
    void before() throws InvalidMapNumberException {
        game = Game.getInstance();
        pl1 = new UserPlayer("1", PlayerColor.PURPLE, new PlayerBoard());
        pl2 = new UserPlayer("2", PlayerColor.GREEN, new PlayerBoard());
        pl3 = new UserPlayer("3", PlayerColor.YELLOW, new PlayerBoard());

        game.init();
        game.initializeDecks();
        game.setGameMap(1);

        game.addPlayer(pl1);
        game.addPlayer(pl2);
        game.addPlayer(pl3);
    }

    @Test
    void use() {
        PowerupCard p1, p2, p3;
        PowerupBaseEffect effect = mock(PowerupBaseEffect.class);

        p1 = (PowerupCard) game.getPowerupCardsDeck().draw();
        p2 = new PowerupCard(p1);

        do {
            p3 = (PowerupCard) game.getPowerupCardsDeck().draw();
        } while (p3.equals(p1));

        p1.hashCode();
        p1.toString();

        assertEquals(p1, p2);
        assertEquals(p1, p1);
        assertNotEquals(p1, p3);
    }
}
