package controller;

import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    private UserPlayer turnOwner;

    private List<UserPlayer> players;
    private List<UserPlayer> deathPlayers;

    private int count;

    public TurnManager(List<UserPlayer> players) {
        this.players = players;
        turnOwner = players.get(count);

        deathPlayers = new ArrayList<>();
    }

    public UserPlayer getTurnOwner() {
        return turnOwner;
    }

    public void addDeathPlayer(UserPlayer player) {
        deathPlayers.add(player);
    }

    public void nextTurn() {
        if (!deathPlayers.isEmpty()) {
            turnOwner = deathPlayers.remove(deathPlayers.size() - 1);
        } else {
            count++;
            count = count % players.size();
            turnOwner = players.get(count);
        }
    }
}
