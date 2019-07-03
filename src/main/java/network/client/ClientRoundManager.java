package network.client;

import enumerations.GameClientState;
import enumerations.UserPlayerState;
import exceptions.game.InvalidGameStateException;
import exceptions.player.ClientRoundManagerException;
import model.player.UserPlayer;

/**
 * Owns the game state machine for a client
 */
class ClientRoundManager {

    private UserPlayerState playerState;
    private GameClientState gameClientState;

    private boolean secondFrenzyAction;

    private final boolean botPresent;
    private boolean botMoved;
    private boolean botCanMove;

    private boolean roundStarted;

    ClientRoundManager(boolean botPresent) {
        this.roundStarted = false;
        this.botPresent = botPresent;
        this.botMoved = false;
        this.botCanMove = true;

        this.playerState = UserPlayerState.SPAWN;
        this.gameClientState = GameClientState.NORMAL;

        this.secondFrenzyAction = false;
    }

    /**
     * Change the state of the player to the next one
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
            case BOT_RESPAWN:
                playerState = UserPlayerState.END;
                break;

            case DEAD:
            case GRENADE_USAGE:
                playerState = UserPlayerState.FIRST_ACTION;
                break;

            case END:
                throw new ClientRoundManagerException("Error, in the UserPlayerState.END state you must call the endRound() method");

            default:
                throw new ClientRoundManagerException("Invalid State!");
        }
    }

    /**
     * Handles the next state in the begin phase of the game (after the spawn)
     */
    private void handleBegin() {
        if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.FIRST_ACTION;
        } else {
            playerState = UserPlayerState.FIRST_FRENZY_ACTION;
        }
    }

    /**
     * Handles the next state after the second move of the player
     */
    private void handleSecondMove() {
        if (botPresent && !botMoved && botCanMove) {
            playerState = UserPlayerState.BOT_ACTION;
        } else {
            playerState = UserPlayerState.ENDING_PHASE;
        }
    }

    /**
     * Handles the next state after the first frenzy move
     */
    private void handleFirstFrenzy() {
        if (secondFrenzyAction) {
            playerState = UserPlayerState.SECOND_FRENZY_ACTION;
        } else {
            if (botPresent && !botMoved && botCanMove) {
                playerState = UserPlayerState.BOT_ACTION;
            } else {
                playerState = UserPlayerState.ENDING_PHASE;
            }
        }
    }

    /**
     * Handles the next state in case of scope usage
     */
    private void handleFirstScope() {
        if (gameClientState == GameClientState.NORMAL) {
            playerState = UserPlayerState.SECOND_ACTION;
        } else {
            handleFirstFrenzy();
        }
    }

    /**
     * Set the state to the dead state
     */
    void death() {
        playerState = UserPlayerState.DEAD;
    }

    /**
     * Set the state to the grenade state
     */
    void grenade() {
        playerState = UserPlayerState.GRENADE_USAGE;
    }

    /**
     * Set the state to the bot spawn state
     */
    void botSpawn() {
        playerState = UserPlayerState.BOT_SPAWN;
    }

    /**
     * Set the state to the bot respawn state
     */
    void botRespawn() {
        playerState = UserPlayerState.BOT_RESPAWN;
    }

    /**
     * Set the state to the player spawn
     */
    void spawn() {
        playerState = UserPlayerState.SPAWN;
    }

    /**
     * Set the state to the targeting scope state
     */
    void targetingScope() {
        if (playerState == UserPlayerState.FIRST_ACTION || playerState == UserPlayerState.FIRST_FRENZY_ACTION) {
            playerState = UserPlayerState.FIRST_SCOPE_USAGE;
        } else if (playerState == UserPlayerState.SECOND_ACTION || playerState == UserPlayerState.SECOND_FRENZY_ACTION) {
            playerState = UserPlayerState.SECOND_SCOPE_USAGE;
        } else {
            throw new InvalidGameStateException();
        }
    }

    /**
     * Set the state after a reconnection
     */
    void reconnection() {
        handleBegin();
    }

    /**
     * Begins the round
     */
    void beginRound() {
        roundStarted = true;
    }

    /**
     * Set the state to {@code FIRST_ACTION}, reset {@code botMoved} to false
     */
    void endRound() {
        playerState = UserPlayerState.FIRST_ACTION;
        roundStarted = false;

        botMoved = false;
        botCanMove = true;
    }

    /**
     * Sets the second frenzy action if the player has to do two frenzy action
     *
     * @param secondFrenzyAction {@code true} if the player has got two action, {@code false} otherwise
     */
    void setSecondFrenzyAction(boolean secondFrenzyAction) {
        this.secondFrenzyAction = secondFrenzyAction;
    }

    /**
     * Sets the player state
     *
     * @param playerState player state that have to be setted
     */
    void setPlayerState(UserPlayerState playerState) {
        this.playerState = playerState;
    }

    /**
     * @return the current user player state
     */
    UserPlayerState getUserPlayerState() {
        return playerState;
    }

    /**
     * Check if the player has already done the bot move
     *
     * @return {@code true} if already moved, {@code false} otherwise
     */
    boolean hasBotMoved() {
        return botMoved;
    }

    /**
     * @return {@code true} if bot is present
     */
    boolean isBotPresent() {
        return botPresent;
    }

    /**
     * @return {@code true} if bot can move present
     */
    boolean isBotCanMove() {
        return botCanMove;
    }

    /**
     * Sets to false botCanMove because at the first turn the bot can't be moved
     */
    void setBotFirstTurn() {
        this.botCanMove = false;
    }

    /**
     * Sets that the bot move has been done
     */
    void setBotMoved() {
        this.botMoved = true;
    }

    /**
     * Sets the final frenzy game state
     */
    void setFinalFrenzy() {
        this.gameClientState = GameClientState.FINAL_FRENZY;
    }

    /**
     * @return the game client state
     */
    GameClientState getGameClientState() {
        return gameClientState;
    }

    /**
     * @return {@code true} if the player has got two frenzy moves, {@code false} otherwise
     */
    boolean isDoubleActionFrenzy() {
        return secondFrenzyAction;
    }
}
