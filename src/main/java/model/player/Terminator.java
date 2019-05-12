package model.player;

import enumerations.Color;

public class Terminator extends Player {
    boolean spawnTurn;
    public Terminator(Color color, PlayerBoard playerBoard) {
        super("Terminator", color, playerBoard);
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
