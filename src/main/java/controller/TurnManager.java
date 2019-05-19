package controller;

import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    private UserPlayer turnOwner;
    private UserPlayer lastPlayer;

    private List<UserPlayer> players;
    private ArrayList<UserPlayer> damagedPlayers;
    private ArrayList<UserPlayer> afterFrenzy;
    private ArrayList<UserPlayer> beforeFrenzy;

    private int count;

    public TurnManager(List<UserPlayer> players) {
        this.players = players;
        this.turnOwner = players.get(count);
        this.afterFrenzy = new ArrayList<>();
        this.beforeFrenzy = new ArrayList<>();

    }

    public UserPlayer getTurnOwner() {
        return turnOwner;
    }

    public void setLastPlayer() {
        this.lastPlayer = turnOwner;
    }

    public UserPlayer getLastPlayer() {
        return this.lastPlayer;
    }

    public void setDamagedPlayers(ArrayList<UserPlayer> damaged) {
        this.damagedPlayers = damaged;
    }

    public ArrayList<UserPlayer> getDamagedPlayers() {
        return this.damagedPlayers;
    }

    public ArrayList<UserPlayer> getAfterFrenzy() {
        return this.afterFrenzy;
    }

    public ArrayList<UserPlayer> getBeforeFrenzy() {
        return this.beforeFrenzy;
    }
    public void nextTurn() {
        count++;
        count = count % players.size();
        turnOwner = players.get(count);
    }

    public void setFrenzyPlayers() {
        UserPlayer frenzyActivator = turnOwner;
        boolean beforeFirst = true;
        UserPlayer tempPlayer;

        do {
            nextTurn();

            if(turnOwner.isFirstPlayer()) {
                beforeFirst = false;
            }

            if(beforeFirst) {
                afterFrenzy.add(turnOwner);
            } else {
                beforeFrenzy.add(turnOwner);
            }

        } while (!turnOwner.equals(frenzyActivator));
    }
}
