package model.player;

import enumerations.PossibleState;

public class PlayerState {
    private PossibleState state;

    public PlayerState(PossibleState state) {
        this.state = state;
    }

    public PossibleState getState() {
        return this.state;
    }
}
