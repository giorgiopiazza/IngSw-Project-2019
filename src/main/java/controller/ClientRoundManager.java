package controller;

import enumerations.GameClientState;
import enumerations.PlayerBoardState;
import enumerations.PossibleAction;
import enumerations.UserPlayerState;
import exceptions.player.ClientRoundManagerException;
import model.player.UserPlayer;
import model.player.PlayerBoard;

import java.util.ArrayList;
import java.util.List;

public class ClientRoundManager {

    private UserPlayerState playerState;
    private PlayerBoardState boardState;
    private GameClientState gameClientState;

    private List<PossibleAction> possibleActions;

    private boolean terminatorPresent;
    private boolean terminatorCanMove;
    private boolean roundStarted;
    private boolean reloaded;

    private UserPlayer that;

    public ClientRoundManager(UserPlayer that, boolean terminatorPresent) {
        this.that = that;

        this.roundStarted = false;
        this.reloaded = false;
        this.terminatorPresent = terminatorPresent;
        this.terminatorCanMove = true;

        this.playerState = UserPlayerState.BEGIN;
        this.boardState = PlayerBoardState.NORMAL;
        this.gameClientState = GameClientState.NORMAL;

        this.possibleActions = new ArrayList<>();
        this.possibleActions.add(PossibleAction.CHOOSE_SPAWN);
    }

    /**
     * Change the state of the player in this round
     *
     * @param that the updated current player in game
     */
    public void nextMove(UserPlayer that) {
        nextMove(that, false, false);
    }

    /**
     * Change the state of the player in this round
     *
     * @param that the updated current player in game
     * @param reloaded if the player reload his weapons
     */
    public void nextMove(UserPlayer that, boolean reloaded) {
        nextMove(that, reloaded, false);
    }

    /**
     * Change the state of the player in this round
     *
     * @param that the updated current player in game
     * @param reloaded if the player reload his weapons
     * @param terminatorMove if the next move is a TERMINATOR_MOVE
     */
    public void nextMove(UserPlayer that, boolean reloaded, boolean terminatorMove) {
        if (reloaded && this.reloaded) throw new ClientRoundManagerException("Cannot reload more than 1 time for round");
        if (!terminatorCanMove && terminatorMove && terminatorPresent) throw new ClientRoundManagerException("Cannot move terminator more than 1 time for round");
        if (!roundStarted) throw new ClientRoundManagerException("Error, round not started yet (before call nextMove() you must call beginRound())");
        if (playerState.equals(UserPlayerState.SECOND_MOVE) && !terminatorMove && terminatorPresent && terminatorCanMove) throw new ClientRoundManagerException("the player does not move the terminator");
        if (that ==  null) throw new NullPointerException("UserPlayer \"that\" cannot be null");

        this.reloaded = reloaded;
        this.that = that;
        this.terminatorCanMove = terminatorMove;

        switch (playerState) {
            case BEGIN:
                if (terminatorCanMove && terminatorPresent) {
                    playerState = UserPlayerState.TERMINATOR_FIRST;
                    terminatorCanMove = false;
                } else {
                    playerState = UserPlayerState.FIRST_MOVE;
                }
                break;

            case FIRST_MOVE:
                if (terminatorCanMove && terminatorPresent) {
                    playerState = UserPlayerState.TERMINATOR_SECOND;
                } else {
                    playerState = UserPlayerState.SECOND_MOVE;
                }
                break;

            case SECOND_MOVE:
                if (terminatorMove && terminatorPresent) {
                    playerState = UserPlayerState.TERMINATOR_THIRD;
                } else {
                    playerState = UserPlayerState.END;
                }
                break;

            case TERMINATOR_FIRST:
                playerState = UserPlayerState.FIRST_MOVE;
                break;

            case TERMINATOR_SECOND:
                playerState = UserPlayerState.SECOND_MOVE;
                break;

            case TERMINATOR_THIRD:
                playerState = UserPlayerState.END;
                break;

            default:
                throw new ClientRoundManagerException("Error, ClientRoundManager cannot be in TERMINATOR_MOVE UserPlayerState");
        }
    }

    public void beginRound(UserPlayer that) {
        this.that = that;
        this.boardState = that.getPlayerBoard().getBoardState();
        roundStarted = true;
    }

    /**
     * Set the state to {@code BEGIN}, reset {@code terminatorCanMove} to true
     */
    public void endRound() {
        playerState = UserPlayerState.BEGIN;
        terminatorCanMove = true;
        roundStarted = false;
        reloaded = false;
    }

    public UserPlayerState getPlayerState() {
        return playerState == UserPlayerState.TERMINATOR_FIRST || playerState == UserPlayerState.TERMINATOR_SECOND || playerState == UserPlayerState.TERMINATOR_THIRD  ? UserPlayerState.TERMINATOR_MOVE : playerState;
    }

    /**
     * This method return the possible actions that the player can be in this round.
     * If in the list is present the PossibleAction.RELOAD, this action is not counted and another can be performed.
     * If in the list is present the PossibleAction.TERMINATOR_ACTION, means that the next move can be the terminator one,
     * if the round is in the UserPlayerState.SECOND_MOVE state, then the next move is necessarily the terminator one.
     *
     * @return a list with the possible actions that the player can perform in this round
     */
    public List<PossibleAction> possibleActions() {
        List<PossibleAction> actions = new ArrayList<>();
        boardState = that.getPlayerBoard().getBoardState();

        if (gameClientState.equals(GameClientState.NORMAL)) {
            switch (boardState) {
                case NORMAL:
                    normalActions(actions);
                    break;

                case FIRST_ADRENALINE:
                    firstAdrenalineActions(actions);
                    break;

                case SECOND_ADRENALINE:
                    secondAdrenalineActions(actions);
                    break;
            }
        } else {
            finalFrenzyActions(actions);
        }

        return actions;
    }

    private void setGameClientState(GameClientState gameClientState) {
        this.gameClientState = gameClientState;
    }

    private GameClientState getGameClientState() {
        return gameClientState;
    }

    private void normalActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END) {
            if (terminatorCanMove && terminatorPresent) actions.add(PossibleAction.TERMINATOR_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.MOVE_AND_PICK);
            actions.add(PossibleAction.SHOOT);
        }
        if (!reloaded) actions.add(PossibleAction.RELOAD);
    }

    private void firstAdrenalineActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END) {
            if (terminatorCanMove && terminatorPresent) actions.add(PossibleAction.TERMINATOR_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.ADRENALINE_PICK);
            actions.add(PossibleAction.SHOOT);
        }
        if (!reloaded) actions.add(PossibleAction.RELOAD);
    }

    private void secondAdrenalineActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END) {
            if (terminatorCanMove && terminatorPresent) actions.add(PossibleAction.TERMINATOR_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.ADRENALINE_PICK);
            actions.add(PossibleAction.ADRENALINE_SHOOT);
        }
        if (!reloaded) actions.add(PossibleAction.RELOAD);
    }

    private void finalFrenzyActions(List<PossibleAction> actions) {
        // TODO
    }

    /**
     * Check if the round already started
     *
     * @return true if the round is started, otherwise false
     */
    public boolean isStarted() {
        return roundStarted;
    }

    /**
     * Check if the current player have already reload
     *
     * @return true if already reload, otherwise false
     */
    public boolean alreadyReloaded() {
        return reloaded;
    }

    /**
     * Check if the player have already done the terminator move
     *
     * @return true if already moved, otherwise false
     */
    public boolean terminatorMoved() {
        return !terminatorCanMove;
    }
}
