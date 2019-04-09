package model.cards;

import enumerations.Ammo;
import model.Game;
import model.player.UserPlayer;

import java.io.File;
import java.util.List;

public class AmmoTile extends Card {

    private final List<Ammo> ammoOnTile;
    private final boolean pickPowerup;

    public AmmoTile(File image, List<Ammo> ammoOnTile, boolean pickPowerup) {
        super(image);
        this.ammoOnTile = ammoOnTile;
        this.pickPowerup = pickPowerup;
    }

    public List<Ammo> getAmmoOnTile() {
        return this.ammoOnTile;
    }

    public boolean isPickPowerup() {
        return this.pickPowerup;
    }

    public void giveResources(UserPlayer pickingPlayer) {
        if (pickingPlayer == null) throw new NullPointerException("Player can not be null");

        for (Ammo ammo : ammoOnTile) {
            pickingPlayer.getPlayerBoard().addAmmo(ammo);
        }

        if (pickPowerup && (pickingPlayer.getPowerups().length < 3)) {
            pickingPlayer.addPowerup((PowerupCard) Game.getInstance().getPowerupCardsDeck().draw());
        }
    }
}
