package model.cards.effects;

import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.cards.WeaponCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectTest {
    Game game;

    @BeforeEach
    void before() throws InvalidMapNumberException {
        game = Game.getInstance();
        game.init();
        game.initializeDecks();
        game.setGameMap(1);
    }

    @Test
    void effectTest() {
        Effect effect = ((WeaponCard) game.getWeaponsCardsDeck().draw()).getBaseEffect();

        effect.getDescription();
        assertEquals(effect, effect);
    }
}