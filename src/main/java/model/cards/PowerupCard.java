package model.cards;

import enumerations.Ammo;
import exceptions.cards.InvalidPowerupActionException;
import exceptions.command.InvalidCommandException;
import exceptions.player.EmptyHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.Effect;
import model.cards.effects.PowerupBaseEffect;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import network.message.EffectRequest;
import network.message.PowerupRequest;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class PowerupCard extends UsableCard {
    private final Ammo value;

    public Ammo getValue() {
        return this.value;
    }

    public PowerupCard(String name, File image, Ammo value, Effect baseEffect) {
        super(name, image, baseEffect);
        this.value = value;
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
            payEffectCost(powerupRequest, shootingPlayer, ((PowerupBaseEffect) getBaseEffect()).hasCost());
            getBaseEffect().execute(powerupRequest);
        } else {
            throw new InvalidPowerupActionException();
        }
    }

    private void payEffectCost(PowerupRequest request, UserPlayer shootingPlayer, boolean cost) throws NotEnoughAmmoException {
        if (cost) {
            PowerupCard[] powerupCards = shootingPlayer.getPowerups();

            List<Integer> powerupsID = request.getPaymentPowerups();

            Ammo colorCost = request.getAmmoColor();

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

            if (!paid) {
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
        if (!Objects.equals(this.getImage(), that.getImage())) return false;
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
