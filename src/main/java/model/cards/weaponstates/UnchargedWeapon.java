package model.cards.weaponstates;

import exceptions.cards.WeaponNotChargedException;
import model.cards.WeaponCard;
import model.cards.effects.Effect;

public class UnchargedWeapon implements WeaponState {

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
        return WeaponCard.UNCHARGED;
    }

    @Override
    public void use(Effect effect, String command) throws WeaponNotChargedException {
        throw new WeaponNotChargedException();
    }
}
