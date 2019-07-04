package utility.persistency;

import controller.GameManager;
import controller.TurnManager;
import model.Game;
import model.player.Bot;
import model.player.UserPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class PersistencyClass implements Serializable {
    private static final long serialVersionUID = 2545891111714662670L;

    private GameManager gameManager;
    private TurnManager turnManager;
    private ArrayList<NotTransientPlayer> playersCopy;

    PersistencyClass(GameManager gameManager) {
        this.gameManager = gameManager;
        this.turnManager = gameManager.getRoundManager().getTurnManager();
        this.playersCopy = setPlayersCopy();
    }

    GameManager getGameManager() {
        return this.gameManager;
    }

    TurnManager getTurnManager() {
        return this.turnManager;
    }

    List<NotTransientPlayer> getPlayersCopy() {
        return this.playersCopy;
    }

    private ArrayList<NotTransientPlayer> setPlayersCopy() {
        Game gameSaved = gameManager.getGameInstance();
        ArrayList<NotTransientPlayer> notTransientPlayers = new ArrayList<>();

        for(UserPlayer userPlayer : gameSaved.getPlayers()) {
            NotTransientPlayer tempPlayer = new NotTransientPlayer(userPlayer);
            notTransientPlayers.add(tempPlayer);
        }

        if(gameSaved.isBotPresent()) {
            notTransientPlayers.add(new NotTransientPlayer((Bot) gameSaved.getBot()));
        }

        return notTransientPlayers;
    }
}
