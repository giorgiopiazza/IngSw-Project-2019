package network.client;

import enumerations.PlayerColor;
import enumerations.PossibleAction;
import model.GameSerialized;
import model.player.Player;
import network.message.ConnectionResponse;
import network.message.GameVoteResponse;
import network.message.Response;

import java.util.List;

/**
 * This listener is used to let the game progress via user inputs.
 * Methods are called when is received a specific message from the server
 */
interface ClientGameManagerListener {
    /**
     * Handles the response to a connection
     */
    void connectionResponse(ConnectionResponse response);

    /**
     * Handles the response to a connection for a loaded game
     */
    void loadResponse();

    /**
     * Asks to the user the player color
     */
    void askColor(List<PlayerColor> availableColors);

    /**
     * Handles the response to the lobby join
     *
     * @param response to the lobby join
     */
    void lobbyJoinResponse(Response response);

    /**
     * Notifies an update of players in the lobby
     *
     * @param users list of users in the lobby
     */
    void playersLobbyUpdate(List<String> users);

    /**
     * Handles the response to a vote
     *
     * @param gameVoteResponse the response to the vote
     */
    void voteResponse(GameVoteResponse gameVoteResponse);

    /**
     * Communicates who is the first player
     *
     * @param username first player username
     */
    void firstPlayerCommunication(String username);

    /**
     * Tells the client that is not his turn
     */
    void notYourTurn();

    /**
     * Tells the client what are the possible actions
     *
     * @param possibleActions list of possible actions
     */
    void displayActions(List<PossibleAction> possibleActions);

    /**
     * Handles a game state update
     *
     * @param gameSerialized update of the game
     */
    void gameStateUpdate(GameSerialized gameSerialized);

    /**
     * Displays the response error
     *
     * @param error error message
     */
    void responseError(String error);

    /**
     * Requests the client the bot spawn
     */
    void botSpawn();

    /**
     * Requests the client the bot respawn
     */
    void botRespawn();

    /**
     * Requests the client the player spawn
     */
    void spawn();

    /**
     * Requests the client a player move
     */
    void move();

    /**
     * Requests the client a player move and pick
     */
    void moveAndPick();

    /**
     * Requests the client a shoot
     */
    void shoot();

    /**
     * Requests the client an adrenaline player move and pick
     */
    void adrenalinePick();

    /**
     * Requests the client an adrenaline player shoot
     */
    void adrenalineShoot();

    /**
     * Requests the client a frenzy player move
     */
    void frenzyMove();

    /**
     * Requests the client a frenzy player move and pick
     */
    void frenzyPick();

    /**
     * Requests the client a frenzy shoot
     */
    void frenzyShoot();

    /**
     * Requests the client a light frenzy player move and pick
     */
    void lightFrenzyPick();

    /**
     * Requests the client a light frenzy shoot
     */
    void lightFrenzyShoot();

    /**
     * Requests the client a bot action
     */
    void botAction();

    /**
     * Requests the client a targeting scope usage
     */
    void targetingScope();

    /**
     * Requests the client a tagback grenade usage
     */
    void tagbackGrenade();

    /**
     * Requests the client a reload
     */
    void reload();

    /**
     * Requests the client a powerup usage
     */
    void powerup();

    /**
     * Request the client a pass turn
     */
    void passTurn();

    /**
     * Notifies the client about a player disconnection
     *
     * @param username of the player who disconnected
     */
    void onPlayerDisconnect(String username);

    /**
     * Notifies the client about the game end
     *
     * @param winners list of winner players
     */
    void notifyGameEnd(List<Player> winners);
}
