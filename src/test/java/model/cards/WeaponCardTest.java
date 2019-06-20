package model.cards;

import enumerations.Ammo;
import enumerations.PlayerColor;
import enumerations.PossibleAction;
import exceptions.AdrenalinaException;
import exceptions.actions.InvalidActionException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import exceptions.command.InvalidCommandException;
import exceptions.game.InvalidMapNumberException;
import exceptions.player.MaxCardsInHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.actions.ShootAction;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.SemiChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
import model.player.*;
import network.message.EffectRequest;
import network.message.ShootRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.WeaponParser;

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

    private Game game;

    private UserPlayer shooter;
    private Player target1;
    private Player target2;
    private Player target3;

    private ShootRequest request;
    private ShootRequest.ShootRequestBuilder builder;
    private ShootAction action;
    private ArrayList<String> userTarget;

    private List<Card> weapons;

    @BeforeEach
    void before() throws InvalidMapNumberException {
        full = new ChargedWeapon();
        WeaponState half = new SemiChargedWeapon();
        empty = new UnchargedWeapon();
        weaponTest = new WeaponCard("TestWeapon", "", mock(Effect.class),
                0, cost, secondaryEffects, half);

        weapons = WeaponParser.parseCards().toList();

        userTarget = new ArrayList<>();

        shooter = new UserPlayer("shooter", PlayerColor.GREEN, new PlayerBoard());
        target1 = new UserPlayer("target1", PlayerColor.BLUE, new PlayerBoard());
        target2 = new UserPlayer("target2", PlayerColor.PURPLE, new PlayerBoard());
        target3 = new UserPlayer("target3", PlayerColor.YELLOW, new PlayerBoard());

        game = Game.getInstance();
        game.setGameMap(1);

        game.addPlayer(shooter);
        game.addPlayer((UserPlayer) target1);
        game.addPlayer((UserPlayer) target2);
        game.addPlayer((UserPlayer) target3);
    }

    @Test
    void status() {
        assertEquals(2, weaponTest.status());
        weaponTest.setStatus(empty);
        assertEquals(1, weaponTest.status());
    }

    @Test
    void lockRifle() throws InvalidActionException, WeaponNotChargedException, WeaponAlreadyChargedException, NotEnoughAmmoException, MaxCardsInHandException {
        WeaponCard lockRifle = getWeaponByName("Lock Rifle");
        lockRifle.setStatus(new ChargedWeapon());

        shooter.addWeapon(lockRifle);
        shooter.setPosition(new PlayerPosition(0,0));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", Ammo.RED, null));

        target1.setPosition(new PlayerPosition(0,0));
        target2.setPosition(new PlayerPosition(2,1));
        target3.setPosition(new PlayerPosition(0,0));

        userTarget.add(target1.getUsername());

        // shooter can see the target
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, lockRifle.getId(), 0, null);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // shooter can't see target
        userTarget.clear();
        userTarget.add(target2.getUsername());

        lockRifle.setStatus(new ChargedWeapon());
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, lockRifle.getId(), 0, null);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertThrows(InvalidActionException.class, action::execute);

        // shooter with second effect
        userTarget.clear();
        userTarget.add(target3.getUsername());

        ArrayList<Integer> powerup = new ArrayList<>();
        powerup.add(0);

        lockRifle.setStatus(new ChargedWeapon());
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, lockRifle.getId(), 1, null);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.paymentPowerups(powerup);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // final assert

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(1, target1.getPlayerBoard().getMarkCount());

        assertEquals(0, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());


        assertEquals(2, target3.getPlayerBoard().getDamageCount());
        assertEquals(2, target3.getPlayerBoard().getMarkCount());
    }

    WeaponCard getWeaponByName(String name) {
        WeaponCard weaponCard = null;

        for (Card card : weapons) {
            WeaponCard weapon = (WeaponCard) card;

            if (weapon.getName().equals(name)) {
                weaponCard = weapon;
                break;
            }
        }

        return weaponCard;
    }
}
