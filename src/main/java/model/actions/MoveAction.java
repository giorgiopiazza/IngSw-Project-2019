package model.actions;

import enumerations.PossibleAction;
import exceptions.actions.InvalidActionException;
import model.map.GameMap;
import model.player.PlayerPosition;
import model.player.UserPlayer;

public class MoveAction implements Action {
    private static final int MAX_NORMAL_MOVE = 3;
    private static final int MAX_FRENZY_MOVE = 4;
    private static final int MIN_MOVE = 1;

    private UserPlayer actingPlayer;
    private PlayerPosition movingPos;
    private PossibleAction actionChosen;

    public MoveAction(UserPlayer actingPlayer, PlayerPosition movingPos, PossibleAction actionChosen) {
        this.actingPlayer = actingPlayer;
        this.movingPos = movingPos;
        this.actionChosen = actionChosen;
    }

    @Override
    public boolean validate() throws InvalidActionException {
        // check that the built position has a valid X coordinate
        if(movingPos.getCoordX() < 0 || movingPos.getCoordX() > GameMap.MAX_ROWS - 1) {
            throw new InvalidActionException();
        }

        // check that the built position has a valid Y coordinate
        if(movingPos.getCoordY() < 0 || movingPos.getCoordY() > GameMap.MAX_COLUMNS - 1) {
            throw new InvalidActionException();
        }

        int movingDistance = actingPlayer.getPosition().distanceOf(movingPos);

        if (actionChosen == PossibleAction.MOVE) {
            return (movingDistance >= MIN_MOVE && movingDistance <= MAX_NORMAL_MOVE);
        } else if (actionChosen == PossibleAction.FRENZY_MOVE) {
            return (movingDistance >= MIN_MOVE && movingDistance <= MAX_FRENZY_MOVE);
        } else {
            throw new NullPointerException("To move a player must always have a moving action!");
        }
    }

    @Override
    public void execute() {
        actingPlayer.changePosition(movingPos.getCoordX(), movingPos.getCoordY());
    }
}
