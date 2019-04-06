package model.cards;

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
    public void use(Effect effect) {
        effect.execute(effect.getTarget());
    }
}
