package model.cards;

import enumerations.Ammo;
import enumerations.Color;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;

import java.util.List;

public class WeaponCard extends Card {
    private final Ammo[] unchargedCost;
    private final Ammo[] halfCost;
    private final List<Effect> secondaryEffects;
    private WeaponState weaponState;
    public final static int CHARGED = 0, UNCHARGED = 1, SEMI_CHARGED = 2;

    public WeaponCard(String name, Color color, Effect baseEffect, Ammo[] unchargedCost,
                      Ammo[] halfCost, List<Effect> secondaryEffects, WeaponState weaponState) {
        super(name, color, baseEffect);
        this.unchargedCost = unchargedCost;
        this.halfCost = halfCost;
        this.secondaryEffects = secondaryEffects;
        this.weaponState = weaponState;
    }

    public Ammo[] getUnchargedCost() {
        return this.unchargedCost;
    }

    public Ammo[] getHalfCost() {
        return this.halfCost;
    }

    public List<Effect> getEffects() {
        return this.secondaryEffects;
    }

    public void setStatus(WeaponState status) {
        this.weaponState = status;
    }

    public boolean charged() {
        return weaponState.charged(this);
    }

    public int status() {
        return weaponState.status();
    }

    public boolean rechargeable() {
        if (this.status() == 1 || this.status() == 2) {
            return true;
        } else {
            return false;
        }
    }

    public void recharge() {
        if (this.rechargeable()) {
            setStatus(new ChargedWeapon());
        } else throw new WeaponAlreadyChargedException(this.getName());
    }

    public void use(Effect effect) {
        if (charged()) {
            effect.execute(effect.getTarget());
        } else throw new WeaponNotChargedException(this.getName());
    }


}
