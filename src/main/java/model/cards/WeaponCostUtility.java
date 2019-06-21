package model.cards;

import enumerations.Ammo;
import exceptions.command.InvalidCommandException;
import exceptions.player.EmptyHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.UserPlayer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeaponCostUtility {
    private WeaponCostUtility() {
        throw new IllegalStateException("Utility class");
    }

    public static void payCost(String username, List<Integer> paymentsPowerups, AmmoQuantity cost) throws NotEnoughAmmoException {
        UserPlayer shootingPlayer = Game.getInstance().getUserPlayerByUsername(username);
        PowerupCard[] powerupCards = shootingPlayer.getPowerups();

        List<Integer> usedPowerups = new ArrayList<>();

        AmmoQuantity costWithoutPowerups = getCostWithoutPowerup(cost, paymentsPowerups, usedPowerups, powerupCards);
        shootingPlayer.getPlayerBoard().useAmmo(costWithoutPowerups);

        if (!usedPowerups.isEmpty()) {
            usedPowerups.sort(Collections.reverseOrder());

            try {
                for (Integer powID : usedPowerups) {
                    shootingPlayer.discardPowerupByIndex(powID);
                }
            } catch (EmptyHandException e) {
                throw new InvalidCommandException();
            }
        }
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private static AmmoQuantity getCostWithoutPowerup(AmmoQuantity cost, List<Integer> powerups, List<Integer> usedPowerups, PowerupCard[] powerupCards) {
        int redCost = cost.getRedAmmo();
        int blueCost = cost.getBlueAmmo();
        int yellowCost = cost.getYellowAmmo();

        if (powerups.isEmpty()) {
            return new AmmoQuantity(redCost, blueCost, yellowCost);
        }

        try {
            for (Integer i : powerups) {
                Ammo ammo = powerupCards[i].getValue();

                switch (ammo) {
                    case RED:
                        if (redCost > 0) {
                            redCost--;
                            usedPowerups.add(i);
                        }
                        break;
                    case BLUE:
                        if (blueCost > 0) {
                            blueCost--;
                            usedPowerups.add(i);
                        }
                        break;
                    default:
                        if (yellowCost > 0) {
                            yellowCost--;
                            usedPowerups.add(i);
                        }
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            throw new InvalidCommandException();
        }


        return new AmmoQuantity(redCost, blueCost, yellowCost);
    }
}
