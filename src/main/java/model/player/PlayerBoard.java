package model.player;

import enumerations.Ammo;
import exceptions.BoardAlreadyFlippedException;
import exceptions.BoardFlipDamagedException;
import exceptions.BoardMaxAmmoException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerBoard {
    private List<Player> damages;
    private List<Player> marks;

    private int skulls;
    private List<Ammo> ammo;

    private List<Integer> boardPoints;
    private boolean boardFlipped;

    public PlayerBoard() {
        damages = new ArrayList<>();
        marks = new ArrayList<>();

        skulls = 0;
        ammo = new ArrayList<>();

        boardFlipped = false; // If false there is the 'first blood' point
        boardPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
    }

    public int getSkulls() {
        return skulls;
    }

    public boolean isBoardFlipped() {
        return boardFlipped;
    }

    public Integer[] getBoardPoints() {
        return boardPoints.toArray(new Integer[0]);
    }

    public void flipBoard() throws BoardAlreadyFlippedException, BoardFlipDamagedException {
        if (boardFlipped) {
            throw new BoardAlreadyFlippedException();
        }

        if (!damages.isEmpty()) {
            throw new BoardFlipDamagedException();
        }

        boardFlipped = true;
        boardPoints = new ArrayList<>(Arrays.asList(2, 1, 1, 1));
        skulls = 0;
    }

    public void addAmmo(Ammo ammo) throws BoardMaxAmmoException {
        if (Collections.frequency(this.ammo, ammo) == 3) {
            throw new BoardMaxAmmoException();
        }

        this.ammo.add(ammo);
    }
}
