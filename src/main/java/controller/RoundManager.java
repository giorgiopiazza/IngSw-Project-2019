package controller;

import enumerations.*;
import exceptions.actions.InvalidActionException;
import exceptions.cards.InvalidPowerupActionException;
import exceptions.cards.WeaponAlreadyChargedException;
import exceptions.cards.WeaponNotChargedException;
import exceptions.game.InvalidGameStateException;
import exceptions.map.InvalidSpawnColorException;
import exceptions.player.EmptyHandException;
import exceptions.player.MaxCardsInHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.actions.*;
import model.cards.PowerupCard;

import model.map.Square;
import model.player.UserPlayer;
import network.message.*;

import java.util.*;

class RoundManager {
    private static final String TAGBACK_GRANADE = "TAGBACK_GRANADE";
    private static final String TELEPORTER = "TELEPORTER";
    private static final String NEWTON = "NEWTON";
    private static final String TARGETING_SCOPE = "TARGETING_SCOPE";

    private Game gameInstance;
    private GameManager gameManager;
    private TurnManager turnManager;

    RoundManager(GameManager gameManager) {
        this.gameInstance = Game.getInstance();
        this.gameManager = gameManager;
    }

    void initTurnManager() {
        this.turnManager = new TurnManager(gameInstance.getPlayers());
    }

    TurnManager getTurnManager() {
        return this.turnManager;
    }


    private void setInitialActions() {
        if (gameInstance.getState() == GameState.NORMAL) {
            ActionManager.setPossibleActions(turnManager.getTurnOwner());
        } else if (gameInstance.getState() == GameState.FINAL_FRENZY) {
            ActionManager.setFrenzyPossibleActions(turnManager.getTurnOwner(), turnManager);
        }
    }

    private void setReloadAction() {
        turnManager.getTurnOwner().setActions(EnumSet.of(PossibleAction.RELOAD));
    }

    Response handleTerminatorFirstSpawn(TerminatorSpawnRequest spawnRequest) {
        if (turnManager.getTurnOwner().getPossibleActions().contains(PossibleAction.SPAWN_TERMINATOR)) {
            // terminator does not still exist!
            try {
                gameInstance.buildTerminator();
                gameInstance.spawnTerminator(gameInstance.getGameMap().getSpawnSquare(spawnRequest.getSpawnColor()));
            } catch (InvalidSpawnColorException e) {
                return buildNegativeResponse("Invalid color for spawning!");
            }
        } else {
            return buildNegativeResponse("Invalid Action ");
        }

        // I pick the two powerups for the first turn of the player an I set his state to FIRST_SPAWN
        pickTwoPowerups();
        turnManager.getTurnOwner().changePlayerState(PossiblePlayerState.FIRST_SPAWN);
        return buildPositiveResponse("Terminator has spawned!");
    }

    Response handleFirstSpawn(DiscardPowerupRequest discardRequest) {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        int firstSpawnPowerup = discardRequest.getPowerup();
        RoomColor spawnColor;

        if (firstSpawnPowerup < 0 || firstSpawnPowerup > 2 || firstSpawnPowerup > turnOwner.getPowerups().length - 1) {
            return buildNegativeResponse("Invalid powerup index  ");
        }

        if (turnOwner.getPossibleActions().contains(PossibleAction.CHOOSE_SPAWN)) {
            try {
                spawnColor = Ammo.toColor(turnOwner.getPowerups()[discardRequest.getPowerup()].getValue());
                turnOwner.discardPowerupByIndex(discardRequest.getPowerup());
            } catch (EmptyHandException e) {
                // never reached, in first spawn state every player always have two powerups!
                return buildNegativeResponse("GAME ERROR");
            }
        } else {
            return buildNegativeResponse("Invalid Action  ");
        }

        // i spawn the turnOwner and then pass the turn picking the powerups for the next player
        try {
            gameInstance.spawnPlayer(turnOwner, gameInstance.getGameMap().getSpawnSquare(spawnColor));
        } catch (InvalidSpawnColorException e) {
            // never reached, a powerup has always a corresponding spawning color!
            // TODO add different kind of color ? SpawnColor ?
        }

        // changing state handling
        if (gameInstance.isTerminatorPresent()) {
            if (turnOwner.isFirstPlayer()) {
                turnManager.nextTurn();
                pickTwoPowerups();
            }
        } else {
            if (turnManager.endOfRound()) {
                turnManager.nextTurn();
                setInitialActions();
                gameManager.changeState(PossibleGameState.GAME_STARTED);
            } else {
                turnManager.nextTurn();
                pickTwoPowerups();
            }
        }
        return buildPositiveResponse("Player spawned with chosen powerup");
    }

