package model.cards.weaponstates;

import exceptions.cards.WeaponNotChargedException;
import model.cards.Target;
import model.cards.effects.Effect;
import model.cards.WeaponCard;
import model.player.Player;

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
    public void use(Effect effect, Target target, Player playerDealer) throws WeaponNotChargedException {
        throw new WeaponNotChargedException();
    }
}
