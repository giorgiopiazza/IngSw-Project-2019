package controller;

import enumerations.PossibleAction;
import model.GameSerialized;

interface ClientGameManagerListener {
    void firstPlayerCommunication(String username);
    void waitTurn();
    void gameStateUpdate(GameSerialized gameSerialized);

    PossibleAction askAction();

    void spawn();
    void move();
    void moveAndPick();
    void shoot();

    boolean askBotMove();
    boolean askReload();

    void botMove();
    void reload();

}
