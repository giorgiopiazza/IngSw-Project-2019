package controller;

import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    private UserPlayer turnOwner;

    private List<UserPlayer> players;
    private List<UserPlayer> deathPlayers;
    private ArrayList<UserPlayer> damagedPlayers;
    private UserPlayer frenzyActivator;

    private int count;

    public TurnManager(List<UserPlayer> players) {
        this.players = players;
        this.frenzyActivator = null;
        this.turnOwner = players.get(count);

        this.deathPlayers = new ArrayList<>();
    }

    public UserPlayer getTurnOwner() {
        return turnOwner;
    }

    public void addDeathPlayer(UserPlayer player) {
        deathPlayers.add(player);
    }

    public void setDamagedPlayers(ArrayList<UserPlayer> damaged) {
        this.damagedPlayers = damaged;
    }

    public ArrayList<UserPlayer> getDamagedPlayers() {
        return this.damagedPlayers;
    }

    public void setFrenzyActivator(UserPlayer frenzyActivator) {
        this.frenzyActivator = frenzyActivator;
    }

    public UserPlayer getFrenzyActivator() {
        return this.frenzyActivator;
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
