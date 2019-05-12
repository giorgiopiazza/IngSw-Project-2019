package model;

import enumerations.GameState;
import model.cards.Deck;
import model.player.KillShot;
import model.player.Player;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class GameSerialized implements Serializable  {

    private GameState currentState;
    private boolean gameStarted;

    private ArrayList<UserPlayer> players;
    private boolean terminatorPresent;
    private Player terminator;

    private int killShotNum;
    private KillShot[] killShotsTrack;

    private Deck weaponsCardsDeck;
    private Deck powerupCardsDeck;
    private Deck ammoTileDeck;

    public GameSerialized() {
        Game instance = Game.getInstance();

        currentState = instance.getState();
        gameStarted = instance.isGameStarted();

        players = instance.getPlayers() != null ? new ArrayList<>(instance.getPlayers()) : null;
        terminatorPresent = instance.isTerminatorPresent();
        terminator = instance.getTerminator();

        killShotsTrack = instance.getKillShotsTrack() != null ? Arrays.copyOf(instance.getKillShotsTrack(), instance.getKillShotsTrack().length) : null;
        killShotNum = instance.getKillShotNum();

        weaponsCardsDeck = instance.getWeaponsCardsDeck() != null ? new Deck(instance.getWeaponsCardsDeck()) : null;
        ammoTileDeck = instance.getAmmoTileDeck() != null ? new Deck(instance.getAmmoTileDeck(), true) : null;
        powerupCardsDeck = instance.getPowerupCardsDeck() != null ? new Deck(instance.getPowerupCardsDeck(), true) : null;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
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

    public Deck getWeaponsCardsDeck() {
        return weaponsCardsDeck;
    }

    public void setWeaponsCardsDeck(Deck weaponsCardsDeck) {
        this.weaponsCardsDeck = weaponsCardsDeck;
    }

    public Deck getPowerupCardsDeck() {
        return powerupCardsDeck;
    }

    public void setPowerupCardsDeck(Deck powerupCardsDeck) {
        this.powerupCardsDeck = powerupCardsDeck;
    }

    public Deck getAmmoTileDeck() {
        return ammoTileDeck;
    }

    public void setAmmoTileDeck(Deck ammoTileDeck) {
        this.ammoTileDeck = ammoTileDeck;
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
                ", gameStarted=" + gameStarted +
                ", players=" + players +
                ", terminatorPresent=" + terminatorPresent +
                ", terminator=" + terminator +
                ", killShotNum=" + killShotNum +
                ", killShotsTrack=" + Arrays.toString(killShotsTrack) +
                ", weaponsCardsDeck=" + weaponsCardsDeck +
                ", powerupCardsDeck=" + powerupCardsDeck +
                ", ammoTileDeck=" + ammoTileDeck +
                '}';
    }
}
