package model.player;

import enumerations.PlayerColor;
import model.Game;

public class Bot extends Player {
    private boolean spawnTurn;
    public Bot(PlayerColor color, PlayerBoard playerBoard) {
        super(Game.BOT, color, playerBoard);
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
