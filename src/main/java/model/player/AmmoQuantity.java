package model.player;

import exceptions.playerboard.NotEnoughAmmoException;

import java.util.Objects;

public class AmmoQuantity {
    int redAmmo;
    int blueAmmo;
    int yellowAmmo;

    public AmmoQuantity(int redAmmo, int blueAmmo, int yellowAmmo) {
        this.redAmmo = redAmmo;
        this.blueAmmo = blueAmmo;
        this.yellowAmmo = yellowAmmo;
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
}
