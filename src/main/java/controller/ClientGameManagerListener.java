package controller;

import enumerations.PlayerColor;
import enumerations.PossibleAction;
import model.GameSerialized;
import model.player.Player;
import network.message.ConnectionResponse;
import network.message.Response;

import java.util.List;

interface ClientGameManagerListener {
    void connectionResponse(ConnectionResponse response);
    void askColor(List<PlayerColor> availableColors);
    void lobbyJoinResponse(Response response);

    void firstPlayerCommunication(String username);
    void notYourTurn();
    void gameStateUpdate(GameSerialized gameSerialized);
    void responseError(String error);

    PossibleAction askAction();

    void botSpawn();
    void spawn();
    void move();
    void moveAndPick();
    void shoot();

    void adrenalinePick();
    void adrenalineShoot();

    void frenzyMove();
    void frenzyPick();
    void frenzyShoot();
    void lightFrenzyPick();
    void lightFrenzyShoot();

    void botAction();
    void reload();
    void powerup();
    void passTurn();

    void onPlayerDisconnect(String username);
    void notifyGameEnd(List<Player> winners);
}
