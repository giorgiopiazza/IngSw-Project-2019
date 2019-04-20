package model.player;

import enumerations.Ammo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;

import static org.junit.jupiter.api.Assertions.*;

class AmmoQuantityTest {
    private AmmoQuantity ammoQuantity;

    @BeforeEach
    void before() {
        ammoQuantity = new AmmoQuantity();
    }

    @Test
    void constructors() {
        assertEquals(ammoQuantity.getBlueAmmo(), 0);
        assertEquals(ammoQuantity.getRedAmmo(), 0);
        assertEquals(ammoQuantity.getYellowAmmo(), 0);

        ammoQuantity = new AmmoQuantity(new Ammo[]{Ammo.RED, Ammo.BLUE, Ammo.YELLOW});
        assertEquals(ammoQuantity.getBlueAmmo(), 1);
        assertEquals(ammoQuantity.getRedAmmo(), 1);
        assertEquals(ammoQuantity.getYellowAmmo(), 1);

        ammoQuantity = new AmmoQuantity(new Ammo[]{Ammo.RED, Ammo.RED, Ammo.RED, Ammo.RED, Ammo.BLUE, Ammo.BLUE,
                Ammo.BLUE, Ammo.BLUE, Ammo.YELLOW, Ammo.YELLOW, Ammo.YELLOW, Ammo.YELLOW});
        assertEquals(ammoQuantity.getBlueAmmo(), 3);
        assertEquals(ammoQuantity.getRedAmmo(), 3);
        assertEquals(ammoQuantity.getYellowAmmo(), 3);
    }

    @Test
    void addAmmo() {
        assertEquals(ammoQuantity.getBlueAmmo(), 0);
        assertEquals(ammoQuantity.getRedAmmo(), 0);
        assertEquals(ammoQuantity.getYellowAmmo(), 0);

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(ammoQuantity.getBlueAmmo(), 1);
        assertEquals(ammoQuantity.getRedAmmo(), 1);
        assertEquals(ammoQuantity.getYellowAmmo(), 1);

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(ammoQuantity.getBlueAmmo(), 2);
        assertEquals(ammoQuantity.getRedAmmo(), 2);
        assertEquals(ammoQuantity.getYellowAmmo(), 2);

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(ammoQuantity.getBlueAmmo(), 3);
        assertEquals(ammoQuantity.getRedAmmo(), 3);
        assertEquals(ammoQuantity.getYellowAmmo(), 3);

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(ammoQuantity.getBlueAmmo(), 3);
        assertEquals(ammoQuantity.getRedAmmo(), 3);
        assertEquals(ammoQuantity.getYellowAmmo(), 3);
    }

    @Test
    void equals() {
        AmmoQuantity ammoQuantity1 = ammoQuantity;

        assertEquals(ammoQuantity, ammoQuantity1);
        assertFalse(ammoQuantity.equals(null));
        assertFalse(ammoQuantity.equals("test"));

        assertEquals(ammoQuantity, new AmmoQuantity(0, 0, 0));
        assertNotEquals(ammoQuantity, new AmmoQuantity(0, 0, 1));
        assertNotEquals(ammoQuantity, new AmmoQuantity(0, 1, 0));
        assertNotEquals(ammoQuantity, new AmmoQuantity(1, 0, 0));
        assertNotEquals(ammoQuantity, new AmmoQuantity(1, 1, 0));
        assertNotEquals(ammoQuantity, new AmmoQuantity(0, 1, 1));
        assertNotEquals(ammoQuantity, new AmmoQuantity(1, 1, 1));
    }

    @Test
    void difference() {
        assertThrows(NullPointerException.class, () -> ammoQuantity.difference(null));
    }

    @Test
    void sum() {
        assertThrows(NullPointerException.class, () -> ammoQuantity.sum(null));

        AmmoQuantity ammoQuantity1 = new AmmoQuantity(1, 1, 1);
        assertEquals(ammoQuantity.sum(ammoQuantity1), new AmmoQuantity(1, 1, 1));

        ammoQuantity1 = new AmmoQuantity(4, 4, 4);
        assertEquals(ammoQuantity.sum(ammoQuantity1), new AmmoQuantity(3, 3, 3));
    }

    @Test
    void hashCodeEquals() {
        assertEquals(ammoQuantity.hashCode(), new AmmoQuantity(0, 0, 0).hashCode());
    }
}