    private void afterTerminatorActionHandler(PossibleGameState gameState) {
        if (gameState == PossibleGameState.GAME_READY) {
            if (turnManager.endOfRound()) {
                turnManager.nextTurn();
                setInitialActions();
                gameManager.changeState(PossibleGameState.GAME_STARTED);
            } else {
                turnManager.nextTurn();
                pickTwoPowerups();
            }
        } else if (gameState == PossibleGameState.GAME_STARTED) {
            // if terminator action is done before the 2 actions the game state does not change, otherwise it must be done before passing the turn
            turnManager.getTurnOwner().removeAction(PossibleAction.TERMINATOR_ACTION);
        } else if (gameState == PossibleGameState.MISSING_TERMINATOR_ACTION) {
            if (gameInstance.getState().equals(GameState.NORMAL)) {
                setReloadAction();
                gameManager.changeState(PossibleGameState.ACTIONS_DONE);
            } else if (gameInstance.getState().equals(GameState.FINAL_FRENZY)) {
                gameManager.changeState(PossibleGameState.FRENZY_ACTIONS_DONE);
            } else {
                throw new InvalidGameStateException();
            }
        } else {
            throw new InvalidGameStateException();
        }
    }

    private void handleNextTurnReset() {
        PossibleGameState arrivingState = turnManager.getArrivingGameState();

        if (arrivingState == PossibleGameState.GAME_READY) {
            turnManager.nextTurn();
            if (turnManager.getTurnOwner().isFirstPlayer()) {
                gameManager.changeState(PossibleGameState.GAME_STARTED);
            } else {
                pickTwoPowerups();
            }
        } else {
            gameManager.changeState(handleAfterActionState(turnManager.isSecondAction()));
        }
    }

    void pickTwoPowerups() {
        for (int i = 0; i < 2; ++i) {
            PowerupCard drawnPowerup = (PowerupCard) gameInstance.getPowerupCardsDeck().draw();
            try {
                turnManager.getTurnOwner().addPowerup(drawnPowerup);
            } catch (MaxCardsInHandException e) {
                // nothing to do here, never reached when picking for the first time two powerups!
            }
        }
    }

    Response handleTerminatorAction(UseTerminatorRequest terminatorRequest, PossibleGameState gameState) {
        TerminatorAction terminatorAction;

        if (turnManager.getTurnOwner().getPossibleActions().contains(PossibleAction.TERMINATOR_ACTION)) {
            terminatorAction = new TerminatorAction(turnManager.getTurnOwner(), gameInstance.getUserPlayerByUsername(terminatorRequest.getTargetPlayer()), terminatorRequest.getMovingPosition());
            try {
                if (terminatorAction.validate()) {
                    terminatorAction.execute();
                    turnManager.setDamagedPlayers(new ArrayList<>(List.of(gameInstance.getUserPlayerByUsername(terminatorRequest.getTargetPlayer()))));
                } else {
                    return buildNegativeResponse("Terminator action not valid");
                }
            } catch (InvalidActionException e) {
                return buildNegativeResponse("Invalid Action ");
            }
        } else {
            return buildNegativeResponse("Player can not do this Action");
        }

        if (!turnManager.getDamagedPlayers().isEmpty()) {
            gameManager.changeState(PossibleGameState.GRANADE_USAGE);
            turnManager.giveTurn(turnManager.getDamagedPlayers().get(0));
            turnManager.resetCount();
            turnManager.setArrivingGameState(gameState);
        } else {
            afterTerminatorActionHandler(gameState);
        }

        return buildPositiveResponse("Terminator action used");
    }

