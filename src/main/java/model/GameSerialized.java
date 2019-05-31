package model;

import enumerations.GameState;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.GameMap;
import model.player.KillShot;
import model.player.Player;
import model.player.Terminator;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameSerialized implements Serializable  {

    private GameState currentState;
    private GameMap gameMap;

    private ArrayList<UserPlayer> players;
    private Terminator terminator;
    private boolean terminatorPresent;

    private int killShotNum;
    private KillShot[] killShotsTrack;

    // attributes for each single player, initialized thanks to the username passed to the constructor
    private int points;
    private PowerupCard[] powerupCards;
    private PowerupCard spawningPowerup;

    public GameSerialized(String userName) {
        Game instance = Game.getInstance();

        currentState = instance.getState();
        players = new ArrayList<>(instance.getPlayers());

        terminatorPresent = instance.isTerminatorPresent();
        if (terminatorPresent) terminator = (Terminator) instance.getTerminator();

        killShotsTrack = instance.getKillShotsTrack() != null ? Arrays.copyOf(instance.getKillShotsTrack(), instance.getKillShotsTrack().length) : null;
        killShotNum = instance.getKillShotNum();

        gameMap = new GameMap(instance.getGameMap());
        setSecretAttributes(userName);
    }

    private void setSecretAttributes(String userName) {
        UserPlayer receivingPlayer = Game.getInstance().getUserPlayerByUsername(userName);

        this.points = receivingPlayer.getPoints();
        this.powerupCards = receivingPlayer.getPowerups();
        this.spawningPowerup = receivingPlayer.getSpawningCard();
    }

    public ArrayList<UserPlayer> getPlayers() {
        return players;
    }

    public boolean isTerminatorPresent() {
        return terminatorPresent;
    }

    public int getKillShotNum() {
        return killShotNum;
    }

    public KillShot[] getKillShotsTrack() {
        return killShotsTrack;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public int getPoints() {
        return this.points;
    }

    public PowerupCard[] getPowerupCards() {
        return this.powerupCards;
    }

    public PowerupCard getSpawningPowerup() {
        return this.spawningPowerup;
    }

    public List<PowerupCard> getPowerUps() {
        List<PowerupCard> powerupList = Arrays.asList(this.powerupCards);
        if (spawningPowerup != null) powerupList.add(this.spawningPowerup);

        return powerupList;
    }

    public Terminator getTerminator() {
        return terminator;
    }

    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> allPlayers = new ArrayList<>(players);

        if(terminatorPresent) {
            allPlayers.add(terminator);
            return allPlayers;
        } else {
            return allPlayers;
        }
    }

    //utility methods for cli debugging
    public void setTerminator(Terminator terminator) {
        this.terminatorPresent = true;
        this.terminator = terminator;
    }

    public void setPlayers(ArrayList<UserPlayer> userPlayers) {
        this.players = userPlayers;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    @Override
    public String toString() {
        return "GameSerialized{" +
                "currentState=" + currentState +
                ", gameMap=" + gameMap +
                ", players=" + players +
                ", terminator=" + terminator +
                ", terminatorPresent=" + terminatorPresent +
                ", killShotNum=" + killShotNum +
                ", killShotsTrack=" + Arrays.toString(killShotsTrack) +
                ", points=" + points +
                ", powerupCards=" + Arrays.toString(powerupCards) +
                ", spawningPowerup=" + spawningPowerup +
                '}';
    }
}
