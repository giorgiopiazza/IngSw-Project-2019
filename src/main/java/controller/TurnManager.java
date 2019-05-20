package controller;

import enumerations.PossibleGameState;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    private UserPlayer turnOwner;
    private UserPlayer lastPlayer;
    private final UserPlayer lastRoundPlayer;

    private List<UserPlayer> players;
    private ArrayList<UserPlayer> damagedPlayers;
    private ArrayList<UserPlayer> afterFrenzy;
    private ArrayList<UserPlayer> beforeFrenzy;

    private PossibleGameState arrivingGameState;

    private int count;
    private int granadeCount;

    public TurnManager(List<UserPlayer> players) {
        this.players = players;
        this.lastRoundPlayer = players.get(players.size() - 1);
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

    public UserPlayer getLastRoundPlayer() {
        return this.lastRoundPlayer;
    }

    public void setDamagedPlayers(ArrayList<UserPlayer> damaged) {
        if(damaged == null) {
            this.damagedPlayers = new ArrayList<>();
        } else {
            this.damagedPlayers = damaged;
        }
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

    public void setArrivingGameState(PossibleGameState arrivingGameState) {
        this.arrivingGameState = arrivingGameState;
    }

    public PossibleGameState getArrivingGameState() {
        return this.arrivingGameState;
    }

    public void resetGranadeCount() {
        this.granadeCount = 0;
    }

    public void increaseGranadeCount() {
        ++this.granadeCount;
    }

    public int getGranadeCount() {
        return this.granadeCount;
    }

    public void nextTurn() {
        count++;
        count = count % players.size();
        turnOwner = players.get(count);
    }

    public void setFrenzyPlayers() {
        UserPlayer frenzyActivator = turnOwner;
        boolean beforeFirst = true;

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

    public boolean endOfRound() {
        if(turnOwner.equals(lastRoundPlayer)) {
            return true;
        } else {
            return false;
        }
    }

    public void giveTurn(UserPlayer damagedPlayer) {
        this.turnOwner = damagedPlayer;
    }
}
