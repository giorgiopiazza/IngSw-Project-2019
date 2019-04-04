package model;

import enumerations.Color;
import exceptions.AdrenalinaException;
import exceptions.game.*;
import model.cards.Deck;
import model.player.*;

import java.util.ArrayList;
import java.util.List;

public class Game {
    /**
     * Maximum number of skulls in a game: 8
     */
    public static final int MAX_KILLSHOT = 8;
    /**
     * Singleton instance of Game
     */
    private static Game instance;
    private boolean started;
    private int killShotNum;
    private boolean terminatorPresent;
    private Player terminator;
    private List<Player> players;
    private KillShot[] killShotsTrack;
    private Deck weaponsCardsDeck;
    private Deck powerupCardsDeck;
    private Deck ammoCardsDeck;

    /**
     * Initialize singleton Game instance
     */
    private Game() {
        players = new ArrayList<>();
        killShotsTrack = new KillShot[MAX_KILLSHOT];
        terminatorPresent = false;
        started = false;
        // TODO: mettere le giuste carte in ogni deck, per ora creo deck vuoto
        weaponsCardsDeck = new Deck();
        powerupCardsDeck = new Deck();
        ammoCardsDeck = new Deck();
    }

    /**
     * The singleton instance of the game returns, if it has not been created it allocates it as well
     *
     * @return the singleton instance
     */
    public static Game getInstance() {
        if(instance == null)
            instance = new Game();
        return instance;
    }

    /**
     * Adds a player to the game
     *
     * @param player the player to add to the game
     * @throws GameAlreadyStartedException if the game has already started
     * @throws MaxPlayerException if the maximum number of players has been reached
     */
    public void addPlayer(Player player) throws AdrenalinaException {
        if(started) throw new GameAlreadyStartedException("it is not possible to add a player when the game has already started");
        if(player == null) throw new NullPointerException("Player cannot be null");
        if(players.size() >= 5) throw new MaxPlayerException();
        players.add(player);
    }

    /**
     * Number of players added to the game
     *
     * @return number of players
     */
    public int playersNumber() {
        return players.size();
    }

    public void startGame() throws AdrenalinaException {
        if(started) throw new GameAlreadyStartedException("the game is already in progress");
        if(players.size() < 3) throw new NotEnoughPlayersException();
        started = true;

        weaponsCardsDeck.flush();
        ammoCardsDeck.flush();
        powerupCardsDeck.flush();

        initializeDecks();

        // TODO: implementation of startGame()
    }

    /**
     * Initializes the three decks: <code>weaponsCardDeck</code>, <code>ammoCardsDeck</code> and <code>powerupCardsDeck</code>
     */
    private void initializeDecks() {

    }

    public void stopGame() throws GameAlreadyStartedException {
        if(!started) throw new GameAlreadyStartedException("the game is not in progress");
        // TODO: implementation of stopGame()
    }

    /**
     * Returns the number of skulls remaining during the game
     *
     * @return the number of remaining skulls
     */
    public int remainingSkulls() {
        int leftSkulls = 0;

        for(int i=0;i<killShotNum;i++) {
            if(killShotsTrack[i] == null) leftSkulls++;
        }

        return leftSkulls;
    }

    /**
     * Adds a killshot to the game by removing a skull
     *
     * @param killShot killshot to add
     */
    public void addKillShot(KillShot killShot) {
        if(killShot == null) throw new NullPointerException("killshot cannot be null");

        boolean added = false;

        for(int i=0;i<killShotNum;i++) {
            if(killShotsTrack[i] == null) {
                killShotsTrack[i] = killShot;
                added = true;
            }
        }

        if(!added) throw new KillShotsTerminatedException();
    }

    /**
     * Set the number of skulls in the game
     *
     * @param killShotNum number of killshots
     * @throws GameAlreadyStartedException if the game has already started
     */
    public void setKillShotNum(int killShotNum) throws GameAlreadyStartedException {
        if(killShotNum > MAX_KILLSHOT) throw new MaximumKillshotExceededException();
        if(started) throw new GameAlreadyStartedException("it is not possible to set the number of killshot when the game is in progress");
        this.killShotNum = killShotNum;
    }

    public boolean isTerminator() {
        return terminatorPresent;
    }

    /**
     * Enable or disable setTerminator mode, true to enable
     *
     * @param terminatorPresent true to enable setTerminator mode, otherwise false
     * @throws GameAlreadyStartedException if the game has already started
     */
    public void setTerminator(boolean terminatorPresent) throws AdrenalinaException {
        if(players.size() >= 5) throw new MaxPlayerException("Can not add Terminator with 5 players");
        if(started) throw new GameAlreadyStartedException("it is not possible to set the setTerminator player when the game has already started.");
        this.terminatorPresent = terminatorPresent;

        terminator = new Terminator(firstColorUnused(), new PlayerBoard());
    }

    /**
     * Find the first color that no player uses, otherwise <code>null</code>
     *
     * @return the first color not used by any player
     */
    private Color firstColorUnused() {
        ArrayList<Color> ar = new ArrayList<>();

        for(Player player : players) {
            ar.add(player.getColor());
        }

        for(int i=0;i<Color.values().length;i++) {
            if(ar.contains(Color.values()[i])) return Color.values()[i];
        }

        return null;
    }

    /**
     * Spawn the player to a spawn point on the map
     *
     * @param player the player to spawn
     * @param playerPosition the player's spawn position
     * @throws GameAlreadyStartedException if the game has not started
     */
    public void spawnPlayer(Player player, PlayerPosition playerPosition) throws GameAlreadyStartedException {
        if(player == null || playerPosition == null) throw new NullPointerException("player or playerPosition cannot be null");
        if(!players.contains(player)) throw new UnknownPlayerException();
        if(!started) throw new GameAlreadyStartedException("Game not started yet");
        player.setPosition(playerPosition);
    }

    /**
     * Function that returns true if the game started, otherwise false
     *
     * @return true if the game started, otherwise false
     */
    public boolean isStarted() {
        return started;
    }
}
