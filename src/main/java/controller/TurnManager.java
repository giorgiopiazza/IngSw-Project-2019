package controller;

import enumerations.PossibleGameState;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

class TurnManager {
    private UserPlayer turnOwner;
    private UserPlayer lastPlayer;
    private final UserPlayer lastRoundPlayer;

    private List<UserPlayer> players;
    private ArrayList<UserPlayer> damagedPlayers;
    private ArrayList<UserPlayer> deathPlayers;

    private boolean secondAction;

    private ArrayList<UserPlayer> afterFrenzy;
    private ArrayList<UserPlayer> beforeFrenzy;

    private PossibleGameState arrivingGameState;

    private int count;
    private int turnCount;

    TurnManager(List<UserPlayer> players) {
        this.players = players;
        this.lastRoundPlayer = players.get(players.size() - 1);
        this.turnOwner = players.get(count);
        this.afterFrenzy = new ArrayList<>();
        this.beforeFrenzy = new ArrayList<>();

    }

    UserPlayer getTurnOwner() {
        return turnOwner;
    }

    void setLastPlayer() {
        this.lastPlayer = turnOwner;
    }

    UserPlayer getLastPlayer() {
        return this.lastPlayer;
    }

    void setDamagedPlayers(ArrayList<UserPlayer> damaged) {
        if (damaged == null) {
            this.damagedPlayers = new ArrayList<>();
        } else {
            this.damagedPlayers = damaged;
        }
    }

    ArrayList<UserPlayer> getDamagedPlayers() {
        return this.damagedPlayers;
    }

    void setDeathPlayers(ArrayList<UserPlayer> deaths) {
        if (deaths == null) {
            this.deathPlayers = new ArrayList<>();
        } else {
            this.deathPlayers = deaths;
        }
    }

    ArrayList<UserPlayer> getDeathPlayers() {
        return this.deathPlayers;
    }

    void setSecondAction(boolean secondAction) {
        this.secondAction = secondAction;
    }

    boolean isSecondAction() {
        return this.secondAction;
    }

    ArrayList<UserPlayer> getAfterFrenzy() {
        return this.afterFrenzy;
    }

    void setArrivingGameState(PossibleGameState arrivingGameState) {
        this.arrivingGameState = arrivingGameState;
    }

    PossibleGameState getArrivingGameState() {
        return this.arrivingGameState;
    }

    void resetCount() {
        this.turnCount = 0;
    }

    void increaseCount() {
        ++this.turnCount;
    }

    int getTurnCount() {
        return this.turnCount;
    }

    void nextTurn() {
        count++;
        count = count % players.size();
        turnOwner = players.get(count);
    }

    void setFrenzyPlayers() {
        UserPlayer frenzyActivator = turnOwner;
        boolean beforeFirst = true;

        do {
            nextTurn();

            if (turnOwner.isFirstPlayer()) {
                beforeFirst = false;
            }

            if (beforeFirst) {
                afterFrenzy.add(turnOwner);
            } else {
                beforeFrenzy.add(turnOwner);
            }

        } while (!turnOwner.equals(frenzyActivator));
    }

    boolean endOfRound() {
        return turnOwner.equals(lastRoundPlayer);
    }

    void giveTurn(UserPlayer damagedPlayer) {
        this.turnOwner = damagedPlayer;
    }
}
