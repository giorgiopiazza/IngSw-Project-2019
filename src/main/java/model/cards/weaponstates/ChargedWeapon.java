package model.cards.weaponstates;

import model.cards.WeaponCard;
import model.cards.effects.Effect;
import network.message.EffectRequest;
import network.message.FireRequest;
import network.message.Message;

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
    public void use(Effect effect, EffectRequest request) {
        effect.execute(request);
    }
}
