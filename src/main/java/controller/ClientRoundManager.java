package controller;

import enumerations.GameClientState;
import enumerations.PlayerBoardState;
import enumerations.PossibleAction;
import enumerations.UserPlayerState;
import exceptions.player.ClientRoundManagerException;
import model.player.Player;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

public class ClientRoundManager {

    private UserPlayerState playerState;
    private PlayerBoardState boardState;
    private GameClientState gameClientState;

    private boolean secondFrenzyAction;

    private boolean botPresent;
    private boolean botCanMove;
    private boolean roundStarted;

    private UserPlayer that;

    ClientRoundManager(UserPlayer that, boolean botPresent) {
        this.that = that;

        this.roundStarted = false;
        this.botPresent = botPresent;
        this.botCanMove = true;

        this.playerState = UserPlayerState.SPAWN;
        this.boardState = PlayerBoardState.NORMAL;
        this.gameClientState = GameClientState.NORMAL;

        this.secondFrenzyAction = false;
    }

    /**
     * Change the state of the player in this round
     *
     * @param botMove if the next move is a BOT_ACTION
     */
    void nextMove(boolean botMove) {
        if (!botCanMove && botMove && botPresent)
            throw new ClientRoundManagerException("Cannot move terminator more than 1 time for round");
        if (!roundStarted)
            throw new ClientRoundManagerException("Error, round not started yet (before call nextMove() you must call beginRound())");
        if (playerState.equals(UserPlayerState.SECOND_ACTION) && !botMove && botPresent && botCanMove)
            throw new ClientRoundManagerException("The player didn't move the bot");
        if (that == null) throw new NullPointerException("UserPlayer \"that\" cannot be null");

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

            case FIRST_FRENZY_ACTION:
                handleFirstFrenzy();
                break;

            case SECOND_FRENZY_ACTION:
                handleSecondFrenzy();
                break;

            case BOT_FIRST:
                handleBotFirst();
                break;

            case BOT_SECOND:
                handleBotSecond();
                break;

            case BOT_THIRD:
                handleBotThird();
                break;

            case RELOAD:
                playerState = UserPlayerState.END;
                break;

            case BOT_ACTION:
                throw new ClientRoundManagerException("Error, ClientRoundManager cannot be in UserPlayerState.BOT_ACTION state");

            case END:
                throw new ClientRoundManagerException("Error, in the UserPlayerState.END state you must call the endRound() method");
        }
    }

    private void handleBegin() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.BOT_FIRST;
        } else if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.FIRST_ACTION;
        } else {
            playerState = UserPlayerState.FIRST_FRENZY_ACTION;
        }
    }

    private void handleFirstMove() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.BOT_SECOND;
        } else {
            playerState = UserPlayerState.SECOND_ACTION;
        }
    }

    private void handleSecondMove() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.BOT_THIRD;
        } else {
            playerState = UserPlayerState.RELOAD;
        }
    }

    private void handleFirstFrenzy() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.BOT_SECOND;
        } else {
            if (secondFrenzyAction) {
                playerState = UserPlayerState.SECOND_FRENZY_ACTION;
            } else {
                playerState = UserPlayerState.END;
            }
        }
    }

    private void handleSecondFrenzy() {
        if (botPresent && botCanMove) {
            playerState = UserPlayerState.BOT_THIRD;
        } else {
            playerState = UserPlayerState.END;
        }
    }

    private void handleBotFirst() {
        if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.FIRST_ACTION;
        } else {
            playerState = UserPlayerState.FIRST_FRENZY_ACTION;
        }
    }

    private void handleBotSecond() {
        if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.SECOND_ACTION;
        } else {
            if (secondFrenzyAction) {
                playerState = UserPlayerState.SECOND_FRENZY_ACTION;
            } else {
                playerState = UserPlayerState.END;
            }
        }
    }

    private void handleBotThird() {
        if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.RELOAD;
        } else {
            playerState = UserPlayerState.END;
        }
    }

    void beginRound() {
        this.boardState = that.getPlayerBoard().getBoardState();
        roundStarted = true;
    }

    /**
     * Set the state to {@code BEGIN}, reset {@code botCanMove} to true
     */
    void endRound() {
        playerState = UserPlayerState.BEGIN;
        botCanMove = true;
        roundStarted = false;
    }

    UserPlayerState getUserPlayerState() {
        return playerState == UserPlayerState.BOT_FIRST || playerState == UserPlayerState.BOT_SECOND || playerState == UserPlayerState.BOT_THIRD ? UserPlayerState.BOT_ACTION : playerState;
    }

    /**
     * This method return the possible actions that the player can be in this round.
     * If in the list is present the PossibleAction.RELOAD, this action is not counted and another can be performed.
     * If in the list is present the PossibleAction.BOT_ACTION, means that the next move can be the terminator one,
     * if the round is in the UserPlayerState.SECOND_ACTION state, then the next move is necessarily the terminator one.
     *
     * @return a list with the possible actions that the player can perform in this round
     */
    List<PossibleAction> possibleActions() {
        List<PossibleAction> actions = new ArrayList<>();
        boardState = that.getPlayerBoard().getBoardState();

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

        return actions;
    }

    /**
     * Returns the final frenzy actions based on who activated the frenzy mode and the position of the player
     * in the game turn
     *
     * @param players         the list of inGame players username
     * @param frenzyActivator the player who activated the frenzy
     * @return the list of possible possibleFinalFrenzyActions for {@code that} player
     */
    List<PossibleAction> possibleFinalFrenzyActions(List<String> players, String frenzyActivator) {
        List<PossibleAction> actions = new ArrayList<>();

        int activatorIndex = players.indexOf(frenzyActivator);
        int playerIndex = players.indexOf(that.getUsername());

        if (playerIndex > activatorIndex) {
            actions.add(PossibleAction.FRENZY_MOVE);
            actions.add(PossibleAction.FRENZY_SHOOT);
            actions.add(PossibleAction.FRENZY_PICK);
            secondFrenzyAction = true;
        } else {
            actions.add(PossibleAction.LIGHT_FRENZY_SHOOT);
            actions.add(PossibleAction.LIGHT_FRENZY_PICK);
        }

        return actions;
    }

    GameClientState getGameClientState() {
        return gameClientState;
    }

    private void normalActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END && playerState != UserPlayerState.BEGIN) {
            if (botCanMove && botPresent) actions.add(PossibleAction.BOT_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.MOVE_AND_PICK);
            actions.add(PossibleAction.SHOOT);
            actions.add(PossibleAction.POWER_UP);
        }
    }

    private void firstAdrenalineActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END && playerState != UserPlayerState.BEGIN) {
            if (botCanMove && botPresent) actions.add(PossibleAction.BOT_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.ADRENALINE_PICK);
            actions.add(PossibleAction.SHOOT);
            actions.add(PossibleAction.POWER_UP);
        }
    }

    private void secondAdrenalineActions(List<PossibleAction> actions) {
        if (playerState != UserPlayerState.END && playerState != UserPlayerState.BEGIN) {
            if (botCanMove && botPresent) actions.add(PossibleAction.BOT_ACTION);
            actions.add(PossibleAction.MOVE);
            actions.add(PossibleAction.ADRENALINE_PICK);
            actions.add(PossibleAction.ADRENALINE_SHOOT);
            actions.add(PossibleAction.POWER_UP);
        }
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
    boolean hasBotMoved() {
        return !botCanMove;
    }

    boolean isBotPresent() {
        return botPresent;
    }

    public boolean roundEnded() {
        return UserPlayerState.BEGIN == playerState && !roundStarted;
    }
}
