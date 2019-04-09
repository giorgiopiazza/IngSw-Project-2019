package model.player;

import enumerations.Ammo;

import exceptions.AdrenalinaException;
import exceptions.playerboard.BoardAlreadyFlippedException;
import exceptions.playerboard.BoardFlipDamagedException;
import exceptions.playerboard.BoardMaxAmmoException;
import exceptions.playerboard.NotEnoughAmmoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
    void boardFlip() throws AdrenalinaException {
        assertArrayEquals(new Integer[]{8, 6, 4, 2, 1, 1}, playerBoard.getBoardPoints());

        playerBoard.flipBoard();

        assertTrue(playerBoard.isBoardFlipped());

        assertArrayEquals(new Integer[]{2, 1, 1, 1}, playerBoard.getBoardPoints());
    }

    @Test
    void addAmmo() throws AdrenalinaException {

        playerBoard.addAmmo(Ammo.BLUE);
        assertArrayEquals(new Ammo[]{Ammo.BLUE}, playerBoard.getAmmo());

        playerBoard.addAmmo(Ammo.BLUE);
        assertArrayEquals(new Ammo[]{Ammo.BLUE, Ammo.BLUE}, playerBoard.getAmmo());

        playerBoard.addAmmo(Ammo.YELLOW);
        assertArrayEquals(new Ammo[]{Ammo.BLUE, Ammo.BLUE, Ammo.YELLOW}, playerBoard.getAmmo());

        playerBoard.addAmmo(Ammo.BLUE);
        assertArrayEquals(new Ammo[]{Ammo.BLUE, Ammo.BLUE, Ammo.YELLOW, Ammo.BLUE}, playerBoard.getAmmo());

        assertThrows(BoardMaxAmmoException.class, () -> playerBoard.addAmmo(Ammo.BLUE));
    }

    @Test
    void useAmmo() throws AdrenalinaException {
        playerBoard.addAmmo(Ammo.BLUE);
        playerBoard.addAmmo(Ammo.BLUE);
        playerBoard.addAmmo(Ammo.YELLOW);
        playerBoard.addAmmo(Ammo.BLUE);
        playerBoard.addAmmo(Ammo.RED);

        playerBoard.useAmmo(new ArrayList<>(Arrays.asList(Ammo.BLUE, Ammo.BLUE)));
        playerBoard.useAmmo(new ArrayList<>(Arrays.asList(Ammo.YELLOW, Ammo.RED)));

        assertThrows(NotEnoughAmmoException.class, () -> playerBoard.useAmmo(new ArrayList<>(Arrays.asList(Ammo.BLUE, Ammo.BLUE))));
        assertThrows(NotEnoughAmmoException.class, () -> playerBoard.useAmmo(new ArrayList<>(Arrays.asList(Ammo.YELLOW, Ammo.YELLOW))));
        assertThrows(NotEnoughAmmoException.class, () -> playerBoard.useAmmo(new ArrayList<>(Arrays.asList(Ammo.RED, Ammo.RED))));
    }
}
