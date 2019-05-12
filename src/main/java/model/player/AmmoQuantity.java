package model.player;

import enumerations.Ammo;
import exceptions.playerboard.NotEnoughAmmoException;

import java.util.*;

public class AmmoQuantity {
    private int redAmmo;
    private int blueAmmo;
    private int yellowAmmo;

    public AmmoQuantity() {
        redAmmo = 0;
        blueAmmo = 0;
        yellowAmmo = 0;
    }

    public AmmoQuantity(int redAmmo, int blueAmmo, int yellowAmmo) {
        this.redAmmo = redAmmo;
        this.blueAmmo = blueAmmo;
        this.yellowAmmo = yellowAmmo;
    }

    public AmmoQuantity(Ammo[] ammo) {
        List<Ammo> ammoList = new ArrayList<>(Arrays.asList(ammo));
        redAmmo = (Collections.frequency(ammoList, Ammo.RED) < 3) ? Collections.frequency(ammoList, Ammo.RED) : 3;
        blueAmmo = (Collections.frequency(ammoList, Ammo.BLUE) < 3) ? Collections.frequency(ammoList, Ammo.BLUE) : 3;
        yellowAmmo = (Collections.frequency(ammoList, Ammo.YELLOW) < 3) ? Collections.frequency(ammoList, Ammo.YELLOW) : 3;
    }

    public AmmoQuantity(AmmoQuantity other) {
        this.redAmmo = other.redAmmo;
        this.blueAmmo = other.blueAmmo;
        this.yellowAmmo = other.yellowAmmo;
    }

    public void addAmmo(Ammo ammo) {
        switch (ammo) {
            case RED:
                addRedAmmo();
                break;
            case BLUE:
                addBlueAmmo();
                break;
            default:
                addYellowAmmo();
        }
    }

    public int getRedAmmo() {
        return redAmmo;
    }

    public void addRedAmmo() {
        if (redAmmo < 3) {
            redAmmo++;
        }
    }

    public int getBlueAmmo() {
        return blueAmmo;
    }

    public void addBlueAmmo() {
        if (blueAmmo < 3) {
            blueAmmo++;
        }
    }

    public int getYellowAmmo() {
        return yellowAmmo;
    }

    public void addYellowAmmo() {
        if (yellowAmmo < 3) {
            yellowAmmo++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmmoQuantity that = (AmmoQuantity) o;
        return redAmmo == that.redAmmo &&
                blueAmmo == that.blueAmmo &&
                yellowAmmo == that.yellowAmmo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(redAmmo, blueAmmo, yellowAmmo);
    }

    public AmmoQuantity difference(AmmoQuantity ammoQuantity) throws NotEnoughAmmoException {
        if (ammoQuantity == null) {
            throw new NullPointerException();
        }

        int diffRed = this.redAmmo - ammoQuantity.getRedAmmo();
        int diffBlue = this.blueAmmo - ammoQuantity.getBlueAmmo();
        int diffYellow = this.yellowAmmo - ammoQuantity.getYellowAmmo();

        if (diffRed >= 0 && diffBlue >= 0 && diffYellow >= 0) {
            return new AmmoQuantity(diffRed, diffBlue, diffYellow);
        } else {
            throw new NotEnoughAmmoException();
        }
    }

    public AmmoQuantity sum(AmmoQuantity ammoQuantity) {
        if (ammoQuantity == null) {
            throw new NullPointerException();
        }

        int sumRed = (this.redAmmo + ammoQuantity.getRedAmmo() < 3) ? this.redAmmo + ammoQuantity.getRedAmmo() : 3;
        int sumBlue = (this.blueAmmo + ammoQuantity.getBlueAmmo() < 3) ? this.blueAmmo + ammoQuantity.getBlueAmmo() : 3;
        int sumYellow = (this.yellowAmmo + ammoQuantity.getYellowAmmo() < 3) ? this.yellowAmmo + ammoQuantity.getYellowAmmo() : 3;

        return new AmmoQuantity(sumRed, sumBlue, sumYellow);
    }

    @Override
    public String toString() {
        return "AmmoQt{" +
                "redAmmo=" + redAmmo +
                ", blueAmmo=" + blueAmmo +
                ", yellowAmmo=" + yellowAmmo +
                '}';
    }
}
