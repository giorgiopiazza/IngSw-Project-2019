package model.actions;

import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.cards.WeaponCard;
import model.player.UserPlayer;
import network.message.EffectRequest;
import network.message.ReloadRequest;

import java.util.List;

public class ReloadAction implements Action {
    private UserPlayer actingPlayer;
    private List<WeaponCard> rechargingWeapons;
    private ReloadRequest rechargeRequest;

    public ReloadAction(UserPlayer actingPlayer, List<WeaponCard> rechargingWeapons, ReloadRequest rechargeRequest) {
        this.actingPlayer = actingPlayer;
        this.rechargingWeapons = rechargingWeapons;
        this.rechargeRequest = rechargeRequest;
    }

    @Override
    public boolean validate() {
        if(rechargingWeapons.isEmpty() || rechargingWeapons == null) {
            return true;
        }

        for (WeaponCard weaponCard : rechargingWeapons) {
            if (!actingPlayer.hasWeapon(weaponCard)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute() throws WeaponAlreadyChargedException, NotEnoughAmmoException {
        if(rechargingWeapons.isEmpty() || rechargingWeapons == null) {
            return;
        }

        for(WeaponCard weaponCard : rechargingWeapons) {
            /* TODO fix nella payrecharge cost con tipo giusto delle request
            weaponCard.payRechargeCost(rechargeRequest);
             */
        }
    }
}
