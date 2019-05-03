package model.actions;

import enumerations.PossibleAction;
import exceptions.AdrenalinaException;
import exceptions.actions.IncompatibleActionException;
import exceptions.actions.InvalidActionException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.cards.WeaponCard;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.EffectRequest;

import java.util.List;

public class ShootAction implements Action {
    private static final int MAX_NORMAL_MOVE = 0;
    private static final int MAX_ADRENALINE_MOVE = 1;
    private static final int MAX_FRENZY_MOVE = 1;
    private static final int MAX_LIGHT_FRENZY_MOVE = 2;

    private UserPlayer actingPlayer;
    private WeaponCard shootingWeapon;
    private PossibleAction actionChosen;
    private PlayerPosition movingPos;
    private EffectRequest shootRequest;
    private List<WeaponCard> rechargingWeapons;
    private RechargeAction rechargeAction;

    public ShootAction(UserPlayer actingPlayer, WeaponCard shootingWeapon, PossibleAction actionChosen,
                       PlayerPosition movingPos, EffectRequest shootRequest, List<WeaponCard> rechargingWeapons) {
        this.actingPlayer = actingPlayer;
        this.shootingWeapon = shootingWeapon;
        this.actionChosen = actionChosen;
        this.shootRequest = shootRequest;
        this.rechargingWeapons = rechargingWeapons;
        this.rechargeAction = new RechargeAction(actingPlayer, rechargingWeapons, shootRequest);

        if (actingPlayer.getPosition().equals(movingPos) || movingPos == null) {
            this.movingPos = actingPlayer.getPosition();
        } else {
            this.movingPos = movingPos;
        }
    }

    @Override
    public boolean validate() {
        int movingDistance = actingPlayer.getPosition().distanceOf(movingPos);

        // moving validation
        switch (actionChosen) {
            case SHOOT:
                if (movingDistance != MAX_NORMAL_MOVE) {
                    return false;
                }
                break;
            case ADRENALINE_SHOOT:
                if (movingDistance > MAX_ADRENALINE_MOVE) {
                    return false;
                }
                break;
            case FRENZY_SHOOT:
                if (movingDistance > MAX_FRENZY_MOVE) {
                    return false;
                }
                break;
            case LIGHT_FRENZY_SHOOT:
                if (movingDistance > MAX_LIGHT_FRENZY_MOVE) {
                    return false;
                }
                break;
            default:
                throw new IncompatibleActionException(actionChosen);
        }

        // shooting validation
        return actingPlayer.hasWeapon(shootingWeapon);
}

    @Override
    public void execute() {
        // first I move the shooter saving his position in case after the weapon validate it can not be used
        PlayerPosition startingPos = actingPlayer.getPosition();
        actingPlayer.changePosition(movingPos.getCoordX(), movingPos.getCoordY());

        // if the shooting action is a frenzy one I can also recharge my weapons before shooting
        if (rechargingWeapons != null && rechargeAction != null &&
                (actionChosen == PossibleAction.FRENZY_SHOOT || actionChosen == PossibleAction.LIGHT_FRENZY_SHOOT)) {
            if(rechargeAction.validate()) {
                try {
                    rechargeAction.execute();
                } catch (WeaponAlreadyChargedException e) {
                    // TODO Should we do something?
                } catch (NotEnoughAmmoException e) {
                    // TODO Should we do something?
                }

            } else {
                throw new InvalidActionException();
            }
        }

        // then I shoot
        try {
            shootingWeapon.use(shootRequest);
        } catch (AdrenalinaException e) {
            // the weapon can not be used, then I set back the shooting position to his starting position because this
            // action can not be executed
            actingPlayer.changePosition(startingPos.getCoordX(), startingPos.getCoordY());
        }
    }
}
