package model.cards;

import enumerations.Ammo;
import enumerations.Color;
import exceptions.AdrenalinaException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.player.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class WeaponCard extends UsableCard {
    private final Ammo[] cost;
    private final List<Effect> secondaryEffects;
    private WeaponState weaponState;
    public static final int CHARGED = 0;
    public static final int UNCHARGED = 1;
    public static final int SEMI_CHARGED = 2;

    public WeaponCard(String name, File image, Color color, Effect baseEffect, Ammo[] cost,
                      List<Effect> secondaryEffects, WeaponState weaponState) {
        super(name, image, color, baseEffect);
        this.cost = cost;
        this.secondaryEffects = secondaryEffects;
        this.weaponState = weaponState;
    }

    public Ammo[] getCost() {
        return this.cost;
    }

    public Ammo[] getRechargeCost() {
        switch (this.weaponState.status()) {
            case UNCHARGED:
                return cost;

            case SEMI_CHARGED:
                return Arrays.copyOfRange(cost, 1, cost.length - 1);

            default:
                return new Ammo[0];
        }
    }

    public List<Effect> getEffects() {
        return this.secondaryEffects;
    }

    public void setStatus(WeaponState status) {
        this.weaponState = status;
    }

    public boolean isCharged() {
        return weaponState.charged(this);
    }

    public int status() {
        return weaponState.status();
    }

    public boolean rechargeable() {
        return weaponState.rechargeable(this);
    }

    public void recharge() throws WeaponAlreadyChargedException {
        if (this.rechargeable()) {
            setStatus(new ChargedWeapon());
        } else throw new WeaponAlreadyChargedException(this.getName());
    }

    public void use(Effect effect, FiringAction firingAction, Player playerDealer) throws AdrenalinaException {
        if (isCharged()) {
            weaponState.use(effect, firingAction, playerDealer);
        } else throw new WeaponNotChargedException(this.getName());
    }

}
