package controller;

import enumerations.GameClientState;
import enumerations.UserPlayerState;
import exceptions.game.InvalidGameStateException;
import exceptions.player.ClientRoundManagerException;

class ClientRoundManager {

    private UserPlayerState playerState;
    private GameClientState gameClientState;

    private boolean secondFrenzyAction;

    private final boolean botPresent;
    private boolean botMoved;
    private boolean roundStarted;

    ClientRoundManager(boolean botPresent) {
        this.roundStarted = false;
        this.botPresent = botPresent;
        this.botMoved = false;

        this.playerState = UserPlayerState.SPAWN;
        this.gameClientState = GameClientState.NORMAL;

        this.secondFrenzyAction = false;
    }

    /**
     * Change the state of the player in this round
     */
    void nextState() {
        if (!roundStarted)
            throw new ClientRoundManagerException("Error, round not started yet (before call nextState() you must call beginRound())");

        switch (playerState) {
            case BOT_SPAWN:
                playerState = UserPlayerState.SPAWN;
                break;

            case SPAWN:
                handleBegin();
                break;

            case FIRST_ACTION:
                playerState = UserPlayerState.SECOND_ACTION;
                break;

            case SECOND_ACTION:
            case SECOND_FRENZY_ACTION:
            case SECOND_SCOPE_USAGE:
                handleSecondMove();
                break;

            case FIRST_FRENZY_ACTION:
                handleFirstFrenzy();
                break;

            case FIRST_SCOPE_USAGE:
                handleFirstScope();
                break;

            case BOT_ACTION:
                playerState = UserPlayerState.ENDING_PHASE;
                break;

            case ENDING_PHASE:
                playerState = UserPlayerState.END;
                break;

            case DEAD:
            case GRENADE_USAGE:
            case BOT_RESPAWN:
                playerState = UserPlayerState.FIRST_ACTION;
                break;

            case END:
                throw new ClientRoundManagerException("Error, in the UserPlayerState.END state you must call the endRound() method");

            default:
                throw new ClientRoundManagerException("Invalid State!");
        }
    }

    private void handleBegin() {
        if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.FIRST_ACTION;
        } else {
            playerState = UserPlayerState.FIRST_FRENZY_ACTION;
        }
    }

    private void handleSecondMove() {
        if (botPresent && !botMoved) {
            playerState = UserPlayerState.BOT_ACTION;
        } else {
            playerState = UserPlayerState.ENDING_PHASE;
        }
    }

    private void handleFirstFrenzy() {
        if (secondFrenzyAction) {
            playerState = UserPlayerState.SECOND_FRENZY_ACTION;
        } else {
            if (botPresent && !botMoved) {
                playerState = UserPlayerState.BOT_ACTION;
            } else {
                playerState = UserPlayerState.ENDING_PHASE;
            }
        }
    }

    private void handleFirstScope() {
        if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.SECOND_ACTION;
        } else {
            handleFirstFrenzy();
        }
    }

    void death() {
        playerState = UserPlayerState.DEAD;
    }

    void grenade() {
        playerState = UserPlayerState.GRENADE_USAGE;
    }

    void botSpawn() {
        playerState = UserPlayerState.BOT_SPAWN;
    }

    void botRespawn() {
        playerState = UserPlayerState.BOT_RESPAWN;
    }

    void targetingScope() {
        if (playerState == UserPlayerState.FIRST_ACTION || playerState == UserPlayerState.FIRST_FRENZY_ACTION) {
            playerState = UserPlayerState.FIRST_SCOPE_USAGE;
        } else if (playerState == UserPlayerState.SECOND_ACTION || playerState == UserPlayerState.SECOND_FRENZY_ACTION) {
            playerState = UserPlayerState.SECOND_SCOPE_USAGE;
        } else {
            throw new InvalidGameStateException();
        }
    }
    void firstAction() {
        handleBegin();
    }

    void beginRound() {
        roundStarted = true;
    }

    void setPlayerState(UserPlayerState playerState) {
        this.playerState = playerState;
    }

    /**
     * Set the state to {@code FIRST_ACTION}, reset {@code botMoved} to false
     */
    void endRound() {
        playerState = UserPlayerState.FIRST_ACTION;
        botMoved = false;
        roundStarted = false;
    }

    UserPlayerState getUserPlayerState() {
        return playerState;
    }

    void setSecondFrenzyAction(boolean secondFrenzyAction) {
        this.secondFrenzyAction = secondFrenzyAction;
    }

    /**
     * Check if the player have already done the bot move
     *
     * @return true if already moved, otherwise false
     */
    boolean hasBotMoved() {
        return botMoved;
    }

    boolean isBotPresent() {
        return botPresent;
    }

    void setFinalFrenzy() {
        this.gameClientState = GameClientState.FINAL_FRENZY;
    }

    GameClientState getGameClientState() {
        return gameClientState;
    }

    boolean isDoubleActionFrenzy() {
        return secondFrenzyAction;
    }
}