    Response handleGranadeUsage(PowerupRequest granadeMessage) {
        PowerupCard chosenGranade;

        if (granadeMessage.getPowerup().get(0) < 0 || granadeMessage.getPowerup().get(0) > turnManager.getTurnOwner().getPowerups().length) {
            return buildNegativeResponse("Invalid Powerup index!");
        }

        if (granadeMessage.getPowerup().size() != 1) {
            return buildNegativeResponse("Too many powerups!");
        }

        chosenGranade = turnManager.getTurnOwner().getPowerups()[granadeMessage.getPowerup().get(0)];

        if (!chosenGranade.getName().equals(TAGBACK_GRANADE)) {
            return buildNegativeResponse("Invalid Powerup");
        }

        try {
            chosenGranade.use(granadeMessage);
        } catch (NotEnoughAmmoException e) {
            // never reached because granade has never a cost
        } catch (InvalidPowerupActionException e) {
            return buildNegativeResponse("Powerup can not be used");
        }

        // after having executed the granade action I discard it from the players hand
        try {
            turnManager.getTurnOwner().discardPowerupByIndex(granadeMessage.getPowerup().get(0));
        } catch (EmptyHandException e) {
            // never reached if the player has the powerup!
        }


        turnManager.increaseCount();
        // if the player is the last one to use the granade I set back the state to the previous one and give the turn to the next player
        if(turnManager.getTurnCount() > turnManager.getDamagedPlayers().size() - 1) {
            handleNextTurnReset();
        }

        // then I give the turn to the next damaged player
        turnManager.giveTurn(turnManager.getDamagedPlayers().get(turnManager.getTurnCount()));

        return buildPositiveResponse("Granade has been Used");
    }

