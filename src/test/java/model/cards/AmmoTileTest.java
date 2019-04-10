package model.cards;

import enumerations.Ammo;
import enumerations.Color;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AmmoTileTest {

    private List<Ammo> onlyTileAmmo;
    private List<Ammo> lessAmmo;

    private AmmoTile onlyAmmoTile;
    private AmmoTile ammoPowerupTile;

    private UserPlayer playerTestA;
    private UserPlayer playerTestB;
    private PlayerBoard boardA;
    private PlayerBoard boardB;

    @BeforeEach
    void before() {
        onlyTileAmmo = new ArrayList<>(Arrays.asList(Ammo.RED, Ammo.RED, Ammo.YELLOW));
        lessAmmo = new ArrayList<>(Arrays.asList(Ammo.RED,Ammo.RED));
        boardA = new PlayerBoard();
        boardB = new PlayerBoard();
        playerTestA = new UserPlayer("playerTestA", Color.YELLOW, true, boardA, false);
        playerTestB = new UserPlayer("playerTestB", Color.RED, false, boardB, false);
        onlyAmmoTile = new AmmoTile(null, onlyTileAmmo, false);
        ammoPowerupTile = new AmmoTile(null, lessAmmo, true);
    }

    @Test
    void defaultMethods() {
        assertFalse(onlyAmmoTile.isPickPowerup());
        assertTrue(ammoPowerupTile.isPickPowerup());

        for(int i = 0; i < onlyTileAmmo.size(); ++i) {
            assertEquals(onlyTileAmmo.get(i), onlyAmmoTile.getAmmoOnTile().get(i));
        }

        for(int i = 0; i < lessAmmo.size(); ++i) {
            assertEquals(lessAmmo.get(i), ammoPowerupTile.getAmmoOnTile().get(i));
        }
    }

    @Test
    void resourceGrant() {
        assertThrows(NullPointerException.class, () -> onlyAmmoTile.giveResources(null));

        /* TODO when implemented the method to istance a poweupDeck as a powerup can be picked
        ammoPowerupTile.giveResources(playerTestA);
        onlyAmmoTile.giveResources(playerTestB);

        assertArrayEquals(lessAmmo.toArray(new Ammo[0]), playerTestA.getPlayerBoard().getAmmo());
        assertEquals(1, playerTestA.getPowerups().length);

        assertEquals(0, playerTestB.getPowerups().length);
        */
    }
}
