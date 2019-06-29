package model.cards;

import enumerations.Ammo;
import model.cards.effects.PowerupBaseEffect;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class PowerupCardTest {
    @Test
    void use() {
        PowerupCard p1, p2;
        PowerupBaseEffect effect = mock(PowerupBaseEffect.class);

        p1 = new PowerupCard("nome", "", Ammo.BLUE, effect, 0);
        p2 = new PowerupCard("nome", "", Ammo.BLUE, effect, 0);

        assertEquals(p1, p2);
    }
}
