package model.cards;

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
}
