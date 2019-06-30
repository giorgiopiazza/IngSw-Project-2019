package model.actions;

import enumerations.PossibleAction;
import enumerations.SquareType;
import exceptions.actions.IncompatibleActionException;
import exceptions.actions.InvalidActionException;
import exceptions.actions.WeaponChargementException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.player.MaxCardsInHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.cards.WeaponCard;
import model.cards.weaponstates.SemiChargedWeapon;
import model.map.CardSquare;
import model.map.SpawnSquare;
import model.map.Square;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.ActionRequest;
import network.message.MovePickRequest;
import utility.InputValidator;

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
    private ActionRequest pickRequest;

    private SquareType squareType;
    private Square pickingSquare;

    public PickAction(UserPlayer actingPlayer, PossibleAction actionChosen, MovePickRequest pickRequest) {
        // verify that if a parameter in the request is null then the game crashes!
        this.actingPlayer = actingPlayer;
        this.movingPos = pickRequest.getSenderMovePosition();
        this.actionChosen = actionChosen;
        this.pickingWeapon = pickRequest.getAddingWeapon();
        this.discardingWeapon = pickRequest.getDiscardingWeapon();
        this.pickRequest = pickRequest;

        if (movingPos == null) {
            this.movingPos = actingPlayer.getPosition();
            pickingSquare = Game.getInstance().getGameMap().getSquare(movingPos);
        } else {
            pickingSquare = Game.getInstance().getGameMap().getSquare(movingPos);
        }
        squareType = pickingSquare.getSquareType();
    }


    @Override
    public boolean validate() throws InvalidActionException {
        if(!InputValidator.validatePosition(movingPos)) {
            throw new InvalidActionException();
        }

        if(!InputValidator.validateIndexes(pickRequest, actingPlayer)) {
            throw new InvalidActionException();
        }

        int movingDistance = actingPlayer.getPosition().distanceOf(movingPos);
        int maxMove;

        // Moving validation
        switch (actionChosen) {
            case MOVE_AND_PICK:
                maxMove = MAX_NORMAL_MOVE;
                break;
            case ADRENALINE_PICK:
                maxMove = MAX_ADRENALINE_MOVE;
                break;
            case FRENZY_PICK:
                maxMove = MAX_FRENZY_MOVE;
                break;
            case LIGHT_FRENZY_PICK:
                maxMove = MAX_LIGHT_FRENZY_MOVE;
                break;
            default:
                throw new IncompatibleActionException(actionChosen);
        }

        if (!(movingDistance >= MIN_MOVE && movingDistance <= maxMove)) {
            return false;
        }

        // pick validation
        return pickValidation();
    }

    @Override
    public void execute() {
        // first I must always move the player
        actingPlayer.changePosition(movingPos.getRow(), movingPos.getColumn());

        // then I can pick depending on the square I now belong to
        if (squareType == SquareType.TILE) {
            ((CardSquare) pickingSquare).pickAmmoTile().giveResources(actingPlayer);
        } else if (squareType == SquareType.SPAWN) {
            try {
                // weapon is already payed in the validation
                // then I add the weapon to my hand
                actingPlayer.addWeapon(pickingWeapon);
                ((SpawnSquare) pickingSquare).removeWeapon(pickingWeapon);
            } catch (MaxCardsInHandException e) {
                actingPlayer.addWeapon(pickingWeapon, discardingWeapon);
                discardingWeapon.setStatus(new SemiChargedWeapon());
                ((SpawnSquare) pickingSquare).swapWeapons(discardingWeapon, pickingWeapon);
            }
        } else {
            throw new NullPointerException("A square must have a type!");
        }
    }

    private boolean pickValidation() {
        if (squareType == SquareType.TILE) {
            return ((CardSquare) pickingSquare).isAmmoTilePresent();
        } else if (squareType == SquareType.SPAWN && pickingWeapon != null) {
            try {
                pickingWeapon.payRechargeCost(pickRequest);
            } catch (WeaponAlreadyChargedException e) {
                // this is never reached because weapons on the map are never already charged!
                throw new WeaponChargementException();
            } catch (NotEnoughAmmoException e) {
                return false;
            }

            if(actingPlayer.getWeapons().length == 3 && discardingWeapon == null) {
                return false;
            }
            return ((SpawnSquare) pickingSquare).hasWeapon(pickingWeapon);
        } else {
            throw new NullPointerException("You must always have something to pick in a pick Action!");
        }
    }
}
