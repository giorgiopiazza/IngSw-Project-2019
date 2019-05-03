package model;

import enumerations.Color;
import enumerations.GameState;
import enumerations.SquareType;
import exceptions.game.*;
import exceptions.map.InvalidPlayerPositionException;
import model.cards.AmmoTile;
import model.cards.Deck;
import model.cards.WeaponCard;
import model.map.CardSquare;
import model.map.Map;
import model.map.SpawnSquare;
import model.map.Square;
import model.player.*;
import utility.AmmoTileParser;
import utility.PowerupParser;
import utility.WeaponParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private GameState currentState;
    private int killShotNum;
    private boolean terminatorPresent;
    private Player terminator;
    private List<UserPlayer> players;
    private KillShot[] killShotsTrack;
    private Deck weaponsCardsDeck;
    private Deck powerupCardsDeck;
    private Deck ammoTileDeck;
    private Map gameMap;

    /**
     * Initialize singleton Game instance
     */
    private Game() {
        init();
    }

    /**
     * Game initialization
     */
    public void init() {
        players = new ArrayList<>();
        terminator = null;
        this.currentState = GameState.NORMAL;
        killShotsTrack = new KillShot[MAX_KILLSHOT];
        terminatorPresent = false;
        started = false;
        killShotNum = 0;

        weaponsCardsDeck = null;
        powerupCardsDeck = null;
        ammoTileDeck = null;
        gameMap = null;
    }

    public void setGameMap(int mapType) {
        this.gameMap = new Map(mapType);
    }

    /**
     * The singleton instance of the game returns, if it has not been created it allocates it as well
     *
     * @return the singleton instance
     */
    public static Game getInstance() {
        if (instance == null)
            instance = new Game();
        return instance;
    }

    /**
     * @return the current state Game
     */
    public GameState getState() {
        return this.currentState;
    }

    /**
     * Method that sets the current state of the game
     *
     * @param currentState GameState to be changed
     */
    public void setState(GameState currentState) {
        this.currentState = currentState;
    }

    /**
     * @return the instance of the PowerupDeck
     */
    public Deck getPowerupCardsDeck() {
        return this.powerupCardsDeck;
    }

    /**
     * Adds a player to the game
     *
     * @param player the player to add to the game
     * @throws GameAlreadyStartedException if the game has already started
     * @throws MaxPlayerException          if the maximum number of players has been reached
     */
    public void addPlayer(UserPlayer player) throws GameAlreadyStartedException, MaxPlayerException {
        if (started)
            throw new GameAlreadyStartedException("It is not possible to add a player when the game has already started");
        if (player == null) throw new NullPointerException("Player cannot be null");
        if (players.size() >= 5 || (players.size() >= 4 && terminatorPresent)) throw new MaxPlayerException();
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

    /**
     * Checks if game is ready to start
     *
     * @return {@code true} if the game is ready {@code false} otherwise
     */
    private boolean isGameReadyToStart() throws NotEnoughPlayersException {
        if (players.size() < 3) throw new NotEnoughPlayersException();

        return gameMap != null;
    }

    /**
     * Starts the game
     *
     * @throws GameAlreadyStartedException when game is already started
     * @throws NotEnoughPlayersException   when there aren't enough players for start the game
     */
    public void startGame(int killShotNum) throws GameAlreadyStartedException, NotEnoughPlayersException, GameNotReadyException {
        if (started) throw new GameAlreadyStartedException("The game is already in progress");

        if (!isGameReadyToStart()) {
            throw new GameNotReadyException();
        }

        if (killShotNum < 5 || killShotNum > 8) {
            throw new InvalidKillshotNumber();
        }

        started = true;
        this.killShotNum = killShotNum;

        initializeDecks();
        pickFirstPlayer();

        distributeCards();
    }

    /**
     * Picks the first player and reorders the players list
     */
    private void pickFirstPlayer() {
        int first = (new Random()).nextInt(players.size());
        players.get(first).setFirstPlayer();

        List<UserPlayer> newPlayerList = new ArrayList<>();

        for (int i = first; i < players.size(); ++i) {
            newPlayerList.add(players.get(i));
        }

        for (int i = 0; i < first; ++i) {
            newPlayerList.add(players.get(i));
        }

        players = newPlayerList;
    }

    /**
     * Distributes cards on every Square
     */
    private void distributeCards() {
        for (int i = 0; i < Map.MAX_ROWS; ++i) {
            for (int j = 0; j < Map.MAX_COLUMNS; ++j) {
                Square square = gameMap.getSquare(i, j);

                if (square != null) {
                    placeCardOnSquare(square);
                }
            }
        }
    }

    /**
     * Places card(s) of right type on a square
     * @param square where card(s) must be placed
     */
    private void placeCardOnSquare(Square square) {
        if (square.getSquareType() == SquareType.SPAWN) {
            SpawnSquare spawnSquare = (SpawnSquare) square;

            for (int k = 0; k < 3; ++k) {
                spawnSquare.addWeapon((WeaponCard) weaponsCardsDeck.draw());
            }
        } else {
            CardSquare cardSquare = (CardSquare) square;

            cardSquare.setAmmoTile((AmmoTile) ammoTileDeck.draw());
        }
    }

    /**
     * Initializes the three decks: {@code weaponsCardDeck}, {@code ammoTileDeck} and {@code powerupCardsDeck}
     */
    public void initializeDecks() {
        this.weaponsCardsDeck = WeaponParser.parseCards();
        this.ammoTileDeck = AmmoTileParser.parseCards();
        this.powerupCardsDeck = PowerupParser.parseCards();
    }

    public void stopGame() throws GameAlreadyStartedException {
        if (!started) throw new GameAlreadyStartedException("The game is not in progress");

        init();
    }

    /**
     * @throws GameAlreadyStartedException if game already started
     */
    public void flush() throws GameAlreadyStartedException {
        if (started) throw new GameAlreadyStartedException("Cannot flush with game started");

        players.clear();
        initializeDecks();

        gameMap = null;
        terminator = null;

        terminatorPresent = false;

        clearKillshots();
    }

    /**
     * Returns the number of skulls remaining during the game
     *
     * @return the number of remaining skulls
     */
    public int remainingSkulls() {
        int leftSkulls = 0;

        for (int i = 0; i < killShotNum; i++) {
            if (killShotsTrack[i] == null) leftSkulls++;
        }

        return leftSkulls;
    }

    /**
     * Adds a killshot to the game by removing a skull
     *
     * @param killShot killshot to add
     */
    public void addKillShot(KillShot killShot) {
        if (killShot == null) throw new NullPointerException("Killshot cannot be null");

        for (int i = 0; i < killShotNum; i++) {
            if (killShotsTrack[i] == null) {
                killShotsTrack[i] = killShot;
                return;
            }
        }

        throw new KillShotsTerminatedException();
    }

    public void clearKillshots() throws GameAlreadyStartedException {
        if (started) throw new GameAlreadyStartedException("Cannot clear killshots while game is started");
        killShotsTrack = new KillShot[MAX_KILLSHOT];
    }

    public boolean isTerminatorPresent() {
        return terminatorPresent;
    }

    /**
     * Enable or disable setTerminator mode, true to enable
     *
     * @param terminatorPresent true to enable setTerminator mode, otherwise false
     * @return the created instance of terminator
     * @throws GameAlreadyStartedException if the game has already started
     * @throws MaxPlayerException          if the game is full
     */
    public Player setTerminator(boolean terminatorPresent) throws GameAlreadyStartedException, MaxPlayerException {
        if (started)
            throw new GameAlreadyStartedException("It is not possible to set the setTerminator player when the game has already started.");
        if (players.size() >= 5 && terminatorPresent)
            throw new MaxPlayerException("Can not add Terminator with 5 players");
        this.terminatorPresent = terminatorPresent;

        if (terminatorPresent) terminator = new Terminator(firstColorUnused(), new PlayerBoard());
        else terminator = null;

        return terminator;
    }

    /**
     * Find the first color that no player uses, otherwise {@code null}
     *
     * @return the first color not used by any player
     */
    private Color firstColorUnused() {
        ArrayList<Color> ar = new ArrayList<>();

        for (UserPlayer player : players) {
            ar.add(player.getColor());
        }

        for (int i = 0; i < Color.values().length; i++) {
            if (ar.contains(Color.values()[i])) return Color.values()[i];
        }

        return null;
    }

    /**
     * Spawn the player to a spawn point on the map
     *
     * @param player         the player to spawn
     * @param playerPosition the player's spawn position
     * @throws GameAlreadyStartedException if the game has not started
     */
    public void spawnPlayer(UserPlayer player, PlayerPosition playerPosition) throws GameAlreadyStartedException {
        if (player == null || playerPosition == null)
            throw new NullPointerException("Player or playerPosition cannot be null");
        if (!players.contains(player)) throw new UnknownPlayerException();
        if (!started) throw new GameAlreadyStartedException("Game not started yet");

        try {
            Square temp = gameMap.getSquare(playerPosition);

            if (temp == null) {
                throw new InvalidPlayerPositionException();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidPlayerPositionException();
        }

        player.setPosition(playerPosition);
    }

    /**
     * Spawn the terminator player to a spawn point on the map
     *
     * @param playerPosition the player's spawn position
     * @throws GameAlreadyStartedException if the game has not started
     */
    public void spawnTerminator(PlayerPosition playerPosition) throws GameAlreadyStartedException {
        if (playerPosition == null) throw new NullPointerException("playerPosition cannot be null");
        if (!started) throw new GameAlreadyStartedException("Game not started yet");
        if (!terminatorPresent) throw new TerminatorNotSetException();

        terminator.setPosition(playerPosition);
    }

    /**
     * Function that returns true if the game started, otherwise false
     *
     * @return true if the game started, otherwise false
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * The number of killshot for this Game
     *
     * @return number of killshot set
     */
    public int getKillShotNum() {
        return killShotNum;
    }

    /**
     * Return the instance of terminator {@code player} for this game
     *
     * @return the terminator instance
     */
    public Player getTerminator() {
        return (terminatorPresent) ? terminator : null;
    }

    /**
     * @return the GameMap
     */
    public Map getGameMap() {
        return gameMap;
    }

    /**
     * @return the List of players in the game
     */
    public List<UserPlayer> getPlayers() {
        return players;
    }

    /**
     * Method to obtain the UserPlayer with the specified ID
     *
     * @param id you want to obtain the related UserPlayer
     * @return the UserPlayer with the ID passed
     */
    public UserPlayer getPlayerByID(int id) {
        for (UserPlayer p : players) {
            if (p.getId() == id) return p;
        }
        throw new MissingPlayerIDException(id);
    }

    /**
     * Method to obtain the positions of the players passed
     *
     * @param players ArrayList of players you need their position
     * @return the ArrayList of positions of the players
     */
    public List<PlayerPosition> getPlayersPositions(List<Player> players) {
        List<PlayerPosition> positions = new ArrayList<>();

        for (Player player : players) {
            positions.add(player.getPosition());
        }

        return positions;
    }

    /**
     * Method to obtain the player that started to play the game
     *
     * @return the UserPLayer who started the game
     */
    public UserPlayer getFirstPlayer() {
        for (UserPlayer player : players) {
            if (player.isFirstPlayer()) {
                return player;
            }
        }

        throw new NoFirstPlayerException();
    }

    /**
     * Method that returns the players who, in the final frenzy mode,
     * have to obtain the greater effects, these players are:
     * all the players next to the one who activated the frenzy mode
     * and before the first one who started the game
     *
     * @param frenzyActivator playerID of the player who activated the final frenzy mode
     * @return the List of UserPlayers whose IDs respect the rule of the final frenzy mode
     */
    public List<UserPlayer> getBeforeFirstFrenzyPlayers(int frenzyActivator) {
        List<UserPlayer> frenzyPlayers = new ArrayList<>();
        int firstPlayerID = getFirstPlayer().getId();

        for (UserPlayer player : players) {
            if (player.getId() < firstPlayerID && player.getId() > frenzyActivator) {
                frenzyPlayers.add(player);
            }
        }

        return frenzyPlayers;


    }

    /**
     * Complementary method to getBeforeFirstFrenzyPlayers
     *
     * @param frenzyActivator playerID of the player who activated the final frenzy mode
     * @return the List of UserPlayers whose IDs respect the rule of the final frenzy mode
     */
    public List<UserPlayer> getAfterFirstFrenzyPlayers(int frenzyActivator) {
        List<UserPlayer> frenzyPlayers = new ArrayList<>();
        int firstPlayerID = getFirstPlayer().getId();

        for (UserPlayer player : players) {
            if (player.getId() >= firstPlayerID && player.getId() <= frenzyActivator) {
                frenzyPlayers.add(player);
            }
        }

        return frenzyPlayers;
    }
}
