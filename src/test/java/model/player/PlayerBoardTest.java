package model.player;

import enumerations.Ammo;

import exceptions.AdrenalinaException;
import exceptions.playerboard.BoardAlreadyFlippedException;
import exceptions.playerboard.BoardFlipDamagedException;
import exceptions.playerboard.BoardMaxAmmoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PlayerBoardTest {
    private PlayerBoard playerBoard;
    private Player damageDealer;
    private Ammo ammo;

    @BeforeEach
    void before() {
        playerBoard = new PlayerBoard();
        damageDealer = mock(Player.class);
    }

    @Test
    void addDamage() {
        playerBoard.addDamage(damageDealer, 2);
        assertEquals(2, playerBoard.getDamageCount());
    }

    @Test
    void addMark() {
        playerBoard.addMark(damageDealer, 2);
        assertEquals(0, playerBoard.getDamageCount());
        assertEquals(2, playerBoard.getMarkCount());

        playerBoard.addDamage(damageDealer, 5);
        assertEquals(7, playerBoard.getDamageCount());
        assertEquals(0, playerBoard.getMarkCount());

        playerBoard.addDamage(damageDealer, 6);
        assertEquals(12, playerBoard.getDamageCount());
        assertEquals(0, playerBoard.getMarkCount());
    }

    @Test
    void pointsAfterDeath() {
        assertArrayEquals(new Integer[]{8, 6, 4, 2, 1, 1}, playerBoard.getBoardPoints());
        assertEquals(0, playerBoard.getSkulls());

        playerBoard.onDeath();

        assertArrayEquals(new Integer[]{6, 4, 2, 1, 1}, playerBoard.getBoardPoints());
        assertEquals(1, playerBoard.getSkulls());
    }

    @Test
    void boardAlreadyFlippedException() {
        assertThrows(BoardAlreadyFlippedException.class, () -> {
            playerBoard.flipBoard();
            playerBoard.flipBoard();
        });
    }

    @Test
    void boardFlipDamagedException() {
        assertThrows(BoardFlipDamagedException.class, () -> {
            playerBoard.addDamage(damageDealer, 2);
            playerBoard.flipBoard();
        });
    }

    @Test
    void boardFlip() {
        assertArrayEquals(new Integer[]{8, 6, 4, 2, 1, 1}, playerBoard.getBoardPoints());

        try {
            playerBoard.flipBoard();
        } catch (AdrenalinaException e) {
            e.printStackTrace();
        }

        assertTrue(playerBoard.isBoardFlipped());

        assertArrayEquals(new Integer[]{2, 1, 1, 1}, playerBoard.getBoardPoints());
    }

    @Test
    void addAmmo() {
        Ammo ammoB = Ammo.BLUE;
        Ammo ammoY = Ammo.YELLOW;

        try {
            playerBoard.addAmmo(ammoB);
            assertArrayEquals(new Ammo[]{Ammo.BLUE}, playerBoard.getAmmo());

            playerBoard.addAmmo(ammoB);
            assertArrayEquals(new Ammo[]{Ammo.BLUE, Ammo.BLUE}, playerBoard.getAmmo());

            playerBoard.addAmmo(ammoY);
            assertArrayEquals(new Ammo[]{Ammo.BLUE, Ammo.BLUE, Ammo.YELLOW}, playerBoard.getAmmo());

            playerBoard.addAmmo(ammoB);
            assertArrayEquals(new Ammo[]{Ammo.BLUE, Ammo.BLUE, Ammo.YELLOW, Ammo.BLUE}, playerBoard.getAmmo());
        } catch (AdrenalinaException e) {
            e.printStackTrace();
        }

        assertThrows(BoardMaxAmmoException.class, () -> playerBoard.addAmmo(ammoB));
    }
}
