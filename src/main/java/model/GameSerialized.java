package model;

import enumerations.GameState;
import enumerations.PossibleAction;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.GameMap;
import model.player.KillShot;
import model.player.Player;
import model.player.Bot;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class GameSerialized implements Serializable {
    private static final long serialVersionUID = 526685006552543525L;

    private GameState currentState;
    private GameMap gameMap;

    private ArrayList<UserPlayer> players;
    private Bot bot;
    private Boolean botPresent;
    private boolean botActionDone;

    private int killShotNum;
    private KillShot[] killShotsTrack;
    private ArrayList<KillShot> finalFrenzyKillShots;

    // attributes for each single player, initialized thanks to the username passed to the constructor
    private int points;
    private PowerupCard[] powerupCards;
    private PowerupCard spawningPowerup;

    public GameSerialized(String userName) {
        Game instance = Game.getInstance();

        currentState = instance.getState();

        if (instance.getPlayers() != null) {
            players = new ArrayList<>(instance.getPlayers());
        } else {
            players = new ArrayList<>();
        }

        botPresent = instance.isBotPresent();
        if (botPresent) {
            bot = (Bot) instance.getBot();
            botActionDone = !((UserPlayer) Game.getInstance().getPlayerByName(userName)).getPossibleActions().contains(PossibleAction.BOT_ACTION);
        } else {
            botActionDone = true;
        }

        killShotsTrack = instance.getKillShotsTrack() != null ? Arrays.copyOf(instance.getKillShotsTrack(), instance.getKillShotsTrack().length) : null;
        killShotNum = instance.getKillShotNum();

        if (instance.getFinalFrenzyKillShots() != null) {
            finalFrenzyKillShots = new ArrayList<>(instance.getFinalFrenzyKillShots());
        } else {
            finalFrenzyKillShots = new ArrayList<>();
        }

        gameMap = new GameMap(instance.getGameMap());
        setSecretAttributes(userName);
    }

    private void setSecretAttributes(String userName) {
        Player receivingPlayer = Game.getInstance().getUserPlayerByUsername(userName);
        UserPlayer userPlayer = (UserPlayer) receivingPlayer;

        this.points = userPlayer.getPoints();
        this.powerupCards = userPlayer.getPowerups();
        this.spawningPowerup = userPlayer.getSpawningCard();
    }

    public List<UserPlayer> getPlayers() {
        return players;
    }

    public boolean isBotPresent() {
        return botPresent;
    }

    public boolean isBotActionDone() {
        return botActionDone;
    }

    public int getKillShotNum() {
        return killShotNum;
    }

    public KillShot[] getKillShotsTrack() {
        return killShotsTrack;
    }

    public List<KillShot> getFinalFrenzyKillShots() {
        return finalFrenzyKillShots;
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

    public List<PowerupCard> getPowerups() {
        List<PowerupCard> powerupList = new ArrayList<>(Arrays.asList(this.powerupCards));
        powerupList = powerupList.stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (spawningPowerup != null) powerupList.add(this.spawningPowerup);

        return powerupList;
    }

    public PowerupCard getSpawnPowerup() {
        return spawningPowerup;
    }

    public List<WeaponCard> getPlayerWeapons(String username) {
        for (UserPlayer p : players) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return Arrays.asList(p.getWeapons());
            }
        }

        return new ArrayList<>();
    }

    public Bot getBot() {
        return bot;
    }

    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>(players);

        if (botPresent) {
            allPlayers.add(bot);
            return allPlayers;
        } else {
            return allPlayers;
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    //utility methods for cli debugging
    public void setBot(Bot bot) {
        this.botPresent = true;
        this.bot = bot;
    }

    public void setPlayers(List<UserPlayer> userPlayers) {
        if (userPlayers != null) {
            this.players = new ArrayList<>(userPlayers);
        }
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
                ", terminator=" + bot +
                ", isBotPresent=" + botPresent +
                ", killShotNum=" + killShotNum +
                ", killShotsTrack=" + Arrays.toString(killShotsTrack) +
                ", points=" + points +
                ", powerupCards=" + Arrays.toString(powerupCards) +
                ", spawningPowerup=" + spawningPowerup +
                '}';
    }
}
