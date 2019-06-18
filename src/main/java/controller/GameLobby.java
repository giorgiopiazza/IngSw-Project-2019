package controller;

import enumerations.PlayerColor;
import exceptions.game.MaxPlayerException;
import network.message.GameVoteMessage;
import network.message.LobbyMessage;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class GameLobby implements Serializable {
    private static final long serialVersionUID = 9107773386569787630L;

    private ArrayList<LobbyMessage> inLobbyPlayers;
    private ArrayList<GameVoteMessage> votedPlayers;

    private boolean terminator;
    private int skullNum;

    GameLobby(boolean terminator, int skullNum) {
        this.inLobbyPlayers = new ArrayList<>();
        this.votedPlayers = new ArrayList<>();

        this.terminator = terminator;
        this.skullNum = skullNum;
    }

    void addPlayer(LobbyMessage inLobbyPlayer) {
        if (isLobbyFull()) {
            throw new MaxPlayerException();
        }

        inLobbyPlayers.add(inLobbyPlayer);
    }

    ArrayList<LobbyMessage> getInLobbyPlayers() {
        return inLobbyPlayers;
    }

    void addPlayerVote(GameVoteMessage votedPlayer) {
        if (votedPlayer.getMapVote() > 0 || votedPlayer.getMapVote() < 5) {
            votedPlayers.add(votedPlayer);
        }
    }

    ArrayList<GameVoteMessage> getVotedPlayers() {
        return votedPlayers;
    }

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
                return voteMap.getValue();
            } else {
                return getRandomMap();
            }
        } else {
            return getRandomMap();
        }
    }

    private int getRandomMap() {
        return ThreadLocalRandom.current().nextInt(1, 5);
    }

    boolean getTerminatorPresence() {
        return this.terminator;
    }

    Integer getSkullNum() {
        return this.skullNum;
    }

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

    boolean isLobbyFull() {
        return (terminator && inLobbyPlayers.size() == 4 ||
                !terminator && inLobbyPlayers.size() == 5);
    }
}
