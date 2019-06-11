package controller;

import enumerations.PlayerColor;
import network.message.GameSetupMessage;
import network.message.LobbyMessage;

import java.io.Serializable;
import java.util.*;

public class GameLobby implements Serializable {
    private static final long serialVersionUID = 9107773386569787630L;

    private ArrayList<LobbyMessage> inLobbyPlayers;
    private ArrayList<GameSetupMessage> votedPlayers;

    private boolean terminator;
    private int skullNum;

    GameLobby (boolean terminator, int skullNum) {
        this.inLobbyPlayers = new ArrayList<>();
        this.votedPlayers = new ArrayList<>();

        this.terminator = terminator;
        this.skullNum = skullNum;
    }

    void addPlayer(LobbyMessage inLobbyPlayer) {
        this.inLobbyPlayers.add(inLobbyPlayer);
    }

    ArrayList<LobbyMessage> getInLobbyPlayers() {
        return this.inLobbyPlayers;
    }

    void addPlayerVote(GameSetupMessage votedPlayer) {
        this.votedPlayers.add(votedPlayer);
    }

    ArrayList<GameSetupMessage> getVotedPlayers() {
        return this.votedPlayers;
    }

    Integer getFavouriteMap() {
        if(!votedPlayers.isEmpty()) {
            ArrayList<Integer> playersVotes = new ArrayList<>();

            for(GameSetupMessage mapVote : votedPlayers) {
                playersVotes.add(mapVote.getMapVote());
            }

            Map<Integer, Integer> map = new HashMap<>();
            playersVotes.forEach(t -> map.compute(t, (k, i) -> i == null ? 1 : i + 1));
            return map.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
        } else {
            return 2;
        }
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
}
