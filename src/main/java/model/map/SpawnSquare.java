package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import exceptions.map.MaxSquareWeaponsException;
import model.cards.WeaponCard;

import java.util.Arrays;

public class SpawnSquare extends Square {
    public static final int MAX_WEAPONS = 3;
    private WeaponCard[] weapons;

    /**
     * Create an instance of a piece of <code>SpawnSquare</code> map, allocate an array of <code>
     * MAX_WEAPONS</code> items that will contain the 3 weapons card
     *
     * @param color the color of the square
     * @param north what's in the north
     * @param east  what's in the east
     * @param south what's in the south
     * @param west  what's in the west
     */
    public SpawnSquare(
            Color color,
            SquareAdjacency north,
            SquareAdjacency east,
            SquareAdjacency south,
            SquareAdjacency west) {
        super(color, north, east, south, west);
        weapons = new WeaponCard[MAX_WEAPONS];
    }

    /**
     * Inserts the <code>weapon</code> card in the first empty space it finds inside the weapons array
     *
     * @param weapon the weapon card to insert
     * @return index where the card is inserted
     * @throws MaxSquareWeaponsException if the array already contains 3 cards
     */
    public int addWeapon(WeaponCard weapon) {
        for (int i = 0; i < MAX_WEAPONS; i++) {
            if (weapons[i] == null) {
                weapons[i] = weapon;
                return i;
            }
        }

        throw new MaxSquareWeaponsException();
    }

    /**
     * Removes the <code>weapon</code> card from the <code>weapons</code> card array
     *
     * @param weapon weapon to be removed
     * @return true if the weapon is removed, otherwise false
     */
    public boolean removeWeapon(WeaponCard weapon) {
        for (int i = 0; i < MAX_WEAPONS; i++) {
            if (weapons[i].equals(weapon)) {
                weapons[i] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Remove the weapon card to the chosen <code>index</code>
     *
     * @param index the index of the card to be removed
     * @return true if removed, false if already empty
     */
    public boolean removeWeapon(int index) {
        boolean removed = weapons[index] != null;
        weapons[index] = null;

        return removed;
    }

    public WeaponCard[] getWeapons() {
        return weapons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpawnSquare)) return false;
        if (!super.equals(o)) return false;
        SpawnSquare that = (SpawnSquare) o;
        return Arrays.equals(getWeapons(), that.getWeapons());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(getWeapons());
        return result;
    }
}
