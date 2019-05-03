package model.cards;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import exceptions.command.InvalidCommandException;
import exceptions.player.EmptyHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.effects.Effect;
import model.cards.effects.WeaponBaseEffect;
import model.cards.weaponstates.ChargedWeapon;
import model.cards.weaponstates.UnchargedWeapon;
import model.cards.weaponstates.WeaponState;
import model.player.AmmoQuantity;
import model.player.UserPlayer;
import network.message.EffectRequest;
import network.message.FireRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WeaponCard extends UsableCard {
    private final int ID;
    private final Ammo[] cost;
    private final List<Effect> secondaryEffects;
    private WeaponState weaponState;
    public static final int CHARGED = 0;
    public static final int UNCHARGED = 1;
    public static final int SEMI_CHARGED = 2;

    public WeaponCard(String name, File image, Effect baseEffect,int ID, Ammo[] cost,
                      List<Effect> secondaryEffects, WeaponState weaponState) {
        super(name, image, baseEffect);
        this.ID = ID;
        this.cost = cost;
        this.secondaryEffects = secondaryEffects;
        this.weaponState = weaponState;

    }

    /**
     * @return the ID of the weapon
     */
    public int getID() {
        return this.ID;
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
     * @param request is the request that comes from the controller to execute the FIRE action
     * @throws WeaponNotChargedException in case the weapon can not be used because not charged
     * @throws NotEnoughAmmoException    in case the weapon even with powerups can not be payed
     */
    @Override
    public void use(EffectRequest request) throws AdrenalinaException {
        FireRequest fireRequest = (FireRequest) request;

        if (isCharged()) {
            Effect effect;

            int pId = fireRequest.getSenderID();
            int eId = fireRequest.getEffectID();

            if (pId >= Game.getInstance().playersNumber()) {
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
                payEffectCost(fireRequest, ((WeaponBaseEffect) effect).getCost());

                weaponState.use(effect, fireRequest);
                setStatus(new UnchargedWeapon());
            } else {
                throw new InvalidCommandException();
            }
        } else {
            throw new WeaponNotChargedException(this.getName());
        }
    }

    public void payRechargeCost(UserPlayer payingPlayer, EffectRequest request) {
        /* TODO GIORGIO
         * Io uso il metodo sia nella PickAction che nella ShootAction:
         *
         * PickAction: mi serve per pagare il costo di un arma quando è posizionata sulla board, nella request che
         *             arriva dalla action in cui è istanziata sono presenti i possibili powerup con cui pagare
         *
         * ShootAction: mi serve per ricaricare le armi prima di sparare quando uso un'azione in frenzy mode, in questo
         *              caso l'arma è completamente scarica e la voglio ricaricare per usarla sempre con i possibili
         *              powerup contenuti nel messaggio passato dalla action in cui è istanziato
         *
         * Lancia eccezioni così io domani faccio tutti i catch per non fare eseguire la action
         */
    }

    private void payEffectCost(FireRequest request, AmmoQuantity cost) throws NotEnoughAmmoException {
        UserPlayer shootingPlayer = Game.getInstance().getPlayerByID(request.getSenderID());
        PowerupCard[] powerupCards = shootingPlayer.getPowerups();

        List<Integer> powerupsID = request.getPowerupsID();
        List<Integer> usedPowerupsID = new ArrayList<>();

        AmmoQuantity costWithoutPowerups = getCostWithoutPowerup(cost, powerupsID, usedPowerupsID, powerupCards);
        shootingPlayer.getPlayerBoard().useAmmo(costWithoutPowerups);

        if (!usedPowerupsID.isEmpty()) {
            usedPowerupsID.sort(Collections.reverseOrder());

            try {
                for (Integer id : usedPowerupsID) {
                    shootingPlayer.discardPowerupByIndex(id);
                }
            } catch (EmptyHandException e) {
                throw new InvalidCommandException();
            }
        }
    }

    private AmmoQuantity getCostWithoutPowerup(AmmoQuantity cost, List<Integer> powerupsID, List<Integer> usedPowerupsID, PowerupCard[] powerupCards) {
        int redCost = cost.getRedAmmo();
        int blueCost = cost.getBlueAmmo();
        int yellowCost = cost.getYellowAmmo();

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