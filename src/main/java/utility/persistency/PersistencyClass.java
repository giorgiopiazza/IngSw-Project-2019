package utility.persistency;

import controller.GameManager;
import controller.TurnManager;
import model.Game;
import model.player.Terminator;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.ArrayList;

public class PersistencyClass implements Serializable {
    private GameManager gameManager;
    private TurnManager turnManager;
    private ArrayList<NotTransientPlayer> playersCopy;

    public PersistencyClass(GameManager gameManager) {
        this.gameManager = gameManager;
        this.turnManager = gameManager.getRoundManager().getTurnManager();
        this.playersCopy = setPlayersCopy();
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public TurnManager getTurnManager() {
        return this.turnManager;
    }

    public ArrayList<NotTransientPlayer> getPlayersCopy() {
        return this.playersCopy;
    }

    private ArrayList<NotTransientPlayer> setPlayersCopy() {
        Game gameSaved = gameManager.getGameInstance();
        ArrayList<NotTransientPlayer> notTransientPlayers = new ArrayList<>();

        for(UserPlayer userPlayer : gameSaved.getPlayers()) {
            NotTransientPlayer tempPlayer = new NotTransientPlayer(userPlayer);
            notTransientPlayers.add(tempPlayer);
        }

        if(gameSaved.isTerminatorPresent()) {
            notTransientPlayers.add(new NotTransientPlayer((Terminator) gameSaved.getTerminator()));
        }

        return notTransientPlayers;
    }
}
