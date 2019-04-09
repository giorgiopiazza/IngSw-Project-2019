package model.player;

import enumerations.Ammo;
import exceptions.playerboard.BoardAlreadyFlippedException;
import exceptions.playerboard.BoardFlipDamagedException;
import exceptions.playerboard.BoardMaxAmmoException;
import exceptions.playerboard.NotEnoughAmmoException;

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
    /**
     * If boardFlipped is true the board is flipped
     * and so there isn't the first blood point
     */
    private boolean boardFlipped;

    /**
     * Initialize the PlayerBoard to a not flipped player board
     */
    public PlayerBoard() {
        damages = new ArrayList<>();
        marks = new ArrayList<>();

        skulls = 0;
        ammo = new ArrayList<>();

        boardFlipped = false;
        boardPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
    }

    /**
     * @return number of skull placed on the player board
     */
    public int getSkulls() {
        return skulls;
    }

    /**
     * @return <code>true</code> if the board is flipped, <code>false</code> otherwise
     */
    public boolean isBoardFlipped() {
        return boardFlipped;
    }

    /**
     * Return an array of Integer made from a sublist of the board points.
     * The sublist is the board points list without the first N elements.
     * N is equals to the number of skull placed on the board.
     *
     * @return an array of the board points
     */
    public Integer[] getBoardPoints() {
        return boardPoints.subList(skulls, boardPoints.size()).toArray(new Integer[0]);
    }

    /**
     * @return the count of damage suffered
     */
    public int getDamageCount() {
        return damages.size();
    }

    /**
     * @return the count of mark suffered
     */
    public int getMarkCount() {
        return marks.size();
    }

    /**
     * Tries to flip the player board changing the player board points and removing all the skulls on it
     *
     * @throws BoardAlreadyFlippedException if the player board is already flipped
     * @throws BoardFlipDamagedException    if there is damage on the player board
     */
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

    /**
     * Adds an ammo to player board
     *
     * @param ammo to add to player board
     * @throws BoardMaxAmmoException when there are already three of passed ammo
     */
    public void addAmmo(Ammo ammo) throws BoardMaxAmmoException {
        if (Collections.frequency(this.ammo, ammo) == 3) {
            throw new BoardMaxAmmoException();
        }

        this.ammo.add(ammo);
    }

    /**
     * Checks if there are enough ammo to afford the cost
     *
     * @param cost of the operation
     * @return <code>true</code> if there are enough ammo, <code>false</code> otherwise
     */
    private boolean hasEnoughAmmo(List<Ammo> cost) {
        return Collections.frequency(cost, Ammo.BLUE) <= Collections.frequency(ammo, Ammo.BLUE) &&
                Collections.frequency(cost, Ammo.YELLOW) <= Collections.frequency(ammo, Ammo.YELLOW) &&
                Collections.frequency(cost, Ammo.RED) <= Collections.frequency(ammo, Ammo.RED);
    }

    /**
     * Pays the cost of ammo
     *
     * @param cost of operation
     * @throws NotEnoughAmmoException if there aren't enough ammo to pay the operation
     */
    public void useAmmo(List<Ammo> cost) throws NotEnoughAmmoException {
        if (!hasEnoughAmmo(cost)) {
            throw new NotEnoughAmmoException();
        }

        int red = Collections.frequency(cost, Ammo.RED);
        int blue = Collections.frequency(cost, Ammo.BLUE);
        int yellow = Collections.frequency(cost, Ammo.YELLOW);

        for (int i = ammo.size() - 1; i >= 0; --i) {
            Ammo tempAmmo = ammo.get(i);

            switch (tempAmmo) {
                case RED:
                    if (red > 0) {
                        ammo.remove(i);
                        --red;
                    }
                    break;
                case BLUE:
                    if (blue > 0) {
                        ammo.remove(i);
                        --blue;
                    }
                    break;
                default:
                    if (yellow > 0) {
                        ammo.remove(i);
                        --yellow;
                    }
            }
        }
    }

    /**
     * @return an array of player boards ammo
     */
    public Ammo[] getAmmo() {
        return ammo.toArray(new Ammo[0]);
    }

    /**
     * Adds marks on the player board
     *
     * @param markDealer player who inflicted the mark
     * @param marksCount number of marks inflicted
     */
    public void addMark(Player markDealer, int marksCount) {
        for (int i = 0; i < marksCount; i++) {
            marks.add(markDealer);
        }
    }

    /**
     * Adds marks on the player board
     *
     * @param damageDealer player who inflicted the damage
     * @param damageCount  number of damages inflicted
     */
    public void addDamage(Player damageDealer, int damageCount) {
        int marksNum = Collections.frequency(marks, damageDealer);

        if (marksNum > 0) {
            marks.removeIf(damageDealer::equals);
        }

        for (int i = 0; i < damageCount + marksNum && damages.size() < 12; ++i) {
            damages.add(damageDealer);
        }
    }

    /**
     * Modify the player board on death.
     * Keeps everything except the damages list and adds a skull on the player board
     */
    public void onDeath() {
        damages.clear();
        skulls++;
    }
}
