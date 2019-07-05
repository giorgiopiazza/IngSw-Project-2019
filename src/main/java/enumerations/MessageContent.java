package enumerations;

/**
 * Enumerations that contains the content of each {@link network.message.Message Message} exchanged through
 * the network
 */
public enum MessageContent {
    CONNECTION, CONNECTION_RESPONSE, GET_IN_LOBBY, COLOR, COLOR_RESPONSE, LOBBY_VOTE, VOTE_RESPONSE,
    BOT_SPAWN, DISCARD_POWERUP, BOT_ACTION, MOVE, MOVE_PICK, SHOOT, RELOAD, POWERUP_USAGE,
    PASS_TURN, RESPONSE, GAME_STATE, WINNER, DISCONNECTION, PING, READY, RECONNECTION, GAME_LOAD,
    PLAYERS_IN_LOBBY
}
