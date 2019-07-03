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

/**
 * This is an Utility class used to manage the payment of a {@link WeaponCard Weapon} considering that we can pay both
 *                  (i) her cost
 *                  (ii) the cost of her effects
 */
public class WeaponCostUtility {
    private WeaponCostUtility() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Pays the cost of a Weapon, used by a shooting player, considering that he can pay it with powerups,
     * represented as indexes in a List
     *
     * @param username the player using the weapon
     * @param paymentsPowerups the List of the payment powerups
     * @param cost the cost to be payed of the ammo
     * @throws NotEnoughAmmoException in case the shooter has not enough ammo
     */
    public static void payCost(String username, List<Integer> paymentsPowerups, AmmoQuantity cost) throws NotEnoughAmmoException {
        UserPlayer shootingPlayer = (UserPlayer)Game.getInstance().getUserPlayerByUsername(username);
        PowerupCard[] powerupCards = shootingPlayer.getPowerups();

        List<Integer> usedPowerups = new ArrayList<>();

        AmmoQuantity costWithoutPowerups = getCostWithoutPowerup(cost, paymentsPowerups, usedPowerups, powerupCards);
        shootingPlayer.getPlayerBoard().useAmmo(costWithoutPowerups);

        if (!usedPowerups.isEmpty() && powerupCards.length != 0) {
            usedPowerups.sort(Collections.reverseOrder());

            try {
                for (Integer powID : usedPowerups) {
                    PowerupCard payingPowerup = shootingPlayer.getPowerups()[powID];
                    shootingPlayer.discardPowerupByIndex(powID);
                    Game.getInstance().getPowerupCardsDeck().discardCard(payingPowerup);
                }
            } catch (EmptyHandException e) {
                throw new InvalidCommandException();
            }
        }
    }

    /**
     * Utility method used to pay the cost of a weapon.
     * It returns the cost of the weapon removing the ammo already payed with a powerup
     *
     * @param cost the complete cost to be payed
     * @param powerups the List containing the indexes of the paying powerups
     * @param usedPowerups the List containing the indexes of the powerups used to pay the cost
     * @param powerupCards array of {@link PowerupCard PowerupCards} owned by the paying player
     * @return
     */
    @NotNull
    @Contract("_, _, _, _ -> new")
    private static AmmoQuantity getCostWithoutPowerup(AmmoQuantity cost, List<Integer> powerups, List<Integer> usedPowerups, PowerupCard[] powerupCards) {
        int redCost = cost.getRedAmmo();
        int blueCost = cost.getBlueAmmo();
        int yellowCost = cost.getYellowAmmo();

        if (powerups.isEmpty() || powerupCards.length == 0) {
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
