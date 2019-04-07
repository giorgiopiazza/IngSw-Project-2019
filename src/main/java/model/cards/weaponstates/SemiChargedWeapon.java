package model.cards.weaponstates;

import exceptions.cards.WeaponNotChargedException;
import model.cards.FiringAction;
import model.cards.effects.Effect;
import model.cards.WeaponCard;
import model.cards.WeaponState;
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
    public void use(Effect effect, FiringAction firingAction, Player playerDealer) throws WeaponNotChargedException {
        throw new WeaponNotChargedException();
    }
}
