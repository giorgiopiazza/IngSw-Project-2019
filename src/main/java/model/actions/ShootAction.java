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

    public ShootAction(UserPlayer actingPlayer, PossibleAction actionChosen, ShootRequest shootRequest) {
        this.actingPlayer = actingPlayer;
        this.shootingWeapon = actingPlayer.getWeapons()[shootRequest.getWeaponID()];
        this.actionChosen = actionChosen;
        this.movingPos = shootRequest.getAdrenalineMovePosition();
        this.shootRequest = shootRequest;

        if(shootRequest.getRechargingWeapons() != null) {
            // i payment powerup in questo caso devono essere dentro la shootRequest per pagare le armi da ricaricare
            ReloadRequest reloadRequest = new ReloadRequest(actingPlayer.getUsername(), shootRequest.getToken(), shootRequest.getRechargingWeapons(), shootRequest.getPaymentPowerups());
            this.reloadAction = new ReloadAction(actingPlayer, reloadRequest);
        }

        if (movingPos == null) {
            this.movingPos = actingPlayer.getPosition();
        }
    }

    @Override
    public boolean validate() {
        int movingDistance = actingPlayer.getPosition().distanceOf(movingPos);

        // moving validation
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
                if (movingDistance > MAX_FRENZY_MOVE || reloadAction == null) {
                    return false;
                }
                break;
            case LIGHT_FRENZY_SHOOT:
                if (movingDistance > MAX_LIGHT_FRENZY_MOVE || reloadAction == null) {
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
    public void execute() throws InvalidActionException, WeaponAlreadyChargedException, NotEnoughAmmoException, WeaponNotChargedException {
        // first I move the shooter saving his position in case after the weapon validate it can not be used
        PlayerPosition startingPos = actingPlayer.getPosition();
        actingPlayer.changePosition(movingPos.getCoordX(), movingPos.getCoordY());

        // if the shooting action is a frenzy one I can also recharge my weapons before shooting
        if (actionChosen == PossibleAction.FRENZY_SHOOT || actionChosen == PossibleAction.LIGHT_FRENZY_SHOOT) {
            if (reloadAction.validate()) {
                reloadAction.execute();
            } else {
                throw new InvalidActionException();
            }
        }

        // then I shoot
        try {
            shootingWeapon.use(shootRequest);
        } catch (WeaponNotChargedException | InvalidCommandException e) {
            // the weapon can not be used, then I set back the shooting position to his starting position because this
            // action can not be executed
            actingPlayer.changePosition(startingPos.getCoordX(), startingPos.getCoordY());
            throw new WeaponNotChargedException();
        }
    }
}
