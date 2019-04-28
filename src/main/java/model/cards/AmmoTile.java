package model.cards;

import enumerations.Ammo;
import exceptions.player.NegativeQuantityException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.UserPlayer;

import java.io.File;
import java.util.Objects;

public class AmmoTile extends Card {

    private final AmmoQuantity ammoOnTile;
    private final boolean pickPowerup;

    public AmmoTile(File image, AmmoQuantity ammoOnTile, boolean pickPowerup) {
        super(image);
        this.ammoOnTile = ammoOnTile;
        this.pickPowerup = pickPowerup;
    }

    public AmmoQuantity getAmmoOnTile() {
        return this.ammoOnTile;
    }

    public boolean isPickPowerup() {
        return this.pickPowerup;
    }

    public void giveResources(UserPlayer pickingPlayer) {
        if (pickingPlayer == null) throw new NullPointerException("Player can not be null");

        if (ammoOnTile.getBlueAmmo() != 0) {
            if (ammoOnTile.getBlueAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getBlueAmmo();
            for (int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.BLUE;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }

        if (ammoOnTile.getRedAmmo() != 0) {
            if (ammoOnTile.getRedAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getRedAmmo();
            for (int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.RED;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }

        if (ammoOnTile.getYellowAmmo() != 0) {
            if (ammoOnTile.getYellowAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getYellowAmmo();
            for (int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.YELLOW;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }

        if (pickPowerup && (pickingPlayer.getPowerups().length < 3)) {
            pickingPlayer.addPowerup((PowerupCard) Game.getInstance().getPowerupCardsDeck().draw());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AmmoTile ammoTile = (AmmoTile) o;
        return pickPowerup == ammoTile.pickPowerup &&
                Objects.equals(ammoOnTile, ammoTile.ammoOnTile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ammoOnTile, pickPowerup);
    }
}
