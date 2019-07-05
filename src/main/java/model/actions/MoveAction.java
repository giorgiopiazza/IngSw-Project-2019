package model.actions;

import enumerations.PossibleAction;
import exceptions.actions.InvalidActionException;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import utility.InputValidator;

/**
 * Implementation of the Move Action considering all the possible kind of moves
 */
public class MoveAction implements Action {
    private static final int MAX_NORMAL_MOVE = 3;
    private static final int MAX_FRENZY_MOVE = 4;
    private static final int MIN_MOVE = 1;

    private UserPlayer actingPlayer;
    private PlayerPosition movingPos;
    private PossibleAction actionChosen;

    /**
     * Builds a Move Action, used to execute a movement during the game
     *
     * @param actingPlayer the Acting moving player
     * @param movingPos the target moving position
     * @param actionChosen the kind of move that the actingPlayer can do
     */
    public MoveAction(UserPlayer actingPlayer, PlayerPosition movingPos, PossibleAction actionChosen) {
        this.actingPlayer = actingPlayer;
        this.movingPos = movingPos;
        this.actionChosen = actionChosen;
    }

    /**
     * Validation of the move action considering the distances defined by the kind of action
     * that is going to be executed
     *
     * @return true if the Move Action is valid, otherwise false
     * @throws InvalidActionException in case the action is invalid due to input validation
     */
    @Override
    public boolean validate() throws InvalidActionException {
        if(!InputValidator.validatePosition(movingPos)) {
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

    /**
     * Implementation of the execution of a moving action
     */
    @Override
    public void execute() {
        actingPlayer.changePosition(movingPos.getRow(), movingPos.getColumn());
    }
}
