package model.cards;

import enumerations.Ammo;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import exceptions.command.InvalidCommandException;
import exceptions.player.EmptyHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import utility.CommandUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WeaponCard extends UsableCard {
    private final Ammo[] cost;
    private final List<Effect> secondaryEffects;
    private WeaponState weaponState;
    public static final int CHARGED = 0;
    public static final int UNCHARGED = 1;
    public static final int SEMI_CHARGED = 2;

    public WeaponCard(String name, File image, Effect baseEffect, Ammo[] cost,
                      List<Effect> secondaryEffects, WeaponState weaponState) {
        super(name, image, baseEffect);
        this.cost = cost;
        this.secondaryEffects = secondaryEffects;
        this.weaponState = weaponState;

    }

    /**
     * Return the cost to use the Weapon depending on it's state
     *
     * @return an array of Ammo which is the recharging Cost of the Weapon
     */
    public Ammo[] getRechargeCost() {
        switch (this.weaponState.status()) {
            case UNCHARGED:
                return cost;

            case SEMI_CHARGED:
                return Arrays.copyOfRange(cost, 1, cost.length);

            default:
                return new Ammo[0];
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
    public boolean isCharged() {
        return weaponState.charged(this);
    }


    /**
     * @return the State of the Weapon: CHARGED = 0, UNCHARGED = 1, SEMI_CHARGED = 2
     */
    public int status() {
        return weaponState.status();
    }

    /**
     * @return true if the Weapon is rechargeable, otherwise false
     */
    public boolean rechargeable() {
        return weaponState.rechargeable(this);
    }

    /**
     * Method used to recharge a Weapon
     *
     * @throws WeaponAlreadyChargedException exception thrown in case the weapon is already charged
     */
    public void recharge() throws WeaponAlreadyChargedException {
        if (this.rechargeable()) {
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
     * @param command is the String that comes from the controller to execute the FIRE action
     * @throws WeaponNotChargedException in case the weapon can not be used because not charged
     * @throws NotEnoughAmmoException    in case the weapon even with powerups can not be payed
     */
    public void use(String command) throws WeaponNotChargedException, NotEnoughAmmoException {
        if (isCharged()) {
            Effect effect;

            String[] splitCommand = command.split(" ");
            int pid = CommandUtility.getPlayerID(splitCommand);
            int eid = CommandUtility.getEffectID(splitCommand);

            UserPlayer shootingPlayer = (UserPlayer) Game.getInstance().getPlayerByID(pid);

            if (eid == 0) {
                effect = getBaseEffect();
            } else if (eid <= secondaryEffects.size()) {
                effect = secondaryEffects.get(eid - 1);
            } else {
                throw new InvalidCommandException();
            }

            if (effect.validate(command)) {
                payEffectCost(command, shootingPlayer, effect);

                weaponState.use(effect, command);
                setStatus(new UnchargedWeapon());
            } else {
                throw new InvalidCommandException();
            }
        } else {
            throw new WeaponNotChargedException(this.getName());
        }
    }

    private void payEffectCost(String command, UserPlayer shootingPlayer, Effect effect) throws NotEnoughAmmoException {
        String[] splitCommand = command.split(" ");

        AmmoQuantity effectCost = effect.getCost();
        PowerupCard[] powerupCards = shootingPlayer.getPowerups();

        List<Integer> powerupsID = CommandUtility.getAttributesID(splitCommand, "-a");
        List<Integer> usedPowerupsID = new ArrayList<>();

        AmmoQuantity costWithoutPowerups = getCostWithoutPowerup(effectCost, powerupsID, usedPowerupsID, powerupCards);
        shootingPlayer.getPlayerBoard().useAmmo(costWithoutPowerups);

        if (!usedPowerupsID.isEmpty()) {
            Collections.sort(usedPowerupsID, Collections.reverseOrder());

            try {
                for (Integer id : usedPowerupsID) {
                    shootingPlayer.discardPowerupByIndex(id);
                }
            } catch (EmptyHandException e) {
                throw new InvalidCommandException();
            }
        }
    }

    private AmmoQuantity getCostWithoutPowerup(AmmoQuantity effectCost, List<Integer> powerupsID, List<Integer> usedPowerupsID, PowerupCard[] powerupCards) {
        int redCost = effectCost.getRedAmmo();
        int blueCost = effectCost.getBlueAmmo();
        int yellowCost = effectCost.getYellowAmmo();

        if (powerupsID.isEmpty()) {
            return new AmmoQuantity(redCost, blueCost, yellowCost);
        }

        for (Integer id : powerupsID) {
            Ammo ammo = powerupCards[id].getValue();

            switch (ammo) {
                case RED:
                    if (redCost > 0) {
                        redCost--;
                        usedPowerupsID.add(id);
                    }
                    break;
                case BLUE:
                    if (blueCost > 0) {
                        blueCost--;
                        usedPowerupsID.add(id);
                    }
                    break;
                default:
                    if (yellowCost > 0) {
                        yellowCost--;
                        usedPowerupsID.add(id);
                    }
            }
        }

        return new AmmoQuantity(redCost, blueCost, yellowCost);
    }
}