    private Response handleScopeUsage(PowerupRequest scopeMessage) {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        PowerupRequest tempRequest;
        List<Integer> powerupsIndexes = scopeMessage.getPowerup();
        List<Integer> paymentPowerups = scopeMessage.getPaymentPowerups();
        List<String> targets = scopeMessage.getTargetPlayersUsernames();
        int sizeDifference = powerupsIndexes.size() - targets.size();

        for(Integer index : powerupsIndexes) {
            if(index < 0 || index > turnOwner.getPowerups().length -1) {
                return buildNegativeResponse("Invalid Index");
            }
            for(Integer paymentIndex : paymentPowerups) {
                if(index.equals(paymentIndex)) {
                    return buildNegativeResponse("Invalid Payment indexes");
                }
            }
        }

        if(powerupsIndexes.size() > turnOwner.getPowerups().length) {
            return buildNegativeResponse("Too many indexes");
        }

        for(Integer index : powerupsIndexes) {
            if(!turnOwner.getPowerups()[index].getName().equals(TARGETING_SCOPE)) {
                return buildNegativeResponse("Invalid Scope Index");
            }
        }

        switch (sizeDifference) {
            case 0:
                for(int i = 0; i < powerupsIndexes.size(); ++i) {
                    if(!paymentPowerups.isEmpty()) {
                        tempRequest = new PowerupRequest.PowerupRequestBuilder(scopeMessage.getSenderUsername(), new ArrayList<>(List.of(powerupsIndexes.get(i))))
                                                        .paymentPowerups(scopeMessage.getPaymentPowerups())
                                                        .targetPlayersID(new ArrayList<>(List.of(targets.get(i))))
                                                        .build();
                        paymentPowerups.remove(0);
                    } else {
                        tempRequest = new PowerupRequest.PowerupRequestBuilder(scopeMessage.getSenderUsername(), new ArrayList<>(List.of(powerupsIndexes.get(i))))
                                                        .targetPlayersID(new ArrayList<>(List.of(targets.get(i))))
                                                        .build();
                    }
                    try {
                        turnOwner.getPowerups()[i].use(tempRequest);
                        turnOwner.discardPowerupByIndex(i);
                    } catch (NotEnoughAmmoException e) {
                        return buildNegativeResponse("Not Enough Ammo");
                    } catch (InvalidPowerupActionException e) {
                        return buildNegativeResponse(" Invalid Action");
                    } catch (EmptyHandException e) {
                        // can not happen here because powerup is already verified to be possessed
                    }
                }
                return buildPositiveResponse("Targeting Scope used");
            case 1:
                if(powerupsIndexes.size() == 3) {
                    for(int i = 0; i < 2; ++i) {
                        tempRequest = new PowerupRequest.PowerupRequestBuilder(scopeMessage.getSenderUsername(), new ArrayList<>(List.of(powerupsIndexes.get(i))))
                                                        .targetPlayersID(new ArrayList<>(List.of(targets.get(0))))
                                                        .build();
                        try {
                            turnOwner.getPowerups()[i].use(tempRequest);
                            turnOwner.discardPowerupByIndex(i);
                        } catch (NotEnoughAmmoException e) {
                            return buildNegativeResponse("Not Enough Ammo");
                        } catch (EmptyHandException e) {
                            // can not happen here because powerup is already verified to be possessed
                        } catch (InvalidPowerupActionException e) {
                            return buildNegativeResponse(" Invalid Action");
                        }
                    }

                    tempRequest = new PowerupRequest.PowerupRequestBuilder(scopeMessage.getSenderUsername(), new ArrayList<>(List.of(powerupsIndexes.get(2))))
                                                    .targetPlayersID(new ArrayList<>(List.of(targets.get(1))))
                                                    .build();
                    try {
                        turnOwner.getPowerups()[2].use(tempRequest);
                        turnOwner.discardPowerupByIndex(2);
                    } catch (NotEnoughAmmoException e) {
                        return buildNegativeResponse("Not Enough Ammo ");
                    } catch (InvalidPowerupActionException e) {
                        return buildNegativeResponse("Invalid Action  ");
                    } catch (EmptyHandException e) {
                        // can not happen here because powerup is already verified to be possessed
                    }
                } else if(powerupsIndexes.size() == 2) {
                    for(int i = 0; i < 2; ++i) {
                        if(!paymentPowerups.isEmpty()) {
                            tempRequest = new PowerupRequest.PowerupRequestBuilder(scopeMessage.getSenderUsername(), new ArrayList<>(List.of(powerupsIndexes.get(i))))
                                    .paymentPowerups(scopeMessage.getPaymentPowerups())
                                    .targetPlayersID(new ArrayList<>(List.of(targets.get(0))))
                                    .build();
                            paymentPowerups.remove(0);
                        } else {
                            tempRequest = new PowerupRequest.PowerupRequestBuilder(scopeMessage.getSenderUsername(), new ArrayList<>(List.of(powerupsIndexes.get(i))))
                                    .targetPlayersID(new ArrayList<>(List.of(targets.get(0))))
                                    .build();
                        }
                        try {
                            turnOwner.getPowerups()[i].use(tempRequest);
                            turnOwner.discardPowerupByIndex(i);
                        } catch (NotEnoughAmmoException e) {
                            return buildNegativeResponse("Not Enough Ammo ");
                        } catch (InvalidPowerupActionException e) {
                            return buildNegativeResponse("Invalid  Action");
                        } catch (EmptyHandException e) {
                            // cn not happen here because powerup is already verified to be possessed
                        }
                    }
                }
                return buildPositiveResponse("Targeting Scopes Used");
            case 2:
                for(int i = 0; i < 3; ++i) {
                    tempRequest = new PowerupRequest.PowerupRequestBuilder(scopeMessage.getSenderUsername(), new ArrayList<>(List.of(powerupsIndexes.get(i))))
                            .targetPlayersID(new ArrayList<>(List.of(targets.get(0))))
                            .build();
                    try {
                        turnOwner.getPowerups()[i].use(tempRequest);
                        turnOwner.discardPowerupByIndex(i);
                    } catch (EmptyHandException e) {
                        // can not happen here because powerup is already verified to be possessed
                    } catch (NotEnoughAmmoException e) {
                        return buildNegativeResponse("Not Enough Ammo  ");
                    } catch (InvalidPowerupActionException e) {
                        return buildNegativeResponse("Invalid  Action ");
                    }
                }

                return buildPositiveResponse("Targeting Scope Used");
            default:
                return buildNegativeResponse(" Invalid Action ");
        }
    }

