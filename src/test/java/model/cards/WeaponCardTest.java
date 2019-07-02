package model.cards;

import enumerations.Ammo;
import enumerations.PlayerColor;
import enumerations.PossibleAction;
import exceptions.actions.InvalidActionException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
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
import network.message.ShootRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.WeaponParser;

import java.util.ArrayList;
import java.util.List;

import static enumerations.Ammo.*;
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
    private Player target4;

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
        target4 = new UserPlayer("target4", PlayerColor.GREEN, new PlayerBoard());

        game = Game.getInstance();
        game.init();
        game.initializeDecks();
        game.setGameMap(1);

        game.addPlayer(shooter);
        game.addPlayer((UserPlayer) target1);
        game.addPlayer((UserPlayer) target2);
        game.addPlayer((UserPlayer) target3);
        game.addPlayer((UserPlayer) target4);
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
        lockRifle.setStatus(full);

        shooter.addWeapon(lockRifle);
        shooter.setPosition(new PlayerPosition(0,0));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", Ammo.RED, null, 0));

        target1.setPosition(new PlayerPosition(0,0));
        target2.setPosition(new PlayerPosition(2,1));
        target3.setPosition(new PlayerPosition(0,0));

        userTarget.add(target1.getUsername());

        // shooter can see the target
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, lockRifle.getId(), 0);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // shooter can't see target
        userTarget.clear();
        userTarget.add(target2.getUsername());

        lockRifle.setStatus(full);
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertThrows(InvalidActionException.class, action::execute);

        // shooter with second effect
        userTarget.clear();
        userTarget.add(target3.getUsername());
        userTarget.add(target1.getUsername());

        ArrayList<Integer> powerup = new ArrayList<>();
        powerup.add(0);

        lockRifle.setStatus(full);
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, lockRifle.getId(), 1);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.paymentPowerups(powerup);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // final assert

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(2, target1.getPlayerBoard().getMarkCount());

        assertEquals(0, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());

        assertEquals(2, target3.getPlayerBoard().getDamageCount());
        assertEquals(1, target3.getPlayerBoard().getMarkCount());
    }

    @Test
    void machineGun() throws MaxCardsInHandException, InvalidActionException, WeaponNotChargedException, WeaponAlreadyChargedException, NotEnoughAmmoException {
        WeaponCard machineGun = getWeaponByName("Machine Gun");
        machineGun.setStatus(full);

        shooter.addWeapon(machineGun);
        shooter.setPosition(new PlayerPosition(0,0));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_blue.png", BLUE, null, 1));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", YELLOW, null, 2));

        target1.setPosition(new PlayerPosition(0,0));
        target2.setPosition(new PlayerPosition(2,1));
        target3.setPosition(new PlayerPosition(0,0));
        target4.setPosition(new PlayerPosition(0,0));

        userTarget.clear();
        userTarget.add(target1.getUsername());
        userTarget.add(target3.getUsername());

        // shooter can see targets
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // first secondary effect
        ArrayList<Integer> powerups = new ArrayList<>();
        powerups.add(1);

        machineGun.setStatus(full);

        userTarget.clear();
        userTarget.add(target1.getUsername());
        userTarget.add(target3.getUsername());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.paymentPowerups(powerups);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // first & second effect
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", YELLOW, null, 3));

        powerups.clear();
        powerups.add(0);
        powerups.add(1);

        machineGun.setStatus(full);

        userTarget.clear();
        userTarget.add(target1.getUsername());
        userTarget.add(target3.getUsername());
        userTarget.add(target4.getUsername());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 3);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.paymentPowerups(powerups);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // shooter cannot see target
        machineGun.setStatus(full);

        userTarget.clear();
        userTarget.add(target2.getUsername());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 3);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        assertThrows(InvalidActionException.class, action::execute);

        // final asserts

        assertEquals(5, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getDamageCount());
        assertEquals(4, target3.getPlayerBoard().getDamageCount());
        assertEquals(1, target4.getPlayerBoard().getDamageCount());
    }

    @Test
    void flameThrower() throws MaxCardsInHandException, NotEnoughAmmoException, WeaponNotChargedException, WeaponAlreadyChargedException, InvalidActionException {
        WeaponCard flameThrower = getWeaponByName("Flamethrower");
        flameThrower.setStatus(full);

        shooter.addWeapon(flameThrower);
        shooter.setPosition(new PlayerPosition(1,0));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", YELLOW, null, 4));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", YELLOW, null, 5));

        target1.setPosition(new PlayerPosition(1,1));
        target2.setPosition(new PlayerPosition(1,2));
        target3.setPosition(new PlayerPosition(1,1));

        // first effect

        userTarget.add(target1.getUsername());
        userTarget.add(target2.getUsername());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        // second effect

        ArrayList<Integer> indexes = new ArrayList<>();
        ArrayList<PlayerPosition> positions = new ArrayList<>();

        positions.add(new PlayerPosition(1,1));
        positions.add(new PlayerPosition(1,2));

        indexes.add(0);
        indexes.add(1);

        flameThrower.setStatus(full);

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder = builder.targetPositions(positions);
        builder = builder.paymentPowerups(indexes);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(3, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());

        assertEquals(2, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());

        assertEquals(2, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
    }

    @Test
    void thor() throws MaxCardsInHandException, NotEnoughAmmoException, WeaponNotChargedException, WeaponAlreadyChargedException, InvalidActionException {
        WeaponCard thor = getWeaponByName("T.H.O.R.");
        thor.setStatus(full);

        shooter.addWeapon(thor);
        shooter.setPosition(new PlayerPosition(0,0));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", BLUE, null, 6));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", BLUE, null, 7));

        target1.setPosition(new PlayerPosition(0,2));
        target2.setPosition(new PlayerPosition(1,2));
        target3.setPosition(new PlayerPosition(1,3));

        userTarget.add(target1.getUsername());
        userTarget.add(target2.getUsername());
        userTarget.add(target3.getUsername());

        ArrayList<Integer> indexes = new ArrayList<>();
        indexes.add(0);
        indexes.add(1);

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 2);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.paymentPowerups(indexes);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());

        assertEquals(1, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());

        assertEquals(2, target3.getPlayerBoard().getDamageCount());
        assertEquals(0, target3.getPlayerBoard().getMarkCount());
    }

    @Test
    void plasmaGun() throws MaxCardsInHandException, NotEnoughAmmoException, WeaponNotChargedException, WeaponAlreadyChargedException, InvalidActionException {
        WeaponCard plasmaGun = getWeaponByName("Plasma Gun");
        plasmaGun.setStatus(full);

        shooter.addWeapon(plasmaGun);
        shooter.setPosition(new PlayerPosition(0,0));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", BLUE, null, 8));

        target1.setPosition(new PlayerPosition(0,0));
        target2.setPosition(new PlayerPosition(2,1));
        target3.setPosition(new PlayerPosition(0,0));

        userTarget.add(target2.getUsername());

        ArrayList<Integer> indexes = new ArrayList<>();
        indexes.add(0);

        // shooter can see the target
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 3);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.paymentPowerups(indexes);
        builder = builder.moveSenderFirst(true);
        builder = builder.senderMovePosition(new PlayerPosition(1,1));

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(3, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
    }

    @Test
    void cyberBlade() throws MaxCardsInHandException, NotEnoughAmmoException, WeaponNotChargedException, WeaponAlreadyChargedException, InvalidActionException {
        WeaponCard cyberBlade = getWeaponByName("Cyberblade");
        cyberBlade.setStatus(full);

        shooter.addWeapon(cyberBlade);
        shooter.setPosition(new PlayerPosition(0,0));
        shooter.addPowerup(new PowerupCard("TAGBACK GRENADE", "/img/powerups/venom_yellow.png", YELLOW, null, 9));

        target1.setPosition(new PlayerPosition(0,0));

        userTarget.add(target1.getUsername());


        // first effect
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder = builder.targetPlayersUsernames(userTarget);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());


        // second effect, move before
        cyberBlade.setStatus(full);
        shooter.changePosition(0, 1);
        target1.getPlayerBoard().setDamages(new ArrayList<>());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.moveSenderFirst(true);
        builder = builder.senderMovePosition(new PlayerPosition(0,0));

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,0), shooter.getPosition());


        // second effect, move after
        cyberBlade.setStatus(full);
        shooter.changePosition(0, 0);
        target1.getPlayerBoard().setDamages(new ArrayList<>());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.moveSenderFirst(false);
        builder = builder.senderMovePosition(new PlayerPosition(0,1));

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,1), shooter.getPosition());


        // third effect
        cyberBlade.setStatus(full);
        shooter.changePosition(0, 0);
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        target2.setPosition(new PlayerPosition(0,0));
        userTarget.add(target2.getUsername());
        ArrayList<Integer> indexes = new ArrayList<>();
        indexes.add(0);

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 2);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.paymentPowerups(indexes);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(2, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,0), shooter.getPosition());


        // fourth effect, move before
        cyberBlade.setStatus(full);
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        target2.getPlayerBoard().setDamages(new ArrayList<>());
        shooter.changePosition(0, 1);

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 3);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.moveSenderFirst(true);
        builder = builder.senderMovePosition(new PlayerPosition(0,0));

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(2, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,0), shooter.getPosition());


        // fourth effect, move in middle
        cyberBlade.setStatus(full);
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        target2.getPlayerBoard().setDamages(new ArrayList<>());
        shooter.changePosition(0, 1);
        shooter.getPlayerBoard().addAmmo(YELLOW);
        target1.changePosition(0,1);

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 3);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.moveInMiddle(true);
        builder = builder.senderMovePosition(new PlayerPosition(0,0));

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(2, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,0), shooter.getPosition());

        // fourth effect, move after
        cyberBlade.setStatus(full);
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        target2.getPlayerBoard().setDamages(new ArrayList<>());
        shooter.changePosition(0, 0);
        shooter.getPlayerBoard().addAmmo(YELLOW);
        target1.changePosition(0,0);

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 3);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.moveSenderFirst(false);
        builder = builder.senderMovePosition(new PlayerPosition(0,1));

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(2, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,1), shooter.getPosition());
    }

    @Test
    void vortexCannon() throws MaxCardsInHandException, NotEnoughAmmoException, WeaponNotChargedException, WeaponAlreadyChargedException, InvalidActionException {
        WeaponCard vortexCannon = getWeaponByName("Vortex Cannon");
        vortexCannon.setStatus(full);

        ArrayList<PlayerPosition> targetPositions = new ArrayList<>();

        shooter.addWeapon(vortexCannon);


        // first effect same position
        shooter.setPosition(new PlayerPosition(1,0));
        target1.setPosition(new PlayerPosition(0,1));
        userTarget.add(target1.getUsername());
        targetPositions.add(target1.getPosition());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.targetPlayersMovePositions(targetPositions);
        builder = builder.moveTargetsFirst(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,1), target1.getPosition());


        // first effect with one movement
        vortexCannon.setStatus(full);
        target1.setPosition(new PlayerPosition(0,2));
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        targetPositions.clear();
        targetPositions.add(new PlayerPosition(0,1));

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.targetPlayersMovePositions(targetPositions);
        builder = builder.moveTargetsFirst(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,1), target1.getPosition());

        // first effect with invalid movement
        vortexCannon.setStatus(full);
        target1.setPosition(new PlayerPosition(0,2));
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        targetPositions.clear();
        targetPositions.add(new PlayerPosition(0,0));

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.targetPlayersMovePositions(targetPositions);
        builder = builder.moveTargetsFirst(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        assertThrows(Exception.class, () -> action.execute());


        // second effect some targets move some targets don't
        vortexCannon.setStatus(full);
        target1.setPosition(new PlayerPosition(0,0));
        target2.setPosition(new PlayerPosition(0,1));
        target3.setPosition(new PlayerPosition(0,2));
        userTarget.add(target2.getUsername());
        userTarget.add(target3.getUsername());
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        targetPositions.clear();
        targetPositions.add(new PlayerPosition(0,1));
        targetPositions.add(new PlayerPosition(0,1));
        targetPositions.add(new PlayerPosition(0,1));

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.targetPlayersMovePositions(targetPositions);
        builder = builder.moveTargetsFirst(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,1), target1.getPosition());
        assertEquals(1, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,1), target2.getPosition());
        assertEquals(1, target3.getPlayerBoard().getDamageCount());
        assertEquals(0, target3.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(0,1), target3.getPosition());

        // second effect invalid move
        vortexCannon.setStatus(full);
        target1.setPosition(new PlayerPosition(0,0));
        target2.setPosition(new PlayerPosition(0,1));
        target3.setPosition(new PlayerPosition(0,2));
        targetPositions.clear();
        targetPositions.add(new PlayerPosition(0,0));
        targetPositions.add(new PlayerPosition(0,0));
        targetPositions.add(new PlayerPosition(0,0));

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.targetPlayersMovePositions(targetPositions);
        builder = builder.moveTargetsFirst(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        assertThrows(Exception.class, () -> action.execute());

        // second effect different positions
        vortexCannon.setStatus(full);
        target1.getPlayerBoard().addAmmo(RED);
        target1.setPosition(new PlayerPosition(0,0));
        target2.setPosition(new PlayerPosition(0,1));
        target3.setPosition(new PlayerPosition(0,2));
        targetPositions.clear();
        targetPositions.add(new PlayerPosition(0,0));
        targetPositions.add(new PlayerPosition(0,0));
        targetPositions.add(new PlayerPosition(0,1));

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder = builder.targetPlayersUsernames(userTarget);
        builder = builder.targetPlayersMovePositions(targetPositions);
        builder = builder.moveTargetsFirst(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        assertThrows(Exception.class, () -> action.execute());
    }

    @Test
    void shockWave() {
        WeaponCard shockwave = getWeaponByName("Shockwave");

    }

    @Test
    void powerGlove() throws MaxCardsInHandException, NotEnoughAmmoException, WeaponNotChargedException, WeaponAlreadyChargedException, InvalidActionException {
        WeaponCard powerGlove = getWeaponByName("Power Glove");
        powerGlove.setStatus(full);

        shooter.addWeapon(powerGlove);
        shooter.setPosition(new PlayerPosition(1,1));
        target1.setPosition(new PlayerPosition(1,2));
        target2.setPosition(new PlayerPosition(1,3));

        userTarget.add(target1.getUsername());

        // first effect
        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 0);
        builder.targetPlayersUsernames(userTarget);
        builder.senderMovePosition(new PlayerPosition(1,2));
        builder.moveToLastTarget(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(1, target1.getPlayerBoard().getDamageCount());
        assertEquals(2, target1.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(1,2), shooter.getPosition());

        // second effect valid positions
        powerGlove.setStatus(full);
        shooter.setPosition(new PlayerPosition(1,1));
        target1.getPlayerBoard().setDamages(new ArrayList<>());
        target1.getPlayerBoard().setMarks(new ArrayList<>());
        userTarget.add(target2.getUsername());

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder.targetPlayersUsernames(userTarget);
        builder.senderMovePosition(new PlayerPosition(1, 3));
        builder.moveToLastTarget(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        action.execute();

        assertEquals(2, target1.getPlayerBoard().getDamageCount());
        assertEquals(0, target1.getPlayerBoard().getMarkCount());
        assertEquals(2, target2.getPlayerBoard().getDamageCount());
        assertEquals(0, target2.getPlayerBoard().getMarkCount());
        assertEquals(new PlayerPosition(1, 3), shooter.getPosition());

        // second effect invalid positions
        powerGlove.setStatus(full);
        shooter.setPosition(new PlayerPosition(1,1));
        target2.setPosition(new PlayerPosition(1, 2));

        builder = new ShootRequest.ShootRequestBuilder(shooter.getUsername(), null, 0, 1);
        builder.targetPlayersUsernames(userTarget);
        builder.senderMovePosition(new PlayerPosition(1, 2));
        builder.moveToLastTarget(true);

        request = new ShootRequest(builder);
        action = new ShootAction(shooter, PossibleAction.SHOOT, request);

        assertTrue(action.validate());
        assertThrows(Exception.class, () -> action.execute());

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
