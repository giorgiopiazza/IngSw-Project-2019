package controller;

import enumerations.PlayerBoardState;
import enumerations.PossibleAction;
import enumerations.UserPlayerState;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClientRoundManager {

    private UserPlayerState playerState;
    private PlayerBoardState boardState;

    private List<PossibleAction> possibleActions;

    private boolean terminatorPresent;
    private boolean terminatorCanMove;
    private boolean startedRound;

    public ClientRoundManager(boolean terminatorPresent) {
        this.startedRound = false;
        this.terminatorPresent = terminatorPresent;
        this.terminatorCanMove = true;

        this.playerState = UserPlayerState.BEGIN;
        this.boardState = PlayerBoardState.NORMAL;

        this.possibleActions = new ArrayList<>();
        this.possibleActions.add(PossibleAction.CHOOSE_SPAWN);
    }

    /**
     * Change the state of the player in this round
     *
     * @param terminatorMove if the next move is a TERMINATOR_MOVE
     * @return true if terminator can make a move in the {@code nextMove}, if startRound not called yet and terminator cannot move false
     */
    public boolean nextMove(boolean terminatorMove) {
        if (startedRound) {
            switch (playerState) {
                case BEGIN:
                    if (terminatorMove && terminatorPresent) {
                        playerState = UserPlayerState.TERMINATOR_FIRST;
                        terminatorCanMove = false;
                    } else {
                        playerState = UserPlayerState.FIRST_MOVE;
                    }
                    break;

                case FIRST_MOVE:
                    if (terminatorMove && terminatorCanMove && terminatorPresent) {
                        playerState = UserPlayerState.TERMINATOR_SECOND;
                        terminatorCanMove = false;
                    } else {
                        playerState = UserPlayerState.SECOND_MOVE;
                    }
                    break;

                case SECOND_MOVE:
                    if (terminatorMove && terminatorCanMove && terminatorPresent) {
                        playerState = UserPlayerState.TERMINATOR_THIRD;
                        terminatorCanMove = false;
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
            }
        } else {
            return false;
        }

        return terminatorPresent && terminatorCanMove;
    }

    public void startRound(UserPlayer that) {
        updateBoardState(that);
        startedRound = true;
    }

    public void updateBoardState(@NotNull UserPlayer that) {
        PlayerBoard board = that.getPlayerBoard();
        this.boardState = board.getBoardState();
    }

    /**
     * Set the state to {@code BEGIN}, reset {@code terminatorCanMove} to true
     */
    public void endRound() {
        playerState = UserPlayerState.BEGIN;
        terminatorCanMove = true;
        startedRound = false;
    }

    public UserPlayerState getPlayerState() {
        return playerState == UserPlayerState.TERMINATOR_FIRST || playerState == UserPlayerState.TERMINATOR_SECOND || playerState == UserPlayerState.TERMINATOR_THIRD  ? UserPlayerState.TERMINATOR_MOVE : playerState;
    }

    public boolean terminatorCanMove() {
        return terminatorCanMove;
    }

    public List<PossibleAction> possibleActions() {

        switch (boardState) {
            case NORMAL:
                // TODO: return normal actions
                break;

            case FIRST_ADRENALINE:
                // TODO: return first adrenaline actions
                throw new UnsupportedOperationException();

            case SECOND_ADRENALINE:
                // TODO: return second adrenaline actions
                break;
        }

        throw new UnsupportedOperationException("not supported yet");
    }
}
