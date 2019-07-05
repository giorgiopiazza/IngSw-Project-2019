package enumerations;

/**
 * Enumeration representing all the possible states that the client round manager can have.
 * These states are mapped to the ones in the controller thanks to a mapping function
 * used to reload a game in persistency
 */
public enum UserPlayerState {
    BOT_SPAWN,
    SPAWN,
    FIRST_ACTION,
    SECOND_ACTION,
    FIRST_FRENZY_ACTION,
    SECOND_FRENZY_ACTION,
    ENDING_PHASE,
    END,
    DEAD,
    BOT_RESPAWN,
    BOT_ACTION,
    GRENADE_USAGE,
    FIRST_SCOPE_USAGE,
    SECOND_SCOPE_USAGE,
    GAME_ENDED
}
