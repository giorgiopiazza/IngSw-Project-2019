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

    public void nextMove(boolean reloaded) {
        nextMove(reloaded, false);
    }

    /**
     * Change the state of the player in this round
     *
     * @param reloaded if the player reload his weapons
     * @param terminatorMove if the next move is a TERMINATOR_MOVE
     */
    public void nextMove(boolean reloaded, boolean terminatorMove) {
        if (reloaded && this.reloaded) throw new ClientRoundManagerException("Cannot reload more than 1 time for round");
        if (!terminatorCanMove && terminatorMove) throw new ClientRoundManagerException("Cannot move terminator more than 1 time for round");
        if (!roundStarted) throw new ClientRoundManagerException("Error, round not started yet (before call nextMove() you must call beginRound())");

        this.reloaded = reloaded;
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
        updateBoardState();
        roundStarted = true;
    }

    public void updateBoardState() {
        PlayerBoard board = that.getPlayerBoard();
        this.boardState = board.getBoardState();
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

    public List<PossibleAction> possibleActions() {
        List<PossibleAction> actions = new ArrayList<>();

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
            // TODO: final frenzy possible actions
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
            if (terminatorCanMove) actions.add(PossibleAction.TERMINATOR_ACTION);
            actions.add(PossibleAction.MOVE_AND_PICK);
            actions.add(PossibleAction.SHOOT);
        }
        if (!reloaded) actions.add(PossibleAction.RELOAD);
    }

    private void firstAdrenalineActions(List<PossibleAction> actions) {
        // TODO
    }

    private void secondAdrenalineActions(List<PossibleAction> actions) {
        // TODO
    }

    public boolean isStarted() {
        return roundStarted;
    }

    public boolean alreadyReloaded() {
        return reloaded;
    }

    public boolean terminatorMoved() {
        return !terminatorCanMove;
    }
}