    Response handlePowerupAction(PowerupRequest powerupRequest) {
        PowerupCard chosenPowerup;

        if (powerupRequest.getPowerup().size() != 1) {
            return buildNegativeResponse("Too many powerups!");
        }

        if (powerupRequest.getPowerup().get(0) < 0 || powerupRequest.getPowerup().get(0) > turnManager.getTurnOwner().getPowerups().length) {
            return buildNegativeResponse("Invalid Powerup index!");
        }

        chosenPowerup = turnManager.getTurnOwner().getPowerups()[powerupRequest.getPowerup().get(0)];

        if (!chosenPowerup.getName().equals(NEWTON) || !chosenPowerup.getName().equals(TELEPORTER)) {
            return buildNegativeResponse("Invalid Powerup");
        }

        try {
            chosenPowerup.use(powerupRequest);
        } catch (NotEnoughAmmoException e) {
            // never reached because neither newton nor teleporter need a cost
        } catch (InvalidPowerupActionException e) {
            return buildNegativeResponse("Powerup can not be used");
        }

        // after having used the powerup I discard it from the players hand
        try {
            turnManager.getTurnOwner().discardPowerupByIndex(powerupRequest.getPowerup().get(0));
        } catch (EmptyHandException e) {
            // never reached if the player has the powerup!
        }

        return buildPositiveResponse("Powerup has been used");
    }

    Response handleMoveAction(MoveRequest moveRequest, boolean secondAction) {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        PossibleAction actionType;
        MoveAction moveAction;

        if (turnOwner.getPossibleActions().contains(PossibleAction.MOVE)) {
            actionType = PossibleAction.MOVE;
        } else if (turnOwner.getPossibleActions().contains(PossibleAction.FRENZY_MOVE)) {
            actionType = PossibleAction.FRENZY_MOVE;
        } else {
            return buildNegativeResponse("Player can not do this action ");
        }

        // first I build the MoveAction
        moveAction = new MoveAction(turnOwner, moveRequest.getSenderMovePosition(), actionType);

        try {
            if (moveAction.validate()) {
                moveAction.execute();
            } else {
                return buildNegativeResponse("Invalid Move action");
            }
        } catch (InvalidActionException e) {
            return buildNegativeResponse("Invalid Move action");
        }

        gameManager.changeState(handleAfterActionState(secondAction));
        return buildPositiveResponse("Move action done");
    }

    Response handlePickAction(MovePickRequest pickRequest, boolean secondAction) {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        PickAction pickAction;
        Set<PossibleAction> ownersActions = turnOwner.getPossibleActions();
        PossibleAction actionType;
        Square movingSquare;

        if (ownersActions.contains(PossibleAction.MOVE_AND_PICK)) {
            actionType = PossibleAction.MOVE_AND_PICK;
        } else if (ownersActions.contains(PossibleAction.ADRENALINE_PICK)) {
            actionType = PossibleAction.ADRENALINE_PICK;
        } else if (ownersActions.contains(PossibleAction.FRENZY_PICK)) {
            actionType = PossibleAction.FRENZY_PICK;
        } else if (ownersActions.contains(PossibleAction.LIGHT_FRENZY_PICK)) {
            actionType = PossibleAction.LIGHT_FRENZY_PICK;
        } else {
            return buildNegativeResponse("Player can not do this action");
        }

        // first I understand if the moving square is a spawn or a tile one then I build the relative pick action
        movingSquare = gameInstance.getGameMap().getSquare(pickRequest.getSenderMovePosition());

        if (movingSquare.getSquareType() == SquareType.TILE) {
            pickAction = new PickAction(turnOwner, actionType, pickRequest);
        } else if (movingSquare.getSquareType() == SquareType.SPAWN) {
            pickAction = new PickAction(turnOwner, actionType, pickRequest);
        } else {
            throw new NullPointerException("A square must always have a type!");
        }

        // now I can try to validate and use the action
        try {
            if (pickAction.validate()) {
                pickAction.execute();
            } else {
                return buildNegativeResponse("Invalid Pick Action");
            }
        } catch (InvalidActionException e) {
            return buildNegativeResponse("Invalid Pick Action");
        }

        gameManager.changeState(handleAfterActionState(secondAction));
        return buildPositiveResponse("Pick Action done");
    }

