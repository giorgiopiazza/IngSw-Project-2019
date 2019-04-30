package model.cards;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import exceptions.command.InvalidCommandException;
import exceptions.player.EmptyHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.Effect;
import model.cards.effects.PowerupBaseEffect;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import utility.CommandUtility;

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
    public void use(String command) throws AdrenalinaException {
        String[] splitCommand = command.split(" ");
        int pId = CommandUtility.getCommandUserID(splitCommand);

        if (pId >= Game.getInstance().playersNumber()) {
            throw new InvalidCommandException();
        }

        UserPlayer shootingPlayer = Game.getInstance().getPlayerByID(pId);

        if (getBaseEffect().validate(command)) {
            payEffectCost(command, shootingPlayer, ((PowerupBaseEffect) getBaseEffect()).hasCost());
            getBaseEffect().execute(command);
        } else {
            throw new InvalidCommandException();
        }
    }

    private void payEffectCost(String command, UserPlayer shootingPlayer, boolean cost) throws NotEnoughAmmoException {
        if (cost) {
            String[] splitCommand = command.split(" ");

            PowerupCard[] powerupCards = shootingPlayer.getPowerups();

            List<Integer> powerupsID = CommandUtility.getAttributesID(splitCommand, "-a");

            Ammo colorCost = CommandUtility.getPowerupPaymentAmmo(splitCommand);

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
        if (!super.equals(o)) return false;
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
