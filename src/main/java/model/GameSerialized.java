package model;

import enumerations.GameState;
import model.player.KillShot;
import model.player.Player;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class GameSerialized implements Serializable  {

    private GameState currentState;

    private ArrayList<UserPlayer> players; // TODO Need to find another way
    private boolean terminatorPresent;
    private Player terminator;

    private int killShotNum;
    private KillShot[] killShotsTrack;

    public GameSerialized() {
        Game instance = Game.getInstance();

        currentState = instance.getState();
        players = instance.getPlayers() != null ? new ArrayList<>(instance.getPlayers()) : null;
        terminatorPresent = instance.isTerminatorPresent();
        terminator = instance.getTerminator();

        killShotsTrack = instance.getKillShotsTrack() != null ? Arrays.copyOf(instance.getKillShotsTrack(), instance.getKillShotsTrack().length) : null;
        killShotNum = instance.getKillShotNum();
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }

    public ArrayList<UserPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<UserPlayer> players) {
        this.players = players;
    }

    public boolean isTerminatorPresent() {
        return terminatorPresent;
    }

    public void setTerminatorPresent(boolean terminatorPresent) {
        this.terminatorPresent = terminatorPresent;
    }

    public Player getTerminator() {
        return terminator;
    }

    public void setTerminator(Player terminator) {
        this.terminator = terminator;
    }

    public int getKillShotNum() {
        return killShotNum;
    }

    public void setKillShotNum(int killShotNum) {
        this.killShotNum = killShotNum;
    }

    public KillShot[] getKillShotsTrack() {
        return killShotsTrack;
    }

    public void setKillShotsTrack(KillShot[] killShotsTrack) {
        this.killShotsTrack = killShotsTrack;
    }

    @Override
    public String toString() {
        return "GameSerialized{" +
                "currentState=" + currentState +
                ", players=" + players +
                ", terminatorPresent=" + terminatorPresent +
                ", terminator=" + terminator +
                ", killShotNum=" + killShotNum +
                ", killShotsTrack=" + Arrays.toString(killShotsTrack) +
                '}';
    }
}