    Response handleShootAction(ShootRequest shootRequest, PowerupRequest scopeRequest, boolean secondAction) {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        Response response;
        ShootAction shootAction;
        PossibleAction actionType;
        Set<PossibleAction> ownersActions = turnOwner.getPossibleActions();

        if (ownersActions.contains(PossibleAction.SHOOT)) {
            actionType = PossibleAction.SHOOT;
        } else if (ownersActions.contains(PossibleAction.ADRENALINE_SHOOT)) {
            actionType = PossibleAction.ADRENALINE_SHOOT;
        } else if (ownersActions.contains(PossibleAction.FRENZY_SHOOT)) {
            actionType = PossibleAction.FRENZY_SHOOT;
        } else if (ownersActions.contains(PossibleAction.LIGHT_FRENZY_SHOOT)) {
            actionType = PossibleAction.LIGHT_FRENZY_SHOOT;
        } else {
            return buildNegativeResponse("Player can not do this action");
        }

        // first I can build the shoot action
        shootAction = new ShootAction(turnOwner, actionType, shootRequest);

        // now I can try to validate and use the action, care, a shoot action can throw different exceptions, each will be returned with a different response
        try {
            if (shootAction.validate()) {
                List<UserPlayer> beforeShootPlayers = gameInstance.getPlayers();
                shootAction.execute();
                turnManager.setDamagedPlayers(buildDamagedPlayers(beforeShootPlayers));
            } else {
                return buildNegativeResponse("Invalid Shoot Action");
            }
        } catch (WeaponAlreadyChargedException e) {
            return buildNegativeResponse("Trying to recharge an already charged weapon with frenzy shoot");
        } catch (NotEnoughAmmoException e) {
            return buildNegativeResponse("Not enough ammo to recharge a weapon with frenzy shoot");
        } catch (WeaponNotChargedException e) {
            return buildNegativeResponse("Not charged weapon can not be used to shoot");
        } catch (InvalidActionException e) {
            return buildNegativeResponse("Invalid Shoot Action");
        }

        // targeting scope handler
        if (scopeRequest != null) {
            response = handleScopeUsage(scopeRequest);
            if(response.getStatus() == MessageStatus.ERROR) {
                return response;
            } // else targeting scope can be used and granade usage is handled
        }

        // tagback granade handler
        if (!turnManager.getDamagedPlayers().isEmpty()) {
            gameManager.changeState(PossibleGameState.GRANADE_USAGE);
            turnManager.giveTurn(turnManager.getDamagedPlayers().get(0));
            turnManager.resetCount();
            turnManager.setSecondAction(secondAction);
        } else {
            gameManager.changeState(handleAfterActionState(secondAction));
        }

        return buildPositiveResponse("Shoot Action done");
    }

    Response handleReloadAction(ReloadRequest reloadRequest) {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        ReloadAction reloadAction;

        if(turnOwner.getPossibleActions().contains(PossibleAction.RELOAD)) {
            reloadAction = new ReloadAction(turnOwner, reloadRequest);
        } else {
            return buildNegativeResponse("Invalid Action");
        }

        try {
            if(reloadAction.validate()) {
                reloadAction.execute();
            } else {
                return buildNegativeResponse("Invalid Action");
            }
        } catch (WeaponAlreadyChargedException e) {
            return buildNegativeResponse("You are trying to recharge a weapon that is already charged");
        } catch (NotEnoughAmmoException e) {
            return buildNegativeResponse("Not enough ammo to reload");
        }

        // after a reload action a player always passes his turn and the game has to manage the death players
        deathPlayersHandler(PossibleGameState.PASS_NORMAL_TURN);
        return buildPositiveResponse("Reload Action done");
    }

