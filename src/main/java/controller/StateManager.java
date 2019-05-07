package controller;

import model.player.UserPlayer;

public class StateManager {
    private UserPlayer currentPlayer;

    public StateManager(UserPlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setCurrentPlayer(UserPlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


}
