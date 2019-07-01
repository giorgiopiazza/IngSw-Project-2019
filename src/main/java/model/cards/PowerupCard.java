package model.cards;

import enumerations.Ammo;
import exceptions.cards.InvalidPowerupActionException;
import exceptions.command.InvalidCommandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.Effect;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import network.message.EffectRequest;
import network.message.PowerupRequest;

import java.util.Objects;

/**
 * This class represents a powerup.
 * A powerup needs an id to be distinguished and an Ammo that represents his value
 */
public class PowerupCard extends UsableCard {
    private static final long serialVersionUID = -8499317938860478314L;

    private final int id;
    private final Ammo value;

    /**
     * Creates a new powerup with needed informations
     *
     * @param name powerup's name
     * @param imagePath powerup's image
     * @param value powerup's value
     * @param baseEffect powerup's only effect
     * @param id poweup's id
     */
    public PowerupCard(String name, String imagePath, Ammo value, Effect baseEffect, int id) {
        super(name, imagePath, baseEffect);
        this.id = id;
        this.value = value;
    }

    /**
     * @return the value of the {@link PowerupCard PowerupCard}
     */
    public Ammo getValue() {
        return this.value;
    }

    /**
     * Executes the usage of the powerup
     *
     * @param request the {@link EffectRequest Request} received to use the powerup
     * @throws NotEnoughAmmoException in case the user has not enough ammo to use the powerup
     * @throws InvalidPowerupActionException in case the action is not valid
     */
    @Override
    public void use(EffectRequest request) throws NotEnoughAmmoException, InvalidPowerupActionException {
        PowerupRequest powerupRequest = (PowerupRequest) request;

        String username = powerupRequest.getSenderUsername();

        if (!Game.getInstance().doesPlayerExists(username)) {
            throw new InvalidCommandException();
        }

        UserPlayer shootingPlayer = Game.getInstance().getUserPlayerByUsername(username);

        if (getBaseEffect().validate(powerupRequest)) {
            payEffectCost(powerupRequest, shootingPlayer, getBaseEffect().getCost());
            getBaseEffect().execute(powerupRequest);
        } else {
            throw new InvalidPowerupActionException();
        }
    }

    /**
     * Pays the cost of the powerup. In this game only TARGETING SCOPES have a cost
     *
     * @param request the {@link EffectRequest Request} received
     * @param shootingPlayer the powerup user
     * @param cost the cost of the using powerup
     * @throws NotEnoughAmmoException in case the user has not enough ammo to pay the powerup
     */
    private void payEffectCost(PowerupRequest request, UserPlayer shootingPlayer, AmmoQuantity cost) throws NotEnoughAmmoException {
        if (!cost.noAmmo()) {
            Ammo colorCost = null;
            boolean paid = false;

            if(request.getAmmoColor() != null && !request.getAmmoColor().isEmpty()) {
                colorCost = request.getAmmoColor().get(0);
            }

            if(request.getPaymentPowerups() != null && !request.getPaymentPowerups().isEmpty()) {
                PowerupCard payingPowerup = shootingPlayer.getPowerups()[request.getPaymentPowerups().get(0)];
                Game.getInstance().getPowerupCardsDeck().discardCard(payingPowerup);
                paid = true;
            }

            if (!paid && colorCost != null) {
                AmmoQuantity ammoQuantityCost = new AmmoQuantity();
                ammoQuantityCost.addAmmo(colorCost);

                try {
                    shootingPlayer.getPlayerBoard().useAmmo(ammoQuantityCost);
                } catch (NotEnoughAmmoException e) {
                    throw new NotEnoughAmmoException();
                }

            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PowerupCard that = (PowerupCard) o;
        if (!this.getName().equals(that.getName())) return false;
        if (!Objects.equals(this.imagePath, that.imagePath)) return false;
        if(value != that.value) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return "PowerUp{" +
                "name=" + getName() +
                ", value=" + value +
                '}';
    }
}