    private ArrayList<UserPlayer> buildDamagedPlayers(List<UserPlayer> beforeShootPlayers) {
        ArrayList<UserPlayer> reallyDamagedPlayers = new ArrayList<>();

        for (UserPlayer afterPlayer : gameInstance.getPlayers()) {
            for (UserPlayer beforePlayer : beforeShootPlayers) {
                if (afterPlayer.equals(beforePlayer) && afterPlayer.getPlayerBoard().getDamageCount() > beforePlayer.getPlayerBoard().getDamageCount()) {
                    reallyDamagedPlayers.add(afterPlayer);
                }
            }
        }

        return reallyDamagedPlayers;
    }

    Response handlePassAction() {
        if(gameInstance.getState() == GameState.NORMAL) {
            return deathPlayersHandler(PossibleGameState.PASS_NORMAL_TURN);
        } else if(gameInstance.getState() == GameState.FINAL_FRENZY) {
            if(turnManager.getTurnOwner().equals(turnManager.getLastPlayer())) {
                // if reached, game has ended, last remaining points are calculated and a winner is declared!
                gameManager.endGame();
                return buildPositiveResponse("Turn passed and GAME HAS ENDED");
            }
            return deathPlayersHandler(PossibleGameState.PASS_FRENZY_TURN);
        } else {
            throw new InvalidGameStateException();
        }
    }

    private Response deathPlayersHandler(PossibleGameState nextPassState) {
        ArrayList<UserPlayer> deathPlayers = gameInstance.getDeathPlayers();

        if(gameInstance.isTerminatorPresent() && gameInstance.getTerminator().getPlayerBoard().getDamageCount() > 10) {
            // first of all I control if the current player has done a double kill
            if(!gameInstance.getDeathPlayers().isEmpty()) {
                turnManager.getTurnOwner().addPoints(1);
            }

            turnManager.setArrivingGameState(nextPassState);
            gameManager.changeState(PossibleGameState.TERMINATOR_RESPAWN);
            return buildPositiveResponse("Terminator has died respawn him before passing");
        } else if(!gameInstance.getDeathPlayers().isEmpty()) {
            // first of all I control if the current player has done a double kill
            if(gameInstance.getDeathPlayers().size() > 1) {
                turnManager.getTurnOwner().addPoints(1);
            }

            // there are death players I set everything I need to respawn them
            turnManager.setDeathPlayers(deathPlayers);
            gameManager.changeState(PossibleGameState.MANAGE_DEATHS);
            turnManager.giveTurn(deathPlayers.get(0));
            turnManager.getTurnOwner().setSpawningCard((PowerupCard) gameInstance.getPowerupCardsDeck().draw());
            turnManager.resetCount();
            turnManager.setArrivingGameState(nextPassState);
            return buildPositiveResponse("Turn passed");
        } else {
            // if no players have died the GameState remains the same
            return handleNextTurn(nextPassState);
        }
    }

    Response handleTerminatorRespawn(TerminatorSpawnRequest respawnRequest) {
        ArrayList<UserPlayer> deathPlayers = gameInstance.getDeathPlayers();

        try {
            gameInstance.spawnTerminator(gameInstance.getGameMap().getSpawnSquare(respawnRequest.getSpawnColor()));
        } catch (InvalidSpawnColorException e) {
            return buildNegativeResponse("Invalid Color for Spawning");
        }

        if(!gameInstance.getDeathPlayers().isEmpty()) {
            // there are death players: I set everything I need to respawn them
            turnManager.setDeathPlayers(deathPlayers);
            gameManager.changeState(PossibleGameState.MANAGE_DEATHS);
            turnManager.giveTurn(deathPlayers.get(0));
            deathPlayers.get(0).setSpawningCard((PowerupCard) gameInstance.getPowerupCardsDeck().draw());
            turnManager.resetCount();
            return buildPositiveResponse("Turn passed after Spawning the Terminator");
        } else {
            if(gameInstance.remainingSkulls() == 1) {   // last skull is going to be removed, frenzy mode has to be activated
                turnManager.setFrenzyPlayers();
                turnManager.setLastPlayer();
                return handleNextTurn(PossibleGameState.PASS_FRENZY_TURN);
            } else {
                return handleNextTurn(turnManager.getArrivingGameState());
            }
        }
    }

