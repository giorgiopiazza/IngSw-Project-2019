package model.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KillShotTest {

    @Test
    void killShot() {
        KillShot killShot = new KillShot("gio", 10);
        killShot.hashCode();

        assertEquals(10, killShot.getPoints());
        assertEquals("gio", killShot.getKiller());
        assertEquals(new KillShot("gio", 10), killShot);
        assertEquals(killShot, killShot);
        assertNotEquals(new Object(), killShot);
        assertNotEquals(null, killShot);
        assertNotEquals(new KillShot("dio", 10), killShot);
        assertNotEquals(new KillShot("gio", 11), killShot);
        assertNotEquals(new KillShot("dio", 11), killShot);
    }

}