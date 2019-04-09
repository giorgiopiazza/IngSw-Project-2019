package model.cards;

import enumerations.Ammo;
import enumerations.Color;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.SemiChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static enumerations.Ammo.BLUE;
import static enumerations.Ammo.YELLOW;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WeaponCardTest {

    private WeaponCard weaponTest;
    private List<Effect> secondaryEffects = new ArrayList<>();
    private Ammo[] cost = new Ammo[]{YELLOW, YELLOW, BLUE};
    private Ammo[] halfCost = new Ammo[]{YELLOW, BLUE};
    private Color weaponColor = Color.YELLOW;
    private WeaponState full;
    private WeaponState empty;
    private WeaponState half;

    @BeforeEach
    void before() {
        full = new ChargedWeapon();
        half = new SemiChargedWeapon();
        empty = new UnchargedWeapon();
        weaponTest = new WeaponCard("TestWeapon", mock(File.class), weaponColor, mock(Effect.class),
                cost, secondaryEffects, half);
    }

    @Test
    void status() {
        assertEquals(2, weaponTest.status());
        weaponTest.setStatus(empty);
        assertEquals(1, weaponTest.status());
        assertEquals(0, weaponTest.getEffects().size());
    }

    @Test
    void rechargeCost() {
        assertArrayEquals(halfCost, weaponTest.getRechargeCost());
        weaponTest.setStatus(empty);
        assertArrayEquals(cost, weaponTest.getRechargeCost());
        weaponTest.setStatus(full);
        assertEquals(0, weaponTest.getRechargeCost().length);
    }

    @Test
    void recharge() {
        assertTrue(weaponTest.rechargeable());
        try {
            weaponTest.recharge();
        } catch (WeaponAlreadyChargedException e) {
            e.printStackTrace();
        }
        assertTrue(weaponTest.isCharged());
        assertThrows(WeaponAlreadyChargedException.class, () -> weaponTest.recharge());
    }

    @Test
    void use() {
        weaponTest.setStatus(full);
        try {
            weaponTest.use(mock(Effect.class), mock(FiringAction.class), mock(Player.class));
        } catch (WeaponNotChargedException e) {
            e.printStackTrace();
        }
        assertThrows(WeaponNotChargedException.class,
                () -> weaponTest.use(mock(Effect.class), mock(FiringAction.class), mock(Player.class)));
    }
}