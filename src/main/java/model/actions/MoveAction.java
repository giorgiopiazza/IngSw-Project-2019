package model.actions;

import enumerations.PossibleAction;
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
    public boolean validate() {
        int movingDistance = actingPlayer.getPosition().distanceOf(movingPos);

        if (actionChosen == PossibleAction.MOVE) {
            if (movingDistance >= MIN_MOVE && movingDistance <= MAX_NORMAL_MOVE) {
                return true;
            } else {
                return false;
            }
        } else if (actionChosen == PossibleAction.FRENZY_MOVE) {
            if (movingDistance >= MIN_MOVE && movingDistance <= MAX_FRENZY_MOVE) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new NullPointerException("To move a player must always have a moving action!");
        }
    }

    @Override
    public void execute() {
        actingPlayer.changePosition(movingPos.getCoordX(), movingPos.getCoordY());
    }
}
