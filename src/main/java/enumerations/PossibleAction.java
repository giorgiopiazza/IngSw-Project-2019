package enumerations;

public enum PossibleAction {
    SPAWN_BOT("Choose a bot spawn point"),
    RESPAWN_BOT("Choose a bot respawn point"),
    CHOOSE_SPAWN("Choose spawn point"),
    CHOOSE_RESPAWN("Choose respawn point"),
    POWER_UP("Use a powerup"),
    GRENADE_USAGE("Use the tagback tagbackGrenade"),
    SCOPE_USAGE("Choose if use targeting scope"),
    MOVE("Move up to 3 boxes"),
    MOVE_AND_PICK("Move up to 1 box and pick up weapon or power up"),
    SHOOT("Shoot a player"),
    RELOAD("Reload your weapons"),
    ADRENALINE_PICK("Move up to 2 boxes and pick up weapon or power up"),
    ADRENALINE_SHOOT("Move up to 1 box and shoot a player"),
    FRENZY_MOVE("TODO Frenzy move desc"),
    FRENZY_PICK("TODO Frenzy move pick desc"),
    FRENZY_SHOOT("TODO Frenzy move shoot desc"),
    LIGHT_FRENZY_PICK("TODO Frenzy move pick desc"),
    LIGHT_FRENZY_SHOOT("TODO Frenzy move shot desc"),
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
