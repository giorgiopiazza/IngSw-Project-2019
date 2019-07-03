package model.player;

import enumerations.PlayerColor;
import utility.GameCostants;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Bot bot = (Bot) o;
        return spawnTurn == bot.spawnTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), spawnTurn);
    }
}