    Response handlePlayerRespawn(DiscardPowerupRequest respawnRequest) {
        UserPlayer turnOwner = turnManager.getTurnOwner();
        PowerupCard spawnPowerup;
        RoomColor spawnColor;
        int powerupIndex = respawnRequest.getPowerup();

        // if powerupIndex is 3 means that the player wants to respawn with the drawn powerup, otherwise with the specified one
        if(powerupIndex < 0 || powerupIndex > 3) {
            return buildNegativeResponse("Invalid powerup index");
        }

        if(powerupIndex != 3 && powerupIndex > turnOwner.getPowerups().length - 1) {
            return buildNegativeResponse("Invalid powerup index");
        }

        if(powerupIndex == 3) {
            spawnPowerup = turnOwner.getSpawningCard();
            turnOwner.setSpawningCard(null);
        } else {
            spawnPowerup = turnOwner.getPowerups()[powerupIndex];
            try {
                turnOwner.discardPowerup(spawnPowerup);
            } catch (EmptyHandException e) {
                // can never occur! We have just verified that the turnOwner has this powerup!
            }
        }
        spawnColor = Ammo.toColor(spawnPowerup.getValue());

        // now that I know the color of the spawning square I can respawn the player
        try {
            gameInstance.spawnPlayer(turnOwner, gameInstance.getGameMap().getSpawnSquare(spawnColor));
        } catch (InvalidSpawnColorException e) {
            // never reached, a powerup has always a corresponding spawning color!
            // TODO add different kind of color ? SpawnColor ?
        }

        // now I have to pass the turn to the next death player and pick a card for him
        turnManager.increaseCount();
        if(turnManager.getTurnCount() > turnManager.getDeathPlayers().size() -1) {
            if((turnManager.getDeathPlayers().size() == 1 && gameInstance.remainingSkulls() == 1) || gameInstance.remainingSkulls() == 0) {   // last skull is going to be removed, frenzy mode has to be activated
                turnManager.setFrenzyPlayers();
                turnManager.setLastPlayer();
                return handleNextTurn(PossibleGameState.PASS_FRENZY_TURN);
            } else {
                return handleNextTurn(turnManager.getArrivingGameState());
            }
        }

        // otherwise there are still death players and the next one has to respawn
        turnManager.giveTurn(turnManager.getDeathPlayers().get(turnManager.getTurnCount()));
        turnManager.getTurnOwner().setSpawningCard((PowerupCard) gameInstance.getPowerupCardsDeck().draw());

        return buildPositiveResponse("Player Respawned");
    }

    private PossibleGameState handleAfterActionState(boolean secondAction) {
        if (!secondAction) {
            return PossibleGameState.SECOND_ACTION;
        } else {
            if (turnManager.getTurnOwner().getPossibleActions().contains(PossibleAction.TERMINATOR_ACTION)) {
                return PossibleGameState.MISSING_TERMINATOR_ACTION;
            } else {
                if (gameInstance.getState().equals(GameState.NORMAL)) {
                    setReloadAction();
                    return PossibleGameState.ACTIONS_DONE;
                } else if (gameInstance.getState().equals(GameState.FINAL_FRENZY)) {
                    return PossibleGameState.FRENZY_ACTIONS_DONE;
                } else {
                    throw new InvalidGameStateException();
                }
            }
        }
    }


    private Response handleNextTurn(PossibleGameState arrivingState) {
        turnManager.nextTurn();
        setInitialActions();

        if (arrivingState == PossibleGameState.PASS_NORMAL_TURN) {
            gameManager.changeState(PossibleGameState.GAME_STARTED);
            return buildPositiveResponse("Turn Passed");
        } else if (arrivingState == PossibleGameState.PASS_FRENZY_TURN) {
            gameManager.changeState(PossibleGameState.FINAL_FRENZY);
            return buildPositiveResponse("Turn Passed");

        } else {
            throw new InvalidGameStateException();
        }
    }

    private Response buildPositiveResponse(String reason) {
        return new Response(reason, MessageStatus.OK);
    }

    private Response buildNegativeResponse(String reason) {
        return new Response(reason, MessageStatus.ERROR);
    }
}
