package model.cards;

import enumerations.Ammo;
import exceptions.player.MaxCardsInHandException;
import exceptions.player.NegativeQuantityException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.UserPlayer;

import java.util.Objects;

/**
 * This class represents an ammo tile. An ammo tile can contain:
 *          (i) three {@link Ammo ammos}
 *          (ii) two {@link Ammo ammos} and one {@link PowerupCard Powerup}
 */
public class AmmoTile extends Card {
    private static final long serialVersionUID = -1145130365479937145L;

    private final AmmoQuantity ammoOnTile;
    private final boolean pickPowerup;

    /**
     * Creates an AmmoTile.
     * A boolean is used to know if if contains a powerup card
     *
     * @param imagePath the image of the AmmoTile
     * @param ammoOnTile the quantity of ammo on the tile
     * @param pickPowerup {@code true} if the AmmoTile contains a Powerup, otherwise {@code false}
     */
    public AmmoTile(String imagePath, AmmoQuantity ammoOnTile, boolean pickPowerup) {
        super(imagePath);
        this.ammoOnTile = ammoOnTile;
        this.pickPowerup = pickPowerup;
    }

    /**
     * @return the {@link Ammo Ammo} on the tile
     */
    AmmoQuantity getAmmoOnTile() {
        return this.ammoOnTile;
    }

    /**
     * @return {@code true} if the AmmoTile contains a Powerup, otherwise {@code false}
     */
    boolean isPickPowerup() {
        return this.pickPowerup;
    }

    /**
     * Grants the resources to the {@link UserPlayer Player} picking the AmmoTile
     *
     * @param pickingPlayer the {@link UserPlayer Player} picking the AmmoTile
     */
    public void giveResources(UserPlayer pickingPlayer) {
        if (pickingPlayer == null) throw new NullPointerException("Player can not be null");

        addBlueAmmo(pickingPlayer);
        addRedAmmo(pickingPlayer);
        addYellowAmmo(pickingPlayer);

        if (pickPowerup) {
            PowerupCard drawnPowerup = (PowerupCard) Game.getInstance().getPowerupCardsDeck().draw();

            // if there are no more powerups I need to build a new deck with already used ones, flush it and then draw
            if(drawnPowerup == null) {
                Game.getInstance().getPowerupCardsDeck().flush();
                drawnPowerup = (PowerupCard) Game.getInstance().getPowerupCardsDeck().draw();
            }

            try {
                pickingPlayer.addPowerup(drawnPowerup);
            } catch (MaxCardsInHandException e) {
                // if a player has already 3 powerups in his hand if his pick action goes on a CardSquare he ignores the powerup
                // and so it is not picked from the deck
                Game.getInstance().getPowerupCardsDeck().addCard(drawnPowerup);
            }
        }
    }

    /**
     * Grants blue ammo to the picking player
     *
     * @param pickingPlayer the picking player
     */
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

    /**
     * Grants red ammo to the picking player
     *
     * @param pickingPlayer the picking player
     */
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

    /**
     * Grants yellow ammo to the picking player
     *
     * @param pickingPlayer the picking player
     */
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
