package model.player;

import enumerations.PlayerColor;
import model.Game;

public class Terminator extends Player {
    private boolean spawnTurn;
    
    public Terminator(PlayerColor color, PlayerBoard playerBoard) {
        super(Game.TERMINATOR_USERNAME, color, playerBoard);
    }

    public Terminator(Terminator other) {
        super(other);
    }

    public boolean isSpawnTurn() {
        return spawnTurn;
    }

    public void setSpawnTurn(boolean spawnTurn) {
        this.spawnTurn = spawnTurn;
    }
}
