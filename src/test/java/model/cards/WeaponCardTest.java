package model.cards;

import enumerations.Ammo;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.SemiChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
import model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static enumerations.Ammo.BLUE;
import static enumerations.Ammo.YELLOW;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WeaponCardTest {

    private WeaponCard weaponTest;
    private Ammo[] cost = new Ammo[]{YELLOW, YELLOW, BLUE};
    private Ammo[] halfCost = new Ammo[]{YELLOW, BLUE};
    private Map<Effect, Ammo[]> secondaryEffects = new HashMap<>();
    private WeaponState full;
    private WeaponState empty;

    @BeforeEach
    void before() {
        full = new ChargedWeapon();
        WeaponState half = new SemiChargedWeapon();
        empty = new UnchargedWeapon();
        weaponTest = new WeaponCard("TestWeapon", mock(File.class), mock(Effect.class),
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
    void recharge() throws WeaponAlreadyChargedException {
        assertTrue(weaponTest.rechargeable());

        weaponTest.recharge();

        assertTrue(weaponTest.isCharged());
        assertThrows(WeaponAlreadyChargedException.class, () -> weaponTest.recharge());
    }

    @Test
    void use() throws WeaponNotChargedException {
        weaponTest.setStatus(full);
        weaponTest.use(mock(Effect.class), mock(Target.class), mock(Player.class));

        assertThrows(WeaponNotChargedException.class,
                () -> weaponTest.use(mock(Effect.class), mock(Target.class), mock(Player.class)));
    }
}
