package enumerations;

public enum PossibleAction {
    SPAWN_TERMINATOR, CHOOSE_SPAWN, CHOOSE_RESPAWN,                    // possible actions a player must do even before the waiting to play state
    MOVE, MOVE_AND_PICK, SHOOT, RELOAD,                                // possible actions a player can do if he has no ADRENALINE
    ADRENALINE_PICK, ADRENALINE_SHOOT,                                 // possible adrenaline actions
    FRENZY_MOVE, FRENZY_PICK, FRENZY_SHOOT,                            // possible x2 final frenzy actions
    LIGHT_FRENZY_PICK, LIGHT_FRENZY_MOVE                               // possible x1 final frenzy actions
}
