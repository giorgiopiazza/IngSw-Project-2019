package controller;

import enumerations.GameClientState;
import enumerations.PlayerBoardState;
import enumerations.PossibleAction;
import enumerations.UserPlayerState;
import exceptions.player.ClientRoundManagerException;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

public class ClientRoundManager {

    private UserPlayerState playerState;
    private PlayerBoardState boardState;
    private GameClientState gameClientState;

    private boolean botPresent;
    private boolean botCanMove;
    private boolean roundStarted;

    private UserPlayer that;

    public ClientRoundManager(UserPlayer that, boolean botPresent) {
        this.that = that;

        this.roundStarted = false;
        this.botPresent = botPresent;
        this.botCanMove = true;

        this.playerState = UserPlayerState.SPAWN;
        this.boardState = PlayerBoardState.NORMAL;
        this.gameClientState = GameClientState.NORMAL;
    }

    /**
     * Change the state of the player in this round
     */
    public void nextMove() {
        nextMove(false);
    }

    /**
     * Change the state of the player in this round
     *
     * @param botMove if the next move is a TERMINATOR_ACTION
     */
    public void nextMove(boolean botMove) {
        if (!botCanMove && botMove && botPresent) throw new ClientRoundManagerException("Cannot move terminator more than 1 time for round");
        if (!roundStarted) throw new ClientRoundManagerException("Error, round not started yet (before call nextMove() you must call beginRound())");
        if (playerState.equals(UserPlayerState.SECOND_ACTION) && !botMove && botPresent && botCanMove) throw new ClientRoundManagerException("The player didn't move the bot");
        if (that ==  null) throw new NullPointerException("UserPlayer \"that\" cannot be null");

        this.botCanMove = botMove;

        switch (playerState) {
            case SPAWN:
            case BEGIN:
                handleBegin();
                break;

            case FIRST_ACTION:
                handleFirstMove();
                break;

            case SECOND_ACTION:
                handleSecondMove();
                break;

            case TERMINATOR_FIRST:
                playerState = UserPlayerState.FIRST_ACTION;
                break;

            case TERMINATOR_SECOND:
                playerState = UserPlayerState.SECOND_ACTION;
                break;

            case TERMINATOR_THIRD:
                playerState = UserPlayerState.RELOAD;
                break;

            case RELOAD:
                playerState = UserPlayerState.END;
                break;

            case TERMINATOR_ACTION:
                throw new ClientRoundManagerException("Error, ClientRoundManager cannot be in UserPlayerState.TERMINATOR_ACTION state");

            case END:
                throw new ClientRoundManagerException("Error, in the UserPlayerState.END state you must call the endRound() method");
        }
    }

    private void handleBegin() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.TERMINATOR_FIRST;
        } else {
            playerState = UserPlayerState.FIRST_ACTION;
        }
    }

    private void handleFirstMove() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.TERMINATOR_SECOND;
        } else {
            playerState = UserPlayerState.SECOND_ACTION;
        }
    }

    private void handleSecondMove() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.TERMINATOR_THIRD;
        } else {
            playerState = UserPlayerState.RELOAD;
        }
    }

    public void beginRound() {
        this.boardState = that.getPlayerBoard().getBoardState();
        roundStarted = true;
    }

    /**
     * Set the state to {@code BEGIN}, reset {@code botCanMove} to true
     */
    public void endRound() {
        playerState = UserPlayerState.BEGIN;
        botCanMove = true;
        roundStarted = false;
    }

    public UserPlayerState getUserPlayerState() {
        return playerState == UserPlayerState.TERMINATOR_FIRST || playerState == UserPlayerState.TERMINATOR_SECOND || playerState == UserPlayerState.TERMINATOR_THIRD  ? UserPlayerState.TERMINATOR_ACTION : playerState;
    }

    /**
     * This method return the possible actions that the player can be in this round.
     * If in the list is present the PossibleAction.RELOAD, this action is not counted and another can be performed.
     * If in the list is present the PossibleAction.TERMINATOR_ACTION, means that the next move can be the terminator one,
     * if the round is in the UserPlayerState.SECOND_ACTION state, then the next move is necessarily the terminator one.
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
        if (playerState != UserPlayerState.END && playerState != UserPlayerState.BEGIN) {
            if (botCanMove && botPresent) actions.add(PossibleAction.TERMINATOR_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.MOVE_AND_PICK);
            actions.add(PossibleAction.SHOOT);
        }
    }

    private void firstAdrenalineActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END && playerState != UserPlayerState.BEGIN) {
            if (botCanMove && botPresent) actions.add(PossibleAction.TERMINATOR_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.ADRENALINE_PICK);
            actions.add(PossibleAction.SHOOT);
        }
    }

    private void secondAdrenalineActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END && playerState != UserPlayerState.BEGIN) {
            if (botCanMove && botPresent) actions.add(PossibleAction.TERMINATOR_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.ADRENALINE_PICK);
            actions.add(PossibleAction.ADRENALINE_SHOOT);
        }
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
     * Check if the player have already done the bot move
     *
     * @return true if already moved, otherwise false
     */
    public boolean hasBotMoved() {
        return !botCanMove;
    }

    public boolean isBotPresent() {
        return botPresent;
    }

    public boolean roundEnded() {
        return UserPlayerState.BEGIN == playerState && !roundStarted;
    }
}
