package model.cards;

import enumerations.Ammo;
import enumerations.PlayerColor;
import exceptions.cards.InvalidPowerupActionException;
import exceptions.command.InvalidCommandException;
import exceptions.game.InvalidMapNumberException;
import exceptions.player.MaxCardsInHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.PowerupBaseEffect;
import model.player.PlayerBoard;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.PowerupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PowerupCardTest {
    private Game game;
    private UserPlayer pl1;
    private UserPlayer pl2;
    private UserPlayer pl3;

    private PowerupRequest powerupRequest;
    private PowerupRequest.PowerupRequestBuilder builder;

    private ArrayList<String> userTargets = new ArrayList<>();
    private ArrayList<PlayerPosition> targetsPos = new ArrayList<>();

    @BeforeEach
    void before() throws InvalidMapNumberException {
        game = Game.getInstance();
        pl1 = new UserPlayer("1", PlayerColor.PURPLE, new PlayerBoard());
        pl2 = new UserPlayer("2", PlayerColor.GREEN, new PlayerBoard());
        pl3 = new UserPlayer("3", PlayerColor.YELLOW, new PlayerBoard());

        game.init();
        game.setGameMap(1);

        game.addPlayer(pl1);
        game.addPlayer(pl2);
        game.addPlayer(pl3);
        game.startGame();
    }

    @Test
    void defaultMethods() {
        PowerupCard p1, p2, p3;
        PowerupBaseEffect effect = mock(PowerupBaseEffect.class);

        p1 = (PowerupCard) game.getPowerupCardsDeck().draw();
        p2 = new PowerupCard(p1);

        do {
            p3 = (PowerupCard) game.getPowerupCardsDeck().draw();
        } while (p3.equals(p1));

        p1.hashCode();
        p1.toString();

        assertEquals(p1, p2);
        assertEquals(p1, p1);
        assertNotEquals(p1, p3);
    }

    @Test
    void newton() throws MaxCardsInHandException, NotEnoughAmmoException, InvalidPowerupActionException {
        PowerupCard newton = drawPowerup("NEWTON");

        pl1.addPowerup(newton);
        ArrayList<Integer> powerup = new ArrayList<>();
        powerup.add(0);

        // inexistent player
        builder = new PowerupRequest.PowerupRequestBuilder("TOPOLINO", null, powerup);

        powerupRequest = builder.build();

        assertThrows(InvalidCommandException.class, () -> newton.use(powerupRequest));

        // invalid newton movement
        pl2.setPosition(new PlayerPosition(0,0));
        targetsPos.add(new PlayerPosition(1,1));
        userTargets.add(pl2.getUsername());
        builder = new PowerupRequest.PowerupRequestBuilder(pl1.getUsername(), null, powerup);
        builder.targetPlayersUsername(userTargets);

        powerupRequest = builder.build();

        assertThrows(InvalidPowerupActionException.class, () -> newton.use(powerupRequest));


        // NEWTON test
        targetsPos.clear();
        targetsPos.add(new PlayerPosition(0,2));
        builder = new PowerupRequest.PowerupRequestBuilder(pl1.getUsername(), null, powerup);
        builder.targetPlayersUsername(userTargets);
        builder = builder.targetPlayersMovePositions(targetsPos);

        powerupRequest = builder.build();
        newton.use(powerupRequest);

        assertEquals(new PlayerPosition(0, 2), pl2.getPosition());
    }

    @Test
    void targetingScope() throws MaxCardsInHandException, NotEnoughAmmoException, InvalidPowerupActionException {
        PowerupCard targetingScope = drawPowerup("TARGETING SCOPE");
        PowerupCard paymentPowerup = drawPowerup("TARGETING SCOPE");

        ArrayList<Ammo> payingColors = new ArrayList<>();

        pl1.addPowerup(targetingScope);
        ArrayList<Integer> powerup = new ArrayList<>();
        powerup.add(0);
        payingColors.add(Ammo.RED);

        // payment without powerup
        pl1.setPosition(new PlayerPosition(0,0));
        pl2.setPosition(new PlayerPosition(0,0));
        userTargets.add(pl2.getUsername());
        builder = new PowerupRequest.PowerupRequestBuilder(pl1.getUsername(), null, powerup);
        builder = builder.targetPlayersUsername(userTargets);
        builder = builder.ammoColor(payingColors);

        powerupRequest = builder.build();
        targetingScope.use(powerupRequest);

        assertEquals(1, pl2.getPlayerBoard().getDamageCount());

        // payment with powerup
        pl1.addPowerup(targetingScope);
        powerup.clear();
        powerup.add(0);
        pl1.addPowerup(paymentPowerup);
        ArrayList<Integer> paymentPowerups = new ArrayList<>();
        powerup.add(1);
        paymentPowerups.add(1);

        builder = new PowerupRequest.PowerupRequestBuilder(pl1.getUsername(), null, powerup);
        builder = builder.targetPlayersUsername(userTargets);
        builder = builder.paymentPowerups(paymentPowerups);

        powerupRequest = builder.build();
        targetingScope.use(powerupRequest);

        assertEquals(2, pl2.getPlayerBoard().getDamageCount());

    }

    private PowerupCard drawPowerup(String name) {
        PowerupCard powerupCard = null;

        for(Card card : game.getPowerupCardsDeck().toList()) {
            PowerupCard powerup = (PowerupCard) card;

            if(((PowerupCard) card).getName().equals(name)) {
                powerupCard = powerup;
                break;
            }
        }

        return powerupCard;
    }


}
