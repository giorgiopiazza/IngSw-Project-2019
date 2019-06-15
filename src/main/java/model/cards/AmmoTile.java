package model.cards;

import enumerations.Ammo;
import exceptions.player.MaxCardsInHandException;
import exceptions.player.NegativeQuantityException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.UserPlayer;

import java.util.Objects;

public class AmmoTile extends Card {
    private static final long serialVersionUID = -1145130365479937145L;

    private final AmmoQuantity ammoOnTile;
    private final boolean pickPowerup;

    public AmmoTile(String imagePath, AmmoQuantity ammoOnTile, boolean pickPowerup) {
        super(imagePath);
        this.ammoOnTile = ammoOnTile;
        this.pickPowerup = pickPowerup;
    }

    AmmoQuantity getAmmoOnTile() {
        return this.ammoOnTile;
    }

    boolean isPickPowerup() {
        return this.pickPowerup;
    }

    public void giveResources(UserPlayer pickingPlayer) {
        if (pickingPlayer == null) throw new NullPointerException("Player can not be null");

        addBlueAmmo(pickingPlayer);
        addRedAmmo(pickingPlayer);
        addYellowAmmo(pickingPlayer);

        if (pickPowerup) {
            try {
                pickingPlayer.addPowerup((PowerupCard) Game.getInstance().getPowerupCardsDeck().draw());
            } catch (MaxCardsInHandException e) {
                // if a player has already 3 powerups in his hand if his pick action goes on a CardSquare he ignores the powerup
            }
        }
    }

    private void addBlueAmmo(UserPlayer pickingPlayer) {
        if (ammoOnTile.getBlueAmmo() != 0) {
            if (ammoOnTile.getBlueAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getBlueAmmo();
            for (int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.BLUE;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }
    }

    private void addRedAmmo(UserPlayer pickingPlayer) {
        if (ammoOnTile.getRedAmmo() != 0) {
            if (ammoOnTile.getRedAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getRedAmmo();
            for (int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.RED;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }
    }

    private void addYellowAmmo(UserPlayer pickingPlayer) {
        if (ammoOnTile.getYellowAmmo() != 0) {
            if (ammoOnTile.getYellowAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getYellowAmmo();
            for (int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.YELLOW;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmmoTile ammoTile = (AmmoTile) o;
        if (!Objects.equals(this.imagePath, ammoTile.imagePath)) return false;
        return pickPowerup == ammoTile.pickPowerup &&
                Objects.equals(ammoOnTile, ammoTile.ammoOnTile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ammoOnTile, pickPowerup);
    }

    @Override
    public String toString() {
        return "AmmoTile{" +
                "ammoOnTile=" + ammoOnTile +
                ", pickPowerup=" + pickPowerup +
                '}';
    }
}
