package controller;

import enumerations.PlayerColor;
import network.message.GameSetupMessage;
import network.message.LobbyMessage;

import java.util.ArrayList;

public class GameLobby {
    private ArrayList<LobbyMessage> inLobbyPlayers;
    private ArrayList<GameSetupMessage> votedPlayers;

    GameLobby () {
        this.inLobbyPlayers = new ArrayList<>();
        this.votedPlayers = new ArrayList<>();
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
        // TODO add lambda to get max occurrences
        // if no one voted for the preference a default map is chosen
        return 2;
    }

    boolean getTerminatorPresence() {
        int inFavorPlayers = 0;

        for(GameSetupMessage setupMessage : votedPlayers) {
            if(setupMessage.getTerminatorVote()) {
                ++inFavorPlayers;
            }
        }

        // tie decision is always in favor
        if(inLobbyPlayers.size() == 5) {
            return false;
        } else {
            return inFavorPlayers >= (inLobbyPlayers.size() % 2 + 1);
        }
    }

    Integer getFavouriteSkullsNum() {
        // TODO add lambda to get max occurrences
        // if no one voted for the preference a default number is chosen
        return 5;
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
