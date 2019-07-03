package model.cards;

import enumerations.Ammo;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import exceptions.command.InvalidCommandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
import model.player.AmmoQuantity;
import network.message.ActionRequest;
import network.message.EffectRequest;
import network.message.ShootRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class WeaponCard extends UsableCard {
    private static final long serialVersionUID = -1676793782570413675L;

    private final int id;
    private final Ammo[] cost;
    private final ArrayList<Effect> secondaryEffects;
    private WeaponState weaponState;
    public static final int CHARGED = 0;
    public static final int UNCHARGED = 1;
    public static final int SEMI_CHARGED = 2;

    public WeaponCard(String name, String imagePath, Effect baseEffect, int id, Ammo[] cost,
                      List<Effect> secondaryEffects, WeaponState weaponState) {
        super(name, imagePath, baseEffect);
        this.id = id;
        this.cost = cost;

        if (secondaryEffects != null) {
            this.secondaryEffects = new ArrayList<>(secondaryEffects);
        } else {
            this.secondaryEffects = new ArrayList<>();
        }

        this.weaponState = weaponState;
    }

    /**
     * @return the id of the weapon
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return the entire cost of the weapon
     */
    public Ammo[] getCost() {
        return this.cost;
    }

    /**
     * @return the secondary effects of the weapon
     */
    public List<Effect> getSecondaryEffects() {
        return this.secondaryEffects;
    }

    /**
     * Return the cost to use the Weapon depending on it's state
     *
     * @return an array of Ammo which is the recharging Cost of the Weapon
     */
    public AmmoQuantity getRechargeCost() {
        switch (this.weaponState.status()) {
            case UNCHARGED:
                return new AmmoQuantity(cost);

            case SEMI_CHARGED:
                return new AmmoQuantity(Arrays.copyOfRange(cost, 1, cost.length));

            default:
                return new AmmoQuantity();
        }
    }

    /**
     * Sets the state of the Weapon
     *
     * @param status the State to put the Weapon
     */
    public void setStatus(WeaponState status) {
        this.weaponState = status;
    }

    /**
     * @return true if the Weapon is charged, otherwise false
     */
    private boolean isCharged() {
        return weaponState.charged(this);
    }

    /**
     * @return the State of the Weapon: CHARGED = 0, UNCHARGED = 1, SEMI_CHARGED = 2
     */
    public int status() {
        return weaponState.status();
    }

    /**
     * @return true if the Weapon is isRechargeable, otherwise false
     */
    public boolean isRechargeable() {
        return weaponState.isRechargeable(this);
    }

    /**
     * Method used to recharge a Weapon
     *
     * @throws WeaponAlreadyChargedException exception thrown in case the weapon is already charged
     */
    public void recharge() throws WeaponAlreadyChargedException {
        if (this.isRechargeable()) {
            setStatus(new ChargedWeapon());
        } else {
            throw new WeaponAlreadyChargedException(this.getName());
        }
    }

    /**
     * Main method called by the player who wants to use this weapon. It parses the command obtaining
     * what he needs to verify first things of a shooting session: which is the effect used, if it can
     * be payed and if the player uses powerups to pay it
     *
     * @param request is the request that comes from the controller to execute the FIRE action
     * @throws WeaponNotChargedException in case the weapon can not be used because not charged
     * @throws NotEnoughAmmoException    in case the weapon even with powerups can not be payed
     */
    @Override
    public void use(EffectRequest request) throws NotEnoughAmmoException, WeaponNotChargedException {
        ShootRequest shootRequest = (ShootRequest) request;

        if (isCharged()) {
            Effect effect;

            String username = shootRequest.getSenderUsername();
            int eId = shootRequest.getEffect();

            if (!Game.getInstance().doesPlayerExists(username)) {
                throw new InvalidCommandException();
            }

            if (eId == 0) {
                effect = getBaseEffect();
            } else if (eId <= secondaryEffects.size()) {
                effect = secondaryEffects.get(eId - 1);
            } else {
                throw new InvalidCommandException();
            }

            if (effect.validate(request)) {
                List<Integer> paymentPowerups = shootRequest.getPaymentPowerups().stream().distinct().collect(Collectors.toList());

                WeaponCostUtility.payCost(shootRequest.getSenderUsername(), paymentPowerups, effect.getCost());

                weaponState.use(effect, shootRequest);
                setStatus(new UnchargedWeapon());
            } else {
                throw new InvalidCommandException();
            }
        } else {
            throw new WeaponNotChargedException(this.getName());
        }
    }

    public void payRechargeCost(ActionRequest request) throws WeaponAlreadyChargedException, NotEnoughAmmoException {
        if (!isRechargeable()) {
            throw new WeaponAlreadyChargedException();
        }

        AmmoQuantity rechargeCost = getRechargeCost();

        List<Integer> paymentPowerups = request.getPaymentPowerups().stream().distinct().collect(Collectors.toList());

        WeaponCostUtility.payCost(request.getSenderUsername(), paymentPowerups, rechargeCost);

        recharge();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WeaponCard that = (WeaponCard) o;
        return id == that.id &&
                Arrays.equals(cost, that.cost) &&
                Objects.equals(secondaryEffects, that.secondaryEffects) &&
                Objects.equals(weaponState, that.weaponState);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), id, secondaryEffects, weaponState);
        result = 31 * result + Arrays.hashCode(cost);
        return result;
    }

    @Override
    public String toString() {
        return "WeaponCard{" +
                "id=" + id +
                ", cost=" + (cost == null ? "null" : Arrays.toString(cost)) +
                ", secondaryEffects=" + secondaryEffects +
                ", weaponState=" + weaponState +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}