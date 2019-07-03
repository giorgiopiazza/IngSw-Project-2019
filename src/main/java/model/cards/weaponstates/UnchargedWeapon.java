package model.cards.weaponstates;

import exceptions.cards.WeaponNotChargedException;
import model.cards.WeaponCard;
import model.cards.effects.Effect;
import network.message.EffectRequest;

public class UnchargedWeapon implements WeaponState {

    private static final long serialVersionUID = -7890105178311912735L;

    @Override
    public boolean charged(WeaponCard weapon) {
        return false;
    }

    @Override
    public boolean isRechargeable(WeaponCard weapon) {
        return true;
    }

    @Override
    public int status() {
        return WeaponCard.UNCHARGED;
    }

    @Override
    public void use(Effect effect, EffectRequest request) throws WeaponNotChargedException {
        throw new WeaponNotChargedException();
    }
}
