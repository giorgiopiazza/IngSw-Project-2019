package model.cards;

import enumerations.Ammo;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import exceptions.command.InvalidCommandException;
import exceptions.player.EmptyHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.BaseEffect;
import model.cards.effects.Effect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
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
        UserPlayer shootingPlayer;
        BaseEffect effect;
        String[] splitCommand;
        String[] utilitySplit;
        int position;
        int effectNum;
        int powerups[];
        ArrayList<Ammo> costList;
        if (isCharged()) {
            // first I split the command String with blanks as I can access each singular action argument
            splitCommand = command.split(" ");

            // i save the player that is using the card
            shootingPlayer = (UserPlayer) Game.getInstance().getPlayerByID(CommandUtility.getCommandSplitPosition(splitCommand, "-pid"));

            // i use getCommandSplitPosition to find the position of the effect
            position = CommandUtility.getCommandSplitPosition(splitCommand, "-e");

            try {   // control that after -e there exists a number
                effectNum = Integer.parseInt(splitCommand[position + 1]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }

            // i take the corresponding effect I find in the command, if the number is too high the command is Invalid
            if (splitCommand[position + 1].equals("0")) {
                effect = (BaseEffect) getBaseEffect();
            } else if (effectNum <= secondaryEffects.size()) {
                effect = (BaseEffect) secondaryEffects.get(effectNum - 1);
            } else throw new InvalidCommandException();
            //effect.validate();


            if (effect.getCost().length > 0) {
                costList = new ArrayList<>(Arrays.asList(effect.getCost()));
                int redCost = Collections.frequency(costList, Ammo.RED);
                int yellowCost = Collections.frequency(costList, Ammo.YELLOW);
                int blueCost = Collections.frequency(costList, Ammo.BLUE);

                if (command.contains("-a")) {    // if command contains powerups i try to use them to pay the effect
                    // i use getCommandSplitPosition to find the position of the powerups
                    position = CommandUtility.getCommandSplitPosition(splitCommand, "-a");

                    try {   // control that after -a exists powerups and puts the powerup you want to use in powerups
                        utilitySplit = splitCommand[position + 1].split(",");
                        powerups = new int[utilitySplit.length];
                        for (int i = 0; i < utilitySplit.length; ++i) {
                            powerups[i] = Integer.parseInt(utilitySplit[i]);

                        }
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandException();
                    }

                    // i first try to pay the effect with powerups
                    for (int i = 0; i < powerups.length; ++i) {
                        Ammo tempAmmo = shootingPlayer.getPowerups()[powerups[i]].getValue();

                        try {
                            switch (tempAmmo) {
                                case RED:
                                    if (redCost > 0) {
                                        shootingPlayer.discardPowerupByIndex(i);
                                        --redCost;
                                        costList.remove(tempAmmo);
                                    }
                                    break;
                                case YELLOW:
                                    if (yellowCost > 0) {
                                        shootingPlayer.discardPowerupByIndex(i);
                                        --yellowCost;
                                        costList.remove(tempAmmo);
                                    }
                                    break;
                                default:
                                    if (blueCost > 0) {
                                        shootingPlayer.discardPowerupByIndex(i);
                                        --blueCost;
                                        costList.remove(tempAmmo);
                                    }
                            }
                        } catch (EmptyHandException e) {
                            // this exception should never be thrown because the control that the player has the powerup is already done here
                            throw new InvalidCommandException();
                        }
                    }
                }

                // then I end to pay the effect if it hasn't be already payed with powerups
                if ((redCost != 0) && (yellowCost != 0) && (blueCost != 0)) {
                    try {
                        shootingPlayer.getPlayerBoard().useAmmo(costList);
                    } catch (NotEnoughAmmoException e) {
                        throw new NotEnoughAmmoException();
                    }
                }
            }
            // command can be modelled to be lighter in the weaponstate.use call
            weaponState.use(effect, command);

            setStatus(new UnchargedWeapon());
        } else {
            throw new WeaponNotChargedException(this.getName());
        }
    }
}