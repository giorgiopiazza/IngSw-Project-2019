package model.actions;

import exceptions.actions.InvalidActionException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.cards.WeaponCard;
import model.cards.WeaponCostUtility;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import network.message.ReloadRequest;
import utility.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the Reload Action that is always executed at the end of the turn of
 * a {@link UserPlayer UserPlayer} when the game's state is NORMAL.
 * In fact in FINAL_FRENZY, Reload Actions are mixed with other shoot actions
 */
public class ReloadAction implements Action {
    private UserPlayer actingPlayer;
    private ReloadRequest rechargeRequest;
    private List<Integer> rechargingWeaponsIndexes;
    private List<Integer> powerupIndexes;

    /**
     * Builds a Reload Action that executes the reload during the game.
     * Care, this action is also used by the {@link ShootAction ShootAction}
     * in case a FRENZY shoot has one or more recharging weapons in his request
     *
     * @param actingPlayer the Reloading Acting Player
     * @param rechargeRequest the {@link ReloadRequest ReloadRequest} received
     */
    public ReloadAction(UserPlayer actingPlayer, ReloadRequest rechargeRequest) {
        this.actingPlayer = actingPlayer;
        this.rechargeRequest = rechargeRequest;
        this.rechargingWeaponsIndexes = rechargeRequest.getWeapons().stream().distinct().collect(Collectors.toList());
        this.powerupIndexes = rechargeRequest.getPaymentPowerups();
    }

    /**
     * Validates the Reload Action.
     * The reload validation considers mostly correct Inputs as the real validation is
     * catched in the exceptions in the execution method
     *
     * @return true if the Reload Action has valid properties as described
     * @throws InvalidActionException in case the action is invalid due to input validation
     */
    @Override
    public boolean validate() throws InvalidActionException {
        if(!InputValidator.validateIndexes(rechargeRequest, actingPlayer)) {
            throw new InvalidActionException();
        }

        return !(rechargingWeaponsIndexes == null || rechargingWeaponsIndexes.isEmpty());
    }

    /**
     * Executes the Reload action every time it is called. This means both during a NORMAL reload
     * action, but also in a FRENZY shoot one!
     *
     * @throws WeaponAlreadyChargedException in case the Weapon is already charged
     * @throws NotEnoughAmmoException in case the Acting Player has not enough ammo to reload the weapons
     */
    @Override
    public void execute() throws WeaponAlreadyChargedException, NotEnoughAmmoException {
        List<WeaponCard> weaponCards = new ArrayList<>();

        for (Integer weaponIndex : rechargingWeaponsIndexes) {
            weaponCards.add(actingPlayer.getWeapons()[weaponIndex]);
        }

        if (weaponCards.stream().anyMatch(w -> !w.isRechargeable())) {
            throw new WeaponAlreadyChargedException();
        }

        payWeaponCosts();

        for (WeaponCard weaponCard : weaponCards) {
            weaponCard.recharge();
        }
    }

    /**
     * Utility method used to pay the effect of a Weapon inside this Action
     *
     * @throws NotEnoughAmmoException in case the Acting Player has not enough ammo to reload the weapons
     */
    private void payWeaponCosts() throws NotEnoughAmmoException {
        int redCost = 0;
        int blueCost = 0;
        int yellowCost = 0;

        for (Integer weaponIndex : rechargingWeaponsIndexes) {
            AmmoQuantity recharginCost = actingPlayer.getWeapons()[weaponIndex].getRechargeCost();

            redCost += recharginCost.getRedAmmo();
            blueCost += recharginCost.getBlueAmmo();
            yellowCost += recharginCost.getYellowAmmo();
        }

        WeaponCostUtility.payCost(actingPlayer.getUsername(), powerupIndexes, new AmmoQuantity(redCost, blueCost, yellowCost));
    }
}
