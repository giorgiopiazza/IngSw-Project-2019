package model.player;

import enumerations.Ammo;

import exceptions.AdrenalinaException;
import exceptions.playerboard.BoardAlreadyFlippedException;
import exceptions.playerboard.BoardFlipDamagedException;
import exceptions.playerboard.NotEnoughAmmoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PlayerBoardTest {
    private PlayerBoard playerBoard;
    private Player damageDealer;

    @BeforeEach
    void before() {
        playerBoard = new PlayerBoard();
        damageDealer = mock(Player.class);
    }

    @Test
    void addDamage() {
        playerBoard.addDamage(damageDealer, 2);
        assertEquals(2, playerBoard.getDamageCount());

        assertThrows(NullPointerException.class, () -> playerBoard.addDamage(null, 2));

        assertEquals(2, playerBoard.getDamageCount());
        playerBoard.addDamage(damageDealer, -2);
        assertEquals(2, playerBoard.getDamageCount());
    }

    @Test
    void addMark() {
        playerBoard.addMark(damageDealer, 2);
        assertEquals(0, playerBoard.getDamageCount());
        assertEquals(2, playerBoard.getMarkCount());

        assertThrows(NullPointerException.class, () -> playerBoard.addMark(null, 2));

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
    void boardFlip() throws AdrenalinaException {
        assertArrayEquals(new Integer[]{8, 6, 4, 2, 1, 1}, playerBoard.getBoardPoints());

        playerBoard.flipBoard();

        assertTrue(playerBoard.isBoardFlipped());

        assertArrayEquals(new Integer[]{2, 1, 1, 1}, playerBoard.getBoardPoints());
    }

    @Test
    void addAmmo() {
        playerBoard.addAmmo(Ammo.BLUE);
        assertEquals(new AmmoQuantity(1, 2, 1), playerBoard.getAmmo());

        playerBoard.addAmmo(Ammo.BLUE);
        assertEquals(new AmmoQuantity(1, 3, 1), playerBoard.getAmmo());

        assertThrows(NullPointerException.class, () -> playerBoard.addAmmo(null));
        assertEquals(new AmmoQuantity(1, 3, 1), playerBoard.getAmmo());

        playerBoard.addAmmo(Ammo.YELLOW);
        assertEquals(new AmmoQuantity(1, 3, 2), playerBoard.getAmmo());

        playerBoard.addAmmo(Ammo.BLUE);
        assertEquals(new AmmoQuantity(1, 3, 2), playerBoard.getAmmo());
    }

    @Test
    void useAmmo() throws AdrenalinaException {
        playerBoard.addAmmo(Ammo.BLUE);
        playerBoard.addAmmo(Ammo.BLUE);
        playerBoard.addAmmo(Ammo.YELLOW);
        playerBoard.addAmmo(Ammo.RED);

        playerBoard.useAmmo(new AmmoQuantity(1, 2, 1));
        assertEquals(new AmmoQuantity(1, 1, 1), playerBoard.getAmmo());
        playerBoard.useAmmo(new AmmoQuantity(1, 0, 1));
        assertEquals(new AmmoQuantity(0, 1, 0), playerBoard.getAmmo());

        assertThrows(NotEnoughAmmoException.class, () -> playerBoard.useAmmo(new AmmoQuantity(0, 2, 0)));
        assertThrows(NotEnoughAmmoException.class, () -> playerBoard.useAmmo(new AmmoQuantity(0, 0, 2)));
        assertThrows(NotEnoughAmmoException.class, () -> playerBoard.useAmmo(new AmmoQuantity(2, 0, 0)));
    }
}
