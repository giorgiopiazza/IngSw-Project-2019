package model.cards;

import enumerations.Ammo;
import exceptions.player.NegativeQuantityException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.UserPlayer;

import java.io.File;

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

        if(ammoOnTile.getBlueAmmo() != 0) {
            if(ammoOnTile.getBlueAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getBlueAmmo();
            for(int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.BLUE;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }

        if(ammoOnTile.getRedAmmo() != 0) {
            if(ammoOnTile.getRedAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getRedAmmo();
            for(int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.RED;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }

        if(ammoOnTile.getYellowAmmo() != 0) {
            if(ammoOnTile.getYellowAmmo() < 0) throw new NegativeQuantityException();
            int tempAmmoCounter = ammoOnTile.getYellowAmmo();
            for(int i = 0; i < tempAmmoCounter; ++i) {
                Ammo tempAmmo = Ammo.YELLOW;
                pickingPlayer.getPlayerBoard().addAmmo(tempAmmo);
            }
        }

        if (pickPowerup && (pickingPlayer.getPowerups().length < 3)) {
            pickingPlayer.addPowerup((PowerupCard) Game.getInstance().getPowerupCardsDeck().draw());
        }
    }
}
