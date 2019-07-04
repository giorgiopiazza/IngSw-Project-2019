package model.actions;

import enumerations.PlayerColor;
import enumerations.PossibleAction;
import exceptions.actions.InvalidActionException;
import model.Game;
import model.player.PlayerBoard;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {
    @Test
    void moveAction() throws Exception {
        UserPlayer moving = new UserPlayer("1", PlayerColor.GREEN, new PlayerBoard());
        Game.getInstance().setGameMap(3);
        moving.setPosition(new PlayerPosition(0,0));

        MoveAction action = new MoveAction(moving, null, PossibleAction.MOVE);
        assertThrows(InvalidActionException.class, action::validate);

        action = new MoveAction(moving, new PlayerPosition(0,0), PossibleAction.MOVE);
        assertFalse(action.validate());

        action = new MoveAction(moving, new PlayerPosition(2,3), PossibleAction.MOVE);
        assertFalse(action.validate());

        action = new MoveAction(moving, new PlayerPosition(0,1), PossibleAction.MOVE);
        assertTrue(action.validate());

        action = new MoveAction(moving, new PlayerPosition(0,0), PossibleAction.FRENZY_MOVE);
        assertFalse(action.validate());

        action = new MoveAction(moving, new PlayerPosition(2,3), PossibleAction.FRENZY_MOVE);
        assertFalse(action.validate());

        action = new MoveAction(moving, new PlayerPosition(0,1), PossibleAction.FRENZY_MOVE);
        assertTrue(action.validate());

        action = new MoveAction(moving, new PlayerPosition(0,1), PossibleAction.SHOOT);
        assertThrows(NullPointerException.class, action::validate);
    }
}
