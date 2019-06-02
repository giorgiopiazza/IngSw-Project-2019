package enumerations;

public enum PossibleAction {
    SPAWN_TERMINATOR, CHOOSE_SPAWN, CHOOSE_RESPAWN,                    // possible actions a player must do even before the waiting to play state
    MOVE, MOVE_AND_PICK, SHOOT, RELOAD,                                // possible actions a player can do if he has no ADRENALINE
    ADRENALINE_PICK, ADRENALINE_SHOOT,                                 // possible adrenaline actions
    FRENZY_MOVE, FRENZY_PICK, FRENZY_SHOOT,                            // possible x2 final frenzy actions
    LIGHT_FRENZY_PICK, LIGHT_FRENZY_SHOOT,                             // possible x1 final frenzy actions
    TERMINATOR_ACTION;                                                 // action that everybody has if the terminator is present in the game

    @Override
    public String toString() {
        switch (this) {
            case SPAWN_TERMINATOR:
                return "Spawn terminator";
            case CHOOSE_SPAWN:
                return "Choose spawn point";
            case CHOOSE_RESPAWN:
                return "Choose respawn point";
            case MOVE:
                return "Move up to 3 boxes";
            case MOVE_AND_PICK:
                return "Move up to 1 box and pick up weapon or power up";
            case SHOOT:
                return "Shoot a player";
            case RELOAD:
                return "Reload your weapons";
            case ADRENALINE_PICK:
                return "Move up to 2 boxes and pick up weapon or power up";
            case ADRENALINE_SHOOT:
                return "Move up to 1 box and shoot a player";

            default:
                return this + "";
        }
    }
}
