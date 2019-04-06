package model.cards;

import exceptions.cards.WeaponNotChargedException;
import model.player.Player;

public class SemiChargedWeapon implements WeaponState {

    @Override
    public boolean charged(WeaponCard weapon) {
        return false;
    }

    @Override
    public boolean rechargeable(WeaponCard weapon) {
        return true;
    }

    @Override
    public int status() {
        return WeaponCard.SEMI_CHARGED;
    }

    @Override
    public void use(Effect effect, Player playerDealer) {
        throw new WeaponNotChargedException();
    }
}
