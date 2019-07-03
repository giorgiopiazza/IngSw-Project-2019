package network.message;

import enumerations.MessageContent;
import model.Game;
import utility.GameCostants;

import java.util.ArrayList;

public class LobbyPlayersResponse extends Message {
    private static final long serialVersionUID = 6870316479006394730L;
    private ArrayList<String> users;

    public LobbyPlayersResponse(ArrayList<String> users) {
        super(GameCostants.GOD_NAME, null, MessageContent.PLAYERS_IN_LOBBY);
        this.users = users;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
