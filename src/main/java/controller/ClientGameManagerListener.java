package controller;

import enumerations.PossibleAction;
import model.GameSerialized;
import model.player.Player;

import java.util.List;

interface ClientGameManagerListener {
    void firstPlayerCommunication(String username);
    void notYourTurn();
    void gameStateUpdate(GameSerialized gameSerialized);
    void responseError(String error);

    PossibleAction askAction();

    void spawn();
    void move();
    void moveAndPick();
    void shoot();

    boolean askBotMove();
    void askReload();

    void botMove();
    void reload();

    void notifyGameEnd(List<Player> winners);
}
