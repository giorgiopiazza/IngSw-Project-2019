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

        players = new ArrayList<>(instance.getPlayers());
        terminatorPresent = instance.isTerminatorPresent();
        terminator = instance.getTerminator();

        killShotsTrack = Arrays.copyOf(instance.getKillShotsTrack(), instance.getKillShotsTrack().length);
        killShotNum = instance.getKillShotNum();

        weaponsCardsDeck = new Deck(instance.getWeaponsCardsDeck());
        ammoTileDeck = new Deck(instance.getAmmoTileDeck(), true);
        powerupCardsDeck = new Deck(instance.getPowerupCardsDeck(), true);
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
}
