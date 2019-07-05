package model.actions;

import enumerations.PossibleAction;
import exceptions.actions.IncompatibleActionException;
import exceptions.actions.InvalidActionException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import exceptions.command.InvalidCommandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.cards.WeaponCard;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.EffectRequest;
import network.message.ReloadRequest;
import network.message.ShootRequest;
import utility.InputValidator;

/**
 * Implements the Shoot Action considering all the possible kind of Shoot that are activated by
 * a player during the game. Remember that a shoot action can involve a {@link ReloadAction
 * ReloadAction} in case the kind of action is a FRENZY ONE
 */
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
    private ReloadAction reloadAction;

    /**
     * Builds a Shoot Action that executes a shoot during the game.
     * This kind of action is the one that receives the most complex request due to give
     * the possibility to a shooter to perform any kind of possible shoot action valid
     * in the game
     *
     * @param actingPlayer the Shooting Acting Player
     * @param actionChosen the kind of Shoot Action that is going to be performed
     * @param shootRequest the {@link ShootRequest ShootRequest} received
     */
    public ShootAction(UserPlayer actingPlayer, PossibleAction actionChosen, ShootRequest shootRequest) {
        this.actingPlayer = actingPlayer;
        this.shootingWeapon = actingPlayer.getWeapons()[shootRequest.getWeaponID()];
        this.actionChosen = actionChosen;
        this.movingPos = shootRequest.getMoveBeforeShootPosition();
        this.shootRequest = shootRequest;

        if (!shootRequest.getRechargingWeapons().isEmpty()) {
            // i payment powerup in questo caso devono essere dentro la shootRequest per pagare le armi da ricaricare
            ReloadRequest reloadRequest = new ReloadRequest(actingPlayer.getUsername(), shootRequest.getToken(), shootRequest.getRechargingWeapons(), shootRequest.getPaymentPowerups());
            this.reloadAction = new ReloadAction(actingPlayer, reloadRequest);
        }

        if (movingPos == null) {
            this.movingPos = actingPlayer.getPosition();
        }
    }

    /**
     * Validates the Shoot Action considering all the constraints on the inputs and the ones
     * defined by the kind of Shoot Action that is going to be executed
     *
     * @return true if the Shoot Action is valid, otherwise false
     * @throws InvalidActionException in case the action is invalid due to input validation
     */
    @Override
    public boolean validate() throws InvalidActionException {
        if (!InputValidator.validatePosition(movingPos)) {
            throw new InvalidActionException();
        }

        if (!InputValidator.validateIndexes(shootRequest, actingPlayer)) {
            throw new InvalidActionException();
        }

        // moving validation
        int movingDistance = actingPlayer.getPosition().distanceOf(movingPos);
        switch (actionChosen) {
            case SHOOT:
                if (movingDistance != MAX_NORMAL_MOVE || reloadAction != null) {
                    return false;
                }
                break;
            case ADRENALINE_SHOOT:
                if (movingDistance > MAX_ADRENALINE_MOVE || reloadAction != null) {
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

        return true;
    }

    /**
     * Executes the Shoot Action considering also the case of a Reload during it.
     * It is the method that throws all the invalid action exception given by the validation
     * of the effect of the chosen Weapon. Also reload exceptions are thrown here, in case
     * during a FRENZY Shoot, the Reload Action is not valid
     *
     * @throws InvalidActionException in case the action is not valid
     * @throws WeaponAlreadyChargedException in case the weapon is already charged
     * @throws NotEnoughAmmoException in case the weapons can not be recharged
     * @throws WeaponNotChargedException in case the using weapon is not charged
     */
    @Override
    public void execute() throws InvalidActionException, WeaponAlreadyChargedException, NotEnoughAmmoException, WeaponNotChargedException {
        // first I move the shooter saving his position in case after the weapon validate it can not be used
        PlayerPosition startingPos = new PlayerPosition(actingPlayer.getPosition());
        actingPlayer.changePosition(movingPos.getRow(), movingPos.getColumn());

        // if the shooting action is a frenzy one I can also recharge my weapons before shooting
        if (reloadAction != null && (actionChosen == PossibleAction.FRENZY_SHOOT || actionChosen == PossibleAction.LIGHT_FRENZY_SHOOT)) {
            if (reloadAction.validate()) {
                reloadAction.execute();
            } else {
                throw new InvalidActionException();
            }
        }

        // then I shoot
        try {
            shootingWeapon.use(shootRequest);
        } catch (WeaponNotChargedException e) {
            // the weapon can not be used, then I set back the shooting position to his starting position because this
            // action can not be executed
            actingPlayer.changePosition(startingPos.getRow(), startingPos.getColumn());
            throw new WeaponNotChargedException();
        } catch (InvalidCommandException e) {
            actingPlayer.changePosition(startingPos.getRow(), startingPos.getColumn());
            throw new InvalidActionException();
        }
    }
}
