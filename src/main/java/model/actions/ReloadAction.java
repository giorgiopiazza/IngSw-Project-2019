package model.actions;

import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.cards.WeaponCard;
import model.cards.WeaponCostUtility;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import network.message.ReloadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReloadAction implements Action {
    private UserPlayer actingPlayer;
    private List<Integer> rechargingWeaponsIndexes;
    private List<Integer> powerupIndexes;

    public ReloadAction(UserPlayer actingPlayer, ReloadRequest rechargeRequest) {
        this.actingPlayer = actingPlayer;
        this.rechargingWeaponsIndexes = rechargeRequest.getWeapons().stream().distinct().collect(Collectors.toList());
        this.powerupIndexes = rechargeRequest.getPaymentPowerups().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public boolean validate() {
        return !(rechargingWeaponsIndexes == null || rechargingWeaponsIndexes.isEmpty());
    }

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
