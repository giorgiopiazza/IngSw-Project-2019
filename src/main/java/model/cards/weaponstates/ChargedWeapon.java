package model.cards.weaponstates;

import exceptions.AdrenalinaException;
import model.cards.Target;
import model.cards.effects.Effect;
import model.cards.WeaponCard;
import model.cards.WeaponState;
import model.player.Player;

public class ChargedWeapon implements WeaponState {

    @Override
    public boolean charged(WeaponCard weapon) {
        return true;
    }

    @Override
    public boolean rechargeable(WeaponCard weapon) {
        return false;
    }

    @Override
    public int status() {
        return WeaponCard.CHARGED;
    }

    @Override
    public void use(Effect effect, Target target, Player playerDealer) throws AdrenalinaException {
        effect.execute(playerDealer);
    }
}
