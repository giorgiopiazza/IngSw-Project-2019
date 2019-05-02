package model.actions;

import enumerations.PossibleAction;
import enumerations.SquareType;
import exceptions.actions.IncompatibleActionException;
import exceptions.player.MaxCardsInHandException;
import model.Game;
import model.cards.WeaponCard;
import model.cards.weaponstates.SemiChargedWeapon;
import model.map.CardSquare;
import model.map.SpawnSquare;
import model.map.Square;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.EffectRequest;

public class PickAction implements Action {
    private static final int MAX_NORMAL_MOVE = 1;
    private static final int MAX_ADRENALINE_MOVE = 2;
    private static final int MAX_FRENZY_MOVE = 2;
    private static final int MAX_LIGHT_FRENZY_MOVE = 3;
    private static final int MIN_MOVE = 0;

    private UserPlayer actingPlayer;
    private PlayerPosition movingPos;
    private PossibleAction actionChosen;
    private WeaponCard pickingWeapon;
    private WeaponCard discardingWeapon;
    private EffectRequest pickRequest;

    private SquareType squareType;
    private Square pickingSquare;

    public PickAction(UserPlayer actingPlayer, PlayerPosition movingPos, PossibleAction actionChosen, WeaponCard pickingWeapon, WeaponCard descardingWeapon, EffectRequest pickRequest) {
        this.actingPlayer = actingPlayer;
        this.actionChosen = actionChosen;
        this.pickingWeapon = pickingWeapon;
        this.discardingWeapon = descardingWeapon;
        this.pickRequest = pickRequest;

        if (actingPlayer.getPosition().equals(movingPos) || movingPos == null) {
            this.movingPos = actingPlayer.getPosition();
            pickingSquare = Game.getInstance().getGameMap().getSquare(movingPos);
        } else {
            this.movingPos = movingPos;
            pickingSquare = Game.getInstance().getGameMap().getSquare(movingPos);
        }
        squareType = pickingSquare.getSquareType();
    }


    @Override
    public boolean validate() {
        int movingDistance = actingPlayer.getPosition().distanceOf(movingPos);

        // moving validation
        switch (actionChosen) {
            case MOVE_AND_PICK:
                if (!(movingDistance >= MIN_MOVE && movingDistance <= MAX_NORMAL_MOVE)) {
                    return false;
                }
                break;
            case ADRENALINE_PICK:
                if (!(movingDistance >= MIN_MOVE && movingDistance <= MAX_ADRENALINE_MOVE)) {
                    return false;
                }
                break;
            case FRENZY_PICK:
                if (!(movingDistance >= MIN_MOVE && movingDistance <= MAX_FRENZY_MOVE)) {
                    return false;
                }
                break;
            case LIGHT_FRENZY_PICK:
                if (!(movingDistance >= MIN_MOVE && movingDistance <= MAX_LIGHT_FRENZY_MOVE)) {
                    return false;
                }
                break;
            default:
                throw new IncompatibleActionException(actionChosen);
        }

        // pick validation
        if (squareType == SquareType.TILE) {
            return ((CardSquare) pickingSquare).isAmmoTilePresent();
        } else if (squareType == SquareType.SPAWN) {
            return ((SpawnSquare) pickingSquare).hasWeapon(pickingWeapon);
        } else {
            throw new NullPointerException("You must always have something to pick in a pick Action!");
        }
    }

    @Override
    public void execute() {
        // first I must always move the player
        actingPlayer.changePosition(movingPos.getCoordX(), movingPos.getCoordY());

        // then I can pick depending on the square I now belong to
        if (squareType == SquareType.TILE) {
            ((CardSquare) pickingSquare).pickAmmoTile().giveResources(actingPlayer);
        } else if (squareType == SquareType.SPAWN) {
            // first I have to pay the weapon to take it
            pickingWeapon.payRechargeCost(actingPlayer, pickRequest);

            // then I add the weapon to my hand
            try {
                actingPlayer.addWeapon(pickingWeapon);
            } catch (MaxCardsInHandException e) {
                actingPlayer.addWeapon(pickingWeapon, discardingWeapon);
                discardingWeapon.setStatus(new SemiChargedWeapon());
                ((SpawnSquare) pickingSquare).swapWeapons(discardingWeapon, pickingWeapon);
            }
        } else {
            throw new NullPointerException("A square must have a type!");
        }
    }
}
