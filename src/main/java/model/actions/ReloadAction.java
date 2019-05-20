package model.actions;

import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.player.UserPlayer;
import network.message.ReloadRequest;

import java.util.List;

public class ReloadAction implements Action {
    private UserPlayer actingPlayer;
    private List<Integer> rechargingWeapons;
    private ReloadRequest rechargeRequest;

    public ReloadAction(UserPlayer actingPlayer, ReloadRequest rechargeRequest) {
        this.actingPlayer = actingPlayer;
        this.rechargingWeapons = rechargeRequest.getWeapons();
        this.rechargeRequest = rechargeRequest;
    }

    @Override
    public boolean validate() {
        if(rechargingWeapons == null || rechargingWeapons.isEmpty()) {
            return true;
        }

        for (Integer weaponIndex : rechargingWeapons) {
            if (weaponIndex < 0 || weaponIndex > actingPlayer.getWeapons().length - 1 || !actingPlayer.hasWeapon(actingPlayer.getWeapons()[weaponIndex])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute() throws WeaponAlreadyChargedException, NotEnoughAmmoException {
        if(rechargingWeapons == null || rechargingWeapons.isEmpty()) {
            return;
        }

        for(Integer weaponIndex : rechargingWeapons) {
            actingPlayer.getWeapons()[weaponIndex].payRechargeCost(rechargeRequest);
        }
    }
}
