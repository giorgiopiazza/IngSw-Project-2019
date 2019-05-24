package model.map;

import enumerations.RoomColor;
import enumerations.SquareAdjacency;
import enumerations.SquareType;
import exceptions.map.MaxSquareWeaponsException;
import exceptions.map.MissingWeaponOnSquareException;
import model.cards.WeaponCard;

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
            RoomColor color,
            SquareAdjacency north,
            SquareAdjacency east,
            SquareAdjacency south,
            SquareAdjacency west) {
        super(color, north, east, south, west, SquareType.SPAWN);
        weapons = new WeaponCard[MAX_WEAPONS];
    }

    /**
     * Inserts the <code>weapon</code> card in the first empty space it finds inside the weapons array
     *
     * @param weapon the weapon card to insert
     * @throws MaxSquareWeaponsException if the array already contains 3 cards
     */
    public void addWeapon(WeaponCard weapon) {
        for (int i = 0; i < MAX_WEAPONS; i++) {
            if (weapons[i] == null) {
                weapons[i] = weapon;
            }
        }

        throw new MaxSquareWeaponsException();
    }

    /**
     * Method that swaps two weapons on a spawn square
     *
     * @param toSwap the weapon to be added on the spawn square
     * @param toPick the weapon to picked
     */
    public void swapWeapons(WeaponCard toSwap, WeaponCard toPick) {
        if(toSwap == null || toPick == null) {
            throw new NullPointerException("You can not swap a null weapon");
        }
        int removedIndex = removeWeapon(toPick);
        weapons[removedIndex] = toSwap;
    }

    /**
     * Method to verify if the spawn square has the specified weapon
     *
     * @param weaponCard the weapon to verify if present
     * @return true if the spawn square contains the weapon, otherwise false
     */
    public boolean hasWeapon(WeaponCard weaponCard) {
        for(int i = 0; i < MAX_WEAPONS; ++i) {
            if(weaponCard.equals(weapons[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method that removes a weapon from the spawn square returning her index
     *
     * @param weapon the WeaponCard to be removed from the spawn square
     * @return the index of the weapon removed
     */
    public int removeWeapon(WeaponCard weapon) {
        if (weapon == null) throw new NullPointerException("weapon cannot be null");

        if(hasWeapon(weapon)) {
            for (int i = 0; i < MAX_WEAPONS; i++) {
                if (weapons[i] != null && weapons[i].equals(weapon)) {
                    weapons[i] = null;
                    return i;
                }
            }
        }

        throw new MissingWeaponOnSquareException(weapon);
    }

    public WeaponCard[] getWeapons() {
        return weapons;
    }
}
