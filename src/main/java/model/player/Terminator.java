package model.player;

import enumerations.Color;

public class Terminator extends Player {

    public Terminator (Color color, PlayerPosition position, PlayerBoard playerBoard ) {
        super("Terminator", color, false, position, playerBoard);
    }
}
