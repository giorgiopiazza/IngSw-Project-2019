package enumerations;

public enum PossibleAction {
    SPAWN_BOT("Choose a bot spawn point"),
    CHOOSE_SPAWN("Choose spawn point"),
    CHOOSE_RESPAWN("Choose respawn point"),
    POWER_UP("Use a powerup"),
    MOVE("Move up to 3 boxes"),
    MOVE_AND_PICK("Move up to 1 box and pick up weapon or power up"),
    SHOOT("Shoot a player"),
    RELOAD("Reload your weapons"),
    ADRENALINE_PICK("Move up to 2 boxes and pick up weapon or power up"),
    ADRENALINE_SHOOT("Move up to 1 box and shoot a player"),
    FRENZY_MOVE("TODO Frenzy move desc"),
    FRENZY_PICK("TODO Frenzy move pick"),
    FRENZY_SHOOT("TODO Frenzy move shoot"),
    LIGHT_FRENZY_PICK("TODO Frenzy move pick"),
    LIGHT_FRENZY_SHOOT("TODO Frenzy move shot"),
    BOT_ACTION("Do the bot action"),
    PASS_TURN("Pass the turn");

    private String description;

    PossibleAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
