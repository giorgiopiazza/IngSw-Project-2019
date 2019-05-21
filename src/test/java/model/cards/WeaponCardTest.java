package model.cards;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import exceptions.cards.WeaponNotChargedException;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.SemiChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
import network.message.EffectRequest;
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
    private Ammo[] cost = new Ammo[]{YELLOW, YELLOW, BLUE};
    private Ammo[] halfCost = new Ammo[]{YELLOW, BLUE};
    private ArrayList<Effect> secondaryEffects = new ArrayList<>();
    private WeaponState full;
    private WeaponState empty;

    @BeforeEach
    void before() {
        full = new ChargedWeapon();
        WeaponState half = new SemiChargedWeapon();
        empty = new UnchargedWeapon();
        weaponTest = new WeaponCard("TestWeapon", mock(File.class), mock(Effect.class),
                0, cost, secondaryEffects, half);
    }

    @Test
    void status() {
        assertEquals(2, weaponTest.status());
        weaponTest.setStatus(empty);
        assertEquals(1, weaponTest.status());
    }
}
