package model.cards;

import enumerations.Ammo;
import exceptions.cards.InvalidPowerupActionException;
import exceptions.command.InvalidCommandException;
import exceptions.player.EmptyHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.Effect;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import network.message.EffectRequest;
import network.message.PowerupRequest;

import java.util.List;
import java.util.Objects;

public class PowerupCard extends UsableCard {
    private static final long serialVersionUID = -8499317938860478314L;

    private final Ammo value;

    public PowerupCard(String name, String imagePath, Ammo value, Effect baseEffect) {
        super(name, imagePath, baseEffect);
        this.value = value;
    }

    public Ammo getValue() {
        return this.value;
    }

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

    private void payEffectCost(PowerupRequest request, UserPlayer shootingPlayer, AmmoQuantity cost) throws NotEnoughAmmoException {
        if (!cost.noAmmo()) {
            PowerupCard[] powerupCards = shootingPlayer.getPowerups();
            List<Integer> powerupsID = request.getPaymentPowerups();
            Ammo colorCost = null;

            if(!request.getAmmoColor().isEmpty()) {
                colorCost = request.getAmmoColor().get(0);
            }

            boolean paid = false;

            for (Integer i : powerupsID) {
                if (powerupCards[i].getValue() == colorCost) {
                    try {
                        shootingPlayer.discardPowerupByIndex(i);
                    } catch (EmptyHandException e) {
                        throw new InvalidCommandException();
                    }

                    paid = true;
                    break;
                }
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
        return value == that.value;
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
