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
        assertEquals(0, ammoQuantity.getBlueAmmo());
        assertEquals(0, ammoQuantity.getRedAmmo());
        assertEquals(0, ammoQuantity.getYellowAmmo());

        ammoQuantity = new AmmoQuantity(new Ammo[]{Ammo.RED, Ammo.BLUE, Ammo.YELLOW});
        assertEquals(1, ammoQuantity.getBlueAmmo());
        assertEquals(1, ammoQuantity.getRedAmmo());
        assertEquals(1, ammoQuantity.getYellowAmmo());


        ammoQuantity = new AmmoQuantity(new Ammo[]{Ammo.RED, Ammo.RED, Ammo.RED, Ammo.RED, Ammo.BLUE, Ammo.BLUE,
                Ammo.BLUE, Ammo.BLUE, Ammo.YELLOW, Ammo.YELLOW, Ammo.YELLOW, Ammo.YELLOW});
        assertEquals(3, ammoQuantity.getBlueAmmo());
        assertEquals(3, ammoQuantity.getRedAmmo());
        assertEquals(3, ammoQuantity.getYellowAmmo());

    }

    @Test
    void addAmmo() {
        assertEquals(0, ammoQuantity.getBlueAmmo());
        assertEquals(0, ammoQuantity.getRedAmmo());
        assertEquals(0, ammoQuantity.getYellowAmmo());

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(1, ammoQuantity.getBlueAmmo());
        assertEquals(1, ammoQuantity.getRedAmmo());
        assertEquals(1, ammoQuantity.getYellowAmmo());

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(2, ammoQuantity.getBlueAmmo());
        assertEquals(2, ammoQuantity.getRedAmmo());
        assertEquals(2, ammoQuantity.getYellowAmmo());

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(3, ammoQuantity.getBlueAmmo());
        assertEquals(3, ammoQuantity.getRedAmmo());
        assertEquals(3, ammoQuantity.getYellowAmmo());

        ammoQuantity.addBlueAmmo();
        ammoQuantity.addRedAmmo();
        ammoQuantity.addYellowAmmo();
        assertEquals(3, ammoQuantity.getBlueAmmo());
        assertEquals(3, ammoQuantity.getRedAmmo());
        assertEquals(3, ammoQuantity.getYellowAmmo());
    }

    @Test
    void equals() {
        AmmoQuantity ammoQuantity1 = ammoQuantity;

        assertEquals(ammoQuantity, ammoQuantity1);
        assertFalse(ammoQuantity.equals(null));
        assertFalse(ammoQuantity.equals("test"));

        assertEquals(new AmmoQuantity(0, 0, 0), ammoQuantity);
        assertNotEquals(new AmmoQuantity(0, 0, 1), ammoQuantity);
        assertNotEquals(new AmmoQuantity(0, 1, 0), ammoQuantity);
        assertNotEquals(new AmmoQuantity(1, 0, 0), ammoQuantity);
        assertNotEquals(new AmmoQuantity(1, 1, 0), ammoQuantity);
        assertNotEquals(new AmmoQuantity(0, 1, 1), ammoQuantity);
        assertNotEquals(new AmmoQuantity(1, 1, 1), ammoQuantity);
    }

    @Test
    void difference() {
        assertThrows(NullPointerException.class, () -> ammoQuantity.difference(null));
    }

    @Test
    void sum() {
        assertThrows(NullPointerException.class, () -> ammoQuantity.sum(null));

        AmmoQuantity ammoQuantity1 = new AmmoQuantity(1, 1, 1);
        assertEquals(new AmmoQuantity(1, 1, 1), ammoQuantity.sum(ammoQuantity1));

        ammoQuantity1 = new AmmoQuantity(4, 4, 4);
        assertEquals(new AmmoQuantity(3, 3, 3), ammoQuantity.sum(ammoQuantity1));
    }

    @Test
    void hashCodeEquals() {
        assertEquals(new AmmoQuantity(0, 0, 0).hashCode(), ammoQuantity.hashCode());
    }
}
