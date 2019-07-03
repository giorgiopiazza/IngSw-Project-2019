package controller;

import enumerations.PossibleGameState;
import model.player.UserPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnManagerTest {
    private TurnManager turnManager;
    private UserPlayer p1;
    private UserPlayer p2;
    private UserPlayer p3;
    private UserPlayer p4;

    @BeforeEach
    void before() {
        p1 = new UserPlayer("gio");
        p2 = new UserPlayer("tose");
        p3 = new UserPlayer("piro");
        p4 = new UserPlayer("pippo");

        turnManager = new TurnManager(List.of(p1, p2, p3, p4));
    }

    @Test
    void copyConstructor() {
        TurnManager newTurnManager = new TurnManager(turnManager);
        assertEquals(newTurnManager.getTurnOwner(), turnManager.getTurnOwner());
    }

    @Test
    void generic() {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        turnManager.setLastPlayer();
        assertEquals(turnOwner, turnManager.getLastPlayer());

        turnOwner = turnManager.getTurnOwner();
        turnManager.setFrenzyActivator();
        assertEquals(turnOwner, turnManager.getFrenzyActivator());

        turnManager.setDamagedPlayers(new ArrayList<>(List.of(p1, p2, p3)));
        assertTrue(turnManager.getDamagedPlayers().containsAll(List.of(p1, p2, p3)));

        turnManager.setDeathPlayers(new ArrayList<>(List.of(p1, p2, p3)));
        assertTrue(turnManager.getDeathPlayers().containsAll(List.of(p1, p2, p3)));

        turnManager.setMarkedByGrenadePlayer(p1);
        assertEquals(p1, turnManager.getMarkedByGrenadePlayer());

        turnManager.setMarkingTerminator(true);
        assertTrue(turnManager.getMarkingTerminator());

        turnManager.setSecondAction(true);
        assertTrue(turnManager.isSecondAction());

        assertTrue(turnManager.isFirstTurn());
        turnManager.endOfFirstTurn();
        assertFalse(turnManager.isFirstTurn());

        turnManager.setArrivingGameState(PossibleGameState.FINAL_FRENZY);
        assertEquals(PossibleGameState.FINAL_FRENZY, turnManager.getArrivingGameState());

        assertEquals(0, turnManager.getTurnCount());
        turnManager.increaseCount();
        assertEquals(1, turnManager.getTurnCount());
        turnManager.resetCount();
        assertEquals(0, turnManager.getTurnCount());

        turnManager.giveTurn(p4);
        assertEquals(p4, turnManager.getTurnOwner());
    }
}
