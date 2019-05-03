package model.actions;

import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.cards.WeaponCard;
import model.player.UserPlayer;
import network.message.EffectRequest;

import java.util.List;

public class RechargeAction implements Action {
    private UserPlayer actingPlayer;
    private List<WeaponCard> rechargingWeapons;
    private EffectRequest rechargeRequest;

    public RechargeAction(UserPlayer actingPlayer, List<WeaponCard> rechargingWeapons, EffectRequest rechargeRequest) {
        this.actingPlayer = actingPlayer;
        this.rechargingWeapons = rechargingWeapons;
        this.rechargeRequest = rechargeRequest;
    }

    @Override
    public boolean validate() {
        for (WeaponCard weaponCard : rechargingWeapons) {
            if (!actingPlayer.hasWeapon(weaponCard)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute() throws WeaponAlreadyChargedException, NotEnoughAmmoException {
        for(WeaponCard weaponCard : rechargingWeapons) {
            weaponCard.payRechargeCost(rechargeRequest);
        }
    }
}
