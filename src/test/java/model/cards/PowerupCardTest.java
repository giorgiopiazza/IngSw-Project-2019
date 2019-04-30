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
        File file = new File("file");
        PowerupBaseEffect effect = mock(PowerupBaseEffect.class);

        p1 = new PowerupCard("nome", file, Ammo.BLUE, effect);
        p2 = new PowerupCard("nome", file, Ammo.BLUE, effect);

        assertEquals(p1, p2);
    }
}
