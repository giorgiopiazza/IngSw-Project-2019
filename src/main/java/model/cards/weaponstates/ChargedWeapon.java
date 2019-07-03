package model.cards.weaponstates;

import model.cards.WeaponCard;
import model.cards.effects.Effect;
import network.message.EffectRequest;

public class ChargedWeapon implements WeaponState {

    private static final long serialVersionUID = 2683351711217253821L;

    @Override
    public boolean charged(WeaponCard weapon) {
        return true;
    }

    @Override
    public boolean isRechargeable(WeaponCard weapon) {
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
