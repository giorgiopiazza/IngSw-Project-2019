package model.player;

import enumerations.Ammo;
import enumerations.PlayerBoardState;
import exceptions.playerboard.BoardAlreadyFlippedException;
import exceptions.playerboard.BoardFlipDamagedException;
import exceptions.playerboard.InvalidDamageException;
import exceptions.playerboard.NotEnoughAmmoException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerBoard implements Serializable {
    private static final long serialVersionUID = 696570674587022548L;

    private List<String> damages;
    private List<String> marks;

    private int skulls;
    private AmmoQuantity ammo;
    private PlayerBoardState boardState;

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
        ammo = new AmmoQuantity(1, 1, 1);

        boardState = PlayerBoardState.NORMAL;
        boardFlipped = false;
        boardPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
    }

    public PlayerBoard(PlayerBoard other) {
        this.damages = new ArrayList<>(other.damages);
        this.marks = new ArrayList<>(other.marks);
        this.skulls = other.skulls;
        this.ammo = new AmmoQuantity(other.ammo);
        this.boardState = other.boardState;
        this.boardPoints = new ArrayList<>(other.boardPoints);
        this.boardFlipped = other.boardFlipped;
    }

    /**
     * @return number of skull placed on the player board
     */
    public int getSkulls() {
        return skulls;
    }

    /**
     * @return {@code true}if the board is flipped, {@code false} otherwise
     */
    public boolean isBoardFlipped() {
        return boardFlipped;
    }

    /**
     * @return the state of the playerboard
     */
    public PlayerBoardState getBoardState() {
        return this.boardState;
    }

    /**
     * Method that sets the state of the playerBoard related to how many damages he has received
     */
    public void setBoardState() {
        int currentDamages = getDamageCount();
        if(currentDamages < 3) {
            this.boardState = PlayerBoardState.NORMAL;
        } else if(currentDamages < 6) {
            this.boardState = PlayerBoardState.FIRST_ADRENALINE;
        } else if(currentDamages < 13) {
            this.boardState = PlayerBoardState.SECOND_ADRENALINE;
        } else {
            throw new InvalidDamageException();
        }

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
     * @throws NullPointerException when ammo is null
     */
    public void addAmmo(Ammo ammo) {
        if (ammo == null) {
            throw new NullPointerException("Ammo cannot be null");
        }

        switch (ammo) {
            case RED:
                this.ammo.addRedAmmo();
                break;
            case BLUE:
                this.ammo.addBlueAmmo();
                break;
            default:
                this.ammo.addYellowAmmo();
        }
    }

    /**
     * Pays the cost of ammo
     *
     * @param cost of operation
     * @throws NotEnoughAmmoException if there aren't enough ammo to pay the operation
     */
    public void useAmmo(AmmoQuantity cost) throws NotEnoughAmmoException {
        ammo = ammo.difference(cost);
    }

    /**
     * @return an array of player boards ammo
     */
    public AmmoQuantity getAmmo() {
        return ammo;
    }

    public List<String> getDamages() {
        return damages;
    }

    public List<String> getMarks() {
        return marks;
    }

    /**
     * Adds marks on the player board
     *
     * @param markDealer player who inflicted the mark
     * @param marksCount number of marks inflicted
     */
    public void addMark(Player markDealer, int marksCount) {
        if (markDealer == null) {
            throw new NullPointerException("Player cannot be null");
        }

        for (int i = 0; i < marksCount; i++) {
            marks.add(markDealer.getUsername());
        }
    }

    /**
     * Adds damages on the player board
     *
     * @param damageDealer player who inflicted the damage
     * @param damageCount  number of damages inflicted
     */
    public void addDamage(Player damageDealer, int damageCount) {
        if (damageDealer == null) {
            throw new NullPointerException("Player cannot be null");
        }

        int marksNum = Collections.frequency(marks, damageDealer.getUsername());

        if (damageCount > 0) {
            if (marksNum > 0) {
                marks.removeIf(damageDealer.getUsername()::equals);
            }

            for (int i = 0; i < damageCount + marksNum && damages.size() < 12; ++i) {
                damages.add(damageDealer.getUsername());
            }
        }

        setBoardState();
    }

    /**
     * Modify the player board on death.
     * Keeps everything except the damages list and adds a skull on the player board
     */
    public void onDeath() {
        damages.clear();
        skulls++;
    }

    @Override
    public String toString() {
        return "PlayerBoard{" +
                "damages=" + Arrays.toString(damages.toArray()) +
                ", marks=" + Arrays.toString(marks.toArray()) +
                ", skulls=" + skulls +
                ", ammo=" + ammo +
                ", boardPoints=" + Arrays.toString(boardPoints.toArray()) +
                ", boardFlipped=" + boardFlipped +
                '}';
    }
}
