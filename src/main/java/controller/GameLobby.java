package controller;

import enumerations.PlayerColor;
import exceptions.game.MaxPlayerException;
import model.Game;
import network.message.GameVoteMessage;
import network.message.LobbyMessage;

import java.io.Serializable;
import java.util.*;

/**
 * This Class handles the players connecting or disconnecting to the game. It is both used to start the game and to
 * manage the disconnection or reconnection to an already started game
 */
class GameLobby implements Serializable {
    private static final long serialVersionUID = 9107773386569787630L;

    private ArrayList<LobbyMessage> inLobbyPlayers;
    private ArrayList<GameVoteMessage> votedPlayers;

    private boolean terminator;
    private int skullNum;

    /**
     * Creates an instance of the class after the terminator presence and the skullNumm have been decided
     *
     * @param terminator boolean representing the presence of the terminator
     * @param skullNum integer representing the number of the skulls of the imminent starting game
     */
    GameLobby(boolean terminator, int skullNum) {
        this.inLobbyPlayers = new ArrayList<>();
        this.votedPlayers = new ArrayList<>();

        this.terminator = terminator;
        this.skullNum = skullNum;
    }

    /**
     * Adds a {@link model.player.UserPlayer UserPlayer} to the lobby. This means adding a {@link LobbyMessage LobbyMessage}
     * containing his name
     *
     * @param inLobbyPlayer the {@link LobbyMessage LobbyMessage} to be added
     */
    void addPlayer(LobbyMessage inLobbyPlayer) {
        if (isLobbyFull()) {
            throw new MaxPlayerException();
        }

        inLobbyPlayers.add(inLobbyPlayer);
    }

    /**
     * @return an ArrayList of all the {@link model.player.UserPlayer Players} in the lobby. This means an ArrayList
     * of {@link LobbyMessage LobbyMessages} containing their name
     */
    ArrayList<LobbyMessage> getInLobbyPlayers() {
        return inLobbyPlayers;
    }

    /**
     * Adds the vote of a {@link model.player.UserPlayer Player} for the playing map. Used at the beginning of the game
     * to choose the map to play with
     *
     * @param votedPlayer the {@link GameVoteMessage GameVoteMessage} containing the vote
     */
    void addPlayerVote(GameVoteMessage votedPlayer) {
        if (votedPlayer.getMapVote() > 0 || votedPlayer.getMapVote() < 5) {
            votedPlayers.add(votedPlayer);
        }
    }

    /**
     * @return an ArrayList of {@link GameVoteMessage GameVoteMessages} containing the votes of the {@link model.player.UserPlayer
     * Players} in the lobby.
     */
    ArrayList<GameVoteMessage> getVotedPlayers() {
        return votedPlayers;
    }

    /**
     * @return the Integer corresponding to the favourite Map for the {@link model.player.UserPlayer UserPlayers}
     * in the Lobby
     */
    Integer getFavouriteMap() {
        if (!votedPlayers.isEmpty()) {
            ArrayList<Integer> playersVotes = new ArrayList<>();

            for (GameVoteMessage mapVote : votedPlayers) {
                playersVotes.add(mapVote.getMapVote());
            }

            Map<Integer, Integer> map = new HashMap<>();
            playersVotes.forEach(t -> map.compute(t, (k, i) -> i == null ? 1 : i + 1));

            Map.Entry<Integer, Integer> voteMap = map.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).orElse(null);
            if (voteMap != null) {
                return voteMap.getKey();
            } else {
                return getRandomMap();
            }
        } else {
            return getRandomMap();
        }
    }

    /**
     * @return a random Integer between 1 and 4 in case the Map did not receive any vote
     */
    private int getRandomMap() {
        return Game.rand.nextInt(4) + 1;
    }

    /**
     * @return true if the Bot is present in the Game
     */
    boolean getTerminatorPresence() {
        return this.terminator;
    }

    /**
     * @return the Number of Skulls chosen for this Game
     */
    Integer getSkullNum() {
        return this.skullNum;
    }

    /**
     * @return an ArrayList of {@link PlayerColor PlayerColors} with the colours that are not already chosen while setting
     * up the Game
     */
    ArrayList<PlayerColor> getUnusedColors() {
        ArrayList<PlayerColor> playerColorsList = new ArrayList<>();

        for (LobbyMessage message : inLobbyPlayers) {
            playerColorsList.add(message.getChosenColor());
        }

        ArrayList<PlayerColor> unusedColorsList = new ArrayList<>();
        for (PlayerColor curColor : PlayerColor.values()) {
            if (!playerColorsList.contains(curColor)) {
                unusedColorsList.add(curColor);
            }
        }

        return unusedColorsList;
    }

    /**
     * @return {@code true} if the Lobby is full.
     * The Lobby is full when:
     * (i) There are already 5 {@link LobbyMessage LobbyMessages}
     * (ii) There are already 4 {@link LobbyMessage LobbyMessages} and the {@link #terminator} is {@code true}
     */
    boolean isLobbyFull() {
        return (terminator && inLobbyPlayers.size() == 4 ||
                !terminator && inLobbyPlayers.size() == 5);
    }
}
