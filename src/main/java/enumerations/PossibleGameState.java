package enumerations;

/**
 * Enumeration containing all the possible states that the {@link controller.GameManager GameManager} can have
 * while "controlling" the evolving of the game.
 */
public enum PossibleGameState {
    /**
     * Game State when wait to vote and entry in lobby
     */
    GAME_ROOM,
    /**
     * Game started
     */
    GAME_STARTED,
    GAME_ENDED,
    FINAL_FRENZY,
    SECOND_ACTION,
    MISSING_TERMINATOR_ACTION,
    GRANADE_USAGE,
    SCOPE_USAGE,
    PASS_NORMAL_TURN,
    PASS_NORMAL_BOT_TURN,
    RELOAD_PASS,
    PASS_FRENZY_TURN,
    ACTIONS_DONE,
    FRENZY_ACTIONS_DONE,
    MANAGE_DEATHS,
    TERMINATOR_RESPAWN
}
