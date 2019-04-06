package model.cards;

public interface WeaponState {

    boolean charged(WeaponCard weapon);

    boolean rechargeable(WeaponCard weapon);

    int status();

    void use(Effect effect);
}
