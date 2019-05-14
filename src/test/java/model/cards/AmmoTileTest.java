package model.cards;

import enumerations.PlayerColor;
import model.Game;
import model.player.AmmoQuantity;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AmmoTileTest {

    private AmmoQuantity onlyRed;
    private AmmoQuantity onlyBlue;
    private AmmoQuantity onlyYellow;
    private AmmoQuantity defaultAllDifferent;
    private AmmoQuantity redAndBlue;
    private AmmoQuantity redAndYellow;
    private AmmoQuantity blueAndYellow;
    private AmmoQuantity redBlueAndPowerup;

    private AmmoTile onlyAmmoTile;
    private AmmoTile onlyAmmoTile2;
    private AmmoTile ammoPowerupTile;

    private UserPlayer playerTestA;
    private UserPlayer playerTestB;
    private PlayerBoard boardA;
    private PlayerBoard boardB;

    @BeforeEach
    void before() {
        defaultAllDifferent = new AmmoQuantity(1,1,1);
        onlyRed = new AmmoQuantity(3,0,0);
        onlyBlue = new AmmoQuantity(0,3,0);
        onlyYellow = new AmmoQuantity(0,0,3);
        redAndBlue = new AmmoQuantity(1,2,0);
        redAndYellow = new AmmoQuantity(1,0,2);
        blueAndYellow = new AmmoQuantity(0,1,2);
        redBlueAndPowerup = new AmmoQuantity(1,1,0);

        boardA = new PlayerBoard();
        boardB = new PlayerBoard();
        playerTestA = new UserPlayer("playerTestA", PlayerColor.YELLOW, boardA);
        playerTestB = new UserPlayer("playerTestB", PlayerColor.GREEN, boardB);
        onlyAmmoTile = new AmmoTile(null, defaultAllDifferent, false);
        onlyAmmoTile2 = new AmmoTile(null, defaultAllDifferent, false);
        ammoPowerupTile = new AmmoTile(null, redBlueAndPowerup, true);

        Game.getInstance().init();
        Game.getInstance().initializeDecks();
    }

    @Test
    void defaultMethods() {
        assertFalse(onlyAmmoTile.isPickPowerup());
        assertTrue(ammoPowerupTile.isPickPowerup());

        assertEquals(1, onlyAmmoTile.getAmmoOnTile().getRedAmmo());
        assertEquals(1, onlyAmmoTile.getAmmoOnTile().getBlueAmmo());
        assertEquals(1, onlyAmmoTile.getAmmoOnTile().getYellowAmmo());
        assertEquals(1, ammoPowerupTile.getAmmoOnTile().getRedAmmo());
        assertEquals(1, ammoPowerupTile.getAmmoOnTile().getBlueAmmo());
        assertEquals(0, ammoPowerupTile.getAmmoOnTile().getYellowAmmo());
    }

    @Test
    void giveResources() {
        assertThrows(NullPointerException.class, () -> onlyAmmoTile.giveResources(null));

        onlyAmmoTile.giveResources(playerTestA);
        assertEquals(new AmmoQuantity(2,2,2), playerTestA.getPlayerBoard().getAmmo());
        onlyAmmoTile.giveResources(playerTestA);
        assertEquals(new AmmoQuantity(3,3,3), playerTestA.getPlayerBoard().getAmmo());
        onlyAmmoTile.giveResources(playerTestA);
        assertEquals(new AmmoQuantity(3,3,3), playerTestA.getPlayerBoard().getAmmo());

        ammoPowerupTile.giveResources(playerTestB);
        assertEquals(new AmmoQuantity(2,2,1), playerTestB.getPlayerBoard().getAmmo());
        ammoPowerupTile.giveResources(playerTestB);
        assertEquals(new AmmoQuantity(3,3,1), playerTestB.getPlayerBoard().getAmmo());
        ammoPowerupTile.giveResources(playerTestB);
        assertEquals(new AmmoQuantity(3,3,1), playerTestB.getPlayerBoard().getAmmo());

        assertNotEquals(onlyAmmoTile, ammoPowerupTile);
        assertEquals(onlyAmmoTile, onlyAmmoTile);
        assertEquals(onlyAmmoTile, onlyAmmoTile2);
        assertNotEquals(onlyAmmoTile, playerTestA);
    }
}
