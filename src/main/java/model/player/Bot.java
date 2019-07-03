package model.player;

import enumerations.PlayerColor;
import model.Game;
import utility.GameCostants;

public class Bot extends Player {
    private static final long serialVersionUID = 7578529420778904200L;
    private boolean spawnTurn;
    public Bot(PlayerColor color, PlayerBoard playerBoard) {
        super(GameCostants.BOT_NAME, color, playerBoard);
    }

    public Bot(Bot other) {
        super(other);
        this.spawnTurn = other.spawnTurn;
    }

    public boolean isSpawnTurn() {
        return spawnTurn;
    }

    public void setSpawnTurn(boolean spawnTurn) {
        this.spawnTurn = spawnTurn;
    }
}
