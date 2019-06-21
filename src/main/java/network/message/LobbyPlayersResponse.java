package network.message;

import enumerations.MessageContent;
import model.Game;
import utility.GameCostants;

import java.util.ArrayList;

public class LobbyPlayersResponse extends Message {
    private ArrayList<String> users;

    public LobbyPlayersResponse(ArrayList<String> users) {
        super(GameCostants.GOD_NAME, null, MessageContent.PLAYERS_IN_LOBBY);
        this.users = users;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
