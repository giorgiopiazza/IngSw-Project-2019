package controller;

import enumerations.*;
import exceptions.actions.InvalidActionException;
import exceptions.actions.MissingActionException;
import exceptions.game.InexistentColorException;
import exceptions.game.InvalidGameStateException;
import exceptions.player.MaxCardsInHandException;
import exceptions.playerboard.NotEnoughAmmoException;
import model.Game;
import model.actions.*;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.SpawnSquare;
import model.map.Square;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.*;

import java.util.*;

public class RoundManager {
    private static final String TAGBACK_GRANADE = "TAGBACK_GRANADE";
    private static final String TELEPORTER = "TELEPORTER";
    private static final String NEWTON = "NEWTON";
    private static final String TARGETING_SCOPE = "TARGETING_SCOPE";
    private Game gameInstance;
    private TurnManager turnManager;

    public RoundManager(Game gameInstance) {
        this.gameInstance = gameInstance;
    }

    public void initTurnManager() {
        this.turnManager = new TurnManager(gameInstance.getPlayers());
    }

    public void setInitialActions() {
        if (gameInstance.getState() == GameState.NORMAL) {
            ActionManager.setPossibleActions(turnManager.getTurnOwner());
        } else if (gameInstance.getState() == GameState.FINAL_FRENZY) {
            ActionManager.setFrenzyPossibleActions(turnManager.getTurnOwner(), turnManager.getFrenzyActivator());
        }
    }

    public void removeTerminatorAction() {
        turnManager.getTurnOwner().getPossibleActions().remove(PossibleAction.TERMINATOR_ACTION);
    }

    public void setReloadAction() {
        turnManager.getTurnOwner().setActions(EnumSet.of(PossibleAction.RELOAD));
    }

    public void addDoubleKillPoint() {
        turnManager.getTurnOwner().addPoints(1);
    }

    public PossibleGameState handleDecision(PossibleGameState changingState) {
        Scanner in = new Scanner(System.in);

        System.out.println("Choose what you want to do (POWERUP/ACTION) >>> ");

        for (; ; ) {
            String decision = in.nextLine();
            if (decision.equalsIgnoreCase("powerup")) {
                return handlePowerupAction(changingState);
            } else if (decision.equalsIgnoreCase("action")) {
                if (changingState == PossibleGameState.GAME_STARTED) {
                    return handlePlayerAction(false, changingState);
                } else if (changingState == PossibleGameState.SECOND_ACTION) {
                    return handlePlayerAction(true, changingState);
                } else {
                    throw new InvalidGameStateException();
                }
            }
        }
    }

    public PossibleGameState handleReloadDecision(PossibleGameState changingState) {
        Scanner in = new Scanner(System.in);

        System.out.println("Choose what you want to do (POWERUP/RELOAD/PASS), remember you are not obliged to reload your weapons >>> ");

        for (; ; ) {
            String decision = in.nextLine();
            if (decision.equalsIgnoreCase("powerup")) {
                return handlePowerupAction(changingState);
            } else if (decision.equalsIgnoreCase("reload")) {
                if (handleReloadAction(turnManager.getTurnOwner())) {
                    return PossibleGameState.PASS_TURN;
                } else {
                    return changingState;
                }
            }
        }
    }

    public void handleFirstRound() {
        for (int i = 0; i < gameInstance.getPlayers().size(); ++i) {
            UserPlayer currentPlayer = turnManager.getTurnOwner();
            Set<PossibleAction> currentPlayerActions = currentPlayer.getPossibleActions();
            System.out.println(currentPlayer.getUsername() + " it's your first turn!");

            if (currentPlayer.getPlayerState() == PossiblePlayerState.FIRST_SPAWN) {
                if (currentPlayerActions.contains(PossibleAction.CHOOSE_SPAWN)) {
                    boolean terminator = false;
                    if (currentPlayerActions.contains(PossibleAction.SPAWN_TERMINATOR)) {
                        System.out.println(currentPlayer.getUsername() + " you are the first player and then you must spawn the terminator first! \n");
                        terminator = true;
                    }
                    chooseSpawn(currentPlayer, terminator);
                }

                if (currentPlayerActions.contains(PossibleAction.TERMINATOR_ACTION)) {
                    System.out.println("Now the terminator action has to be done: \n");
                    handleTerminatorAction(currentPlayer);
                }
            }

            turnManager.nextTurn();
        }

        GameManager.changeState(PossibleGameState.GAME_STARTED);
    }

    private PossibleGameState handlePlayerAction(boolean secondAction, PossibleGameState changingState/* ,message */) {
        Scanner in = new Scanner(System.in);
        UserPlayer turnOwner = turnManager.getTurnOwner();

        // il seguente case sarà fatto dal tipo di messaggio ricevuto
        System.out.println("> " + turnOwner.getUsername() + " chose the action you want to do >>> ");
        String actionChosen;
        for (; ; ) {
            actionChosen = in.nextLine();
            if (turnOwner.hasAction(actionChosen)) {
                break;
            } else {
                // ask for a new action
                System.out.println("Invalid action choose a new one >>> ");
            }
        }

        switch (actionChosen) {
            case ("MOVE"):
                if (handleMoveAction(turnOwner)) {
                    return handleAfterActionState(secondAction);
                } else {
                    return changingState;
                }
            case ("PICK"):
                if (handlePickAction(turnOwner)) {
                    return handleAfterActionState(secondAction);
                } else {
                    return changingState;
                }
            case ("SHOOT"):
                if (handleShootAction(turnOwner)) {
                    return handleAfterShootChangings(secondAction);
                } else {
                    return changingState;
                }
            case ("TERMINATOR"):
                handleTerminatorAction(turnOwner);
                return handleAfterShootChangings(secondAction);

            default:
                // no action recognised, nothing will be executed
                return PossibleGameState.GAME_STARTED;
        }
    }

    private PossibleGameState handleAfterShootChangings(boolean secondAction) {
        if (!turnManager.getDamagedPlayers().isEmpty()) {
            handleTagBackGranadeUsage(turnManager.getDamagedPlayers(), turnManager.getTurnOwner());
        }

        return handleAfterActionState(secondAction);
    }

    private PossibleGameState handleAfterActionState(boolean secondAction) {
        if (!secondAction) {
            return PossibleGameState.SECOND_ACTION;
        } else {
            return PossibleGameState.ACTIONS_DONE;
        }
    }

    private PossibleGameState handlePowerupAction(PossibleGameState changingState) {
        Scanner in = new Scanner(System.in);
        UserPlayer turnOwner = turnManager.getTurnOwner();
        PowerupRequest powerupRequest;
        PowerupCard chosenPowerup;
        String decision;
        int powerupChosen;


        if (turnOwner.getPowerups().length == 0) {
            System.out.println("You have no usable powerups! ");
            return changingState;
        }

        for (PowerupCard powerup : turnOwner.getPowerups()) {
            if (powerup.getName().equalsIgnoreCase(NEWTON) || powerup.getName().equalsIgnoreCase(TELEPORTER)) {
                System.out.println(powerup.toString());
            }
        }

        System.out.println("Choose the powerup you want to use (NEWTON/TELEPORTER) >>> ");
        for (; ; ) {
            decision = in.nextLine();
            if (decision.equalsIgnoreCase(NEWTON)) {
                powerupChosen = getChosenPowerup(turnOwner, NEWTON);
                if (powerupChosen != -1) {
                    break;
                }
            } else if (decision.equalsIgnoreCase(TELEPORTER)) {
                powerupChosen = getChosenPowerup(turnOwner, TELEPORTER);
                if (powerupChosen != -1) {
                    break;
                }
            } else {
                // wrong powerup will be chosen again
            }
        }

        powerupRequest = new PowerupRequest.PowerupRequestBuilder(turnOwner.getUsername(), powerupChosen).build();
        chosenPowerup = turnOwner.getPowerups()[powerupRequest.getPowerup()];
        if (chosenPowerup.getName().equals(decision)) {
            try {
                chosenPowerup.use(powerupRequest);
            } catch (NotEnoughAmmoException e) {
                // never reached, NEWTON and TELEPORTER never cost anything
            }
        }

        return changingState;
    }

    private int getChosenPowerup(UserPlayer player, String powerupChosen) {
        Scanner in = new Scanner(System.in);
        String colorChosen;

        if (player.getPowerupOccurrences(powerupChosen) == 1) {
            try {
                return player.getPowerupByName(powerupChosen, null);
            } catch (InexistentColorException e) {
                // never reached if color is null
            }
        } else if (player.getPowerupOccurrences(powerupChosen) > 1) {
            for (; ; ) {
                System.out.println("Choose the color of the " + powerupChosen + " you want to use, remember powerups have only the colors BLUE,RED,YELLOW >>> ");
                colorChosen = in.nextLine();
                try {
                    return player.getPowerupByName(powerupChosen, Ammo.getColor(colorChosen));
                } catch (InexistentColorException e) {
                    // color chosen is invalid and will be asked again
                }
            }
        }

        return -1;
    }

    private void spawnTerminator() {
        Scanner in = new Scanner(System.in);
        RoomColor colorChosen;
        for (; ; ) {
            System.out.println("Choose the color of the spawning point where to spawn the terminator \n\n");
            System.out.println("Provide the color >>> ");

            try {
                colorChosen = RoomColor.getColor(in.nextLine());
                gameInstance.buildTerminator();
                gameInstance.spawnTerminator(gameInstance.getGameMap().getSpawnSquare(colorChosen));
                break;
            } catch (Exception e) {
                // wrong color is asked again
            }
        }
    }

    public void handleTerminatorRespawn() {
        Scanner in = new Scanner(System.in);
        RoomColor colorChosen;

        for (; ; ) {
            System.out.println("Choose the color of the spawning point where to spawn the terminator \n\n");
            System.out.println("Provide the color >>> ");

            try {
                colorChosen = RoomColor.getColor(in.nextLine());
                gameInstance.spawnTerminator(gameInstance.getGameMap().getSpawnSquare(colorChosen));
                break;
            } catch (Exception e) {
                // wrong color is asked again
            }
        }
    }

    private void chooseSpawn(UserPlayer spawningPlayer, boolean terminator) {
        Scanner in = new Scanner(System.in);
        List<PowerupCard> twoDrawn = new ArrayList<>();
        String spawningPowerup;
        RoomColor spawnColor;

        for (int i = 0; i < 2; ++i) {
            PowerupCard cardDrawn = (PowerupCard) gameInstance.getPowerupCardsDeck().draw();
            twoDrawn.add(cardDrawn);
        }

        System.out.println("You have drawn these two powerups: \n");
        System.out.println("First powerup is: " + twoDrawn.get(0).toString());
        System.out.println("Second powerup is: " + twoDrawn.get(1).toString());

        if (terminator) {
            spawnTerminator();
        }

        System.out.println("Choose the powerup you want to spawn from, the other will be added to your hand \n");

        for (; ; ) {
            System.out.println("You want to spawn with the first or the second powerup ? (first/second) ");
            spawningPowerup = in.nextLine();

            if (spawningPowerup.equals("first")) {
                try {
                    spawningPlayer.addPowerup(twoDrawn.get(1));
                } catch (MaxCardsInHandException e) {
                    // crash
                }
                spawnColor = Ammo.toColor(twoDrawn.get(0).getValue());
                break;
            } else if (spawningPowerup.equals("second")) {
                try {
                    spawningPlayer.addPowerup(twoDrawn.get(0));
                } catch (MaxCardsInHandException e) {
                    // crash
                }
                spawnColor = Ammo.toColor(twoDrawn.get(1).getValue());
                break;
            } else {
                // typo or not accepted command will be asked again
            }

        }

        gameInstance.spawnPlayer(spawningPlayer, gameInstance.getGameMap().getSpawnSquare(spawnColor));
        spawningPlayer.changePlayerState(PossiblePlayerState.WAITING_TO_PLAY);
    }

    public void handlePlayerRespawn(UserPlayer player) {
        // TODO care the fourth powerup picked!
    }

    private void handleTagBackGranadeUsage(ArrayList<UserPlayer> damaged, UserPlayer turnOwner) {
        ArrayList<UserPlayer> granadePossessors = new ArrayList<>();
        PowerupCard chosenPowerup;
        PowerupRequest powerupRequest;
        int granadeChosen;
        for (UserPlayer player : damaged) {
            for (PowerupCard powerup : player.getPowerups()) {
                if (powerup.getName().equals(TAGBACK_GRANADE)) {
                    granadePossessors.add(player);
                }
            }
        }

        granadePossessors = granadePossessors.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        for (UserPlayer player : granadePossessors) {
            System.out.println(player.getUsername() + " do you want to use your granade to damage: " + turnOwner.getUsername() + " ? (yes/no) ");
            granadeChosen = getTagBackGranate(player);
            if (granadeChosen != -1) {
                powerupRequest = new PowerupRequest.PowerupRequestBuilder(player.getUsername(), granadeChosen).build();
                chosenPowerup = player.getPowerups()[powerupRequest.getPowerup()];
                if (chosenPowerup.getName().equals(TAGBACK_GRANADE)) {
                    try {
                        chosenPowerup.use(powerupRequest);
                    } catch (NotEnoughAmmoException e) {
                        // Can't happen with Tagback granade
                    }
                }
            }
        }
    }

    private int getTagBackGranate(UserPlayer player) {
        Scanner in = new Scanner(System.in);
        String colorChosen;

        for (; ; ) {
            String decision = in.nextLine();
            if (decision.equals("yes")) {
                if (player.getPowerupOccurrences(TAGBACK_GRANADE) > 1) {
                    for (; ; ) {
                        System.out.println("Choose the color of the granade you want to use, remember powerups have only the colors BLUE,RED,YELLOW >>> ");
                        colorChosen = in.nextLine();
                        try {
                            return player.getPowerupByName(TAGBACK_GRANADE, Ammo.getColor(colorChosen));
                        } catch (InexistentColorException e) {
                            // color chosen is invalid and will be asked again
                        }
                    }
                } else {
                    try {
                        return player.getPowerupByName(TAGBACK_GRANADE, null);
                    } catch (InexistentColorException e) {
                        // color chosen is invalid and will be asked again
                    }
                }
            } else if (decision.equals("no")) {
                return -1;
            } else {
                // invalid input will be asked again
                System.out.println("Invalid decision, choose a new one >>> ");
            }
        }
    }

    /* --------------------------------------- HERE STARTS THE IMPLEMENTATION OF THE USAGE OF THE ACTIONS ------------------------------------*/

    private TerminatorAction setTerminatorAction(UserPlayer currentPlayer) {
        Scanner in = new Scanner(System.in);
        int coordX;
        int coordY;
        UserPlayer targetPlayer;
        PlayerPosition movingPos;

        System.out.println("ALWAYS REMEMBER THAT IF THE TERMINATOR CAN SEE SOMEONE (THAT IT IS NOT YOU) " +
                "HE MUST ALWAYS SHOOT HIM, THAT IS: IF HE DOESN'T MOVE HE MUST HAVE A TARGET, OTHERWISE NOT");

        for (; ; ) {
            System.out.println("Do you want to move the terminator ? (yes/no) ");
            String moveDecision = in.nextLine();
            if (moveDecision.equals("yes")) {
                System.out.println("Provide the X coordinate >>> ");
                coordX = Integer.parseInt(in.nextLine());
                System.out.println("Provide the y coordinate >>> ");
                coordY = Integer.parseInt(in.nextLine());
                movingPos = new PlayerPosition(coordX, coordY);
                break;
            } else if (moveDecision.equals("no")) {
                movingPos = null;
                break;
            } else {
                // typo or not accepted command, will be asked again
            }
        }

        for (; ; ) {
            System.out.println("Does the terminator shoot someone ? (yes/no) ");
            String shootDecision = in.nextLine();
            if (shootDecision.equals("yes")) {
                System.out.println("Choose the terminator's target >>> ");
                String targetUserName = in.nextLine();
                if (gameInstance.isPlayerPresent(targetUserName)) {
                    targetPlayer = gameInstance.getUserPlayerByUsername(targetUserName);
                    turnManager.setDamagedPlayers(new ArrayList<>(List.of(targetPlayer)));
                    if (targetPlayer.getPosition() != null) {
                        break;
                    }
                }
            } else if (shootDecision.equals("no")) {
                targetPlayer = null;
                break;
            } // typo or not accepted command, will be asked again
        }

        // terminator action has been decided
        return new TerminatorAction(currentPlayer, targetPlayer, movingPos);
    }

    // TerminatorAction is the only one that has to always done when decided to be used because every player in each turn must do it !
    private void handleTerminatorAction(UserPlayer currentPlayer) {
        for (; ; ) {
            TerminatorAction builtAction = setTerminatorAction(currentPlayer);

            try {
                if (builtAction.validate()) {
                    builtAction.execute();
                    break;
                } else {
                    // action is invalid and will be asked again
                }
            } catch (InvalidActionException e) {
                // action is invalid and will be asked again
            }
        }
    }

    private MoveAction setMoveAction(UserPlayer turnOwner) {
        Scanner in = new Scanner(System.in);
        PossibleAction actionType;
        int coordX;
        int coordY;

        if (turnOwner.getPossibleActions().contains(PossibleAction.MOVE)) {
            actionType = PossibleAction.MOVE;
        } else if (turnOwner.getPossibleActions().contains(PossibleAction.FRENZY_MOVE)) {
            actionType = PossibleAction.FRENZY_MOVE;
        } else {
            throw new MissingActionException();
        }

        System.out.println("Choose the position where you want to move: ");
        System.out.println("Provide X coordinate >>> ");
        coordX = Integer.parseInt(in.nextLine());
        System.out.println("Provide Y coordinate >>> ");
        coordY = Integer.parseInt(in.nextLine());

        return new MoveAction(turnOwner, new PlayerPosition(coordX, coordY), actionType);
    }

    /**
     * Method that builds the move action decided by the turnOwner and returns true if the action is
     * executed, otherwise the action is not executed and the player would do an other one
     *
     * @param turnOwner UserPlayer who is currently playing
     * @return true if the action is executed, otherwise false
     */
    private boolean handleMoveAction(UserPlayer turnOwner) {
        MoveAction builtAction = setMoveAction(turnOwner);

        try {
            if (builtAction.validate()) {
                builtAction.execute();
                return true;
            } else {
                return false;
            }
        } catch (InvalidActionException e) {
            return false;
        }
    }

    private PickAction setPickAction(UserPlayer turnOwner) {
        Scanner in = new Scanner(System.in);
        ActionRequest pickRequest;
        Set<PossibleAction> ownersActions = turnOwner.getPossibleActions();
        PossibleAction actionType;
        PlayerPosition movingPos;
        Square movingSquare;
        int coordX;
        int coordY;

        if (ownersActions.contains(PossibleAction.MOVE_AND_PICK)) {
            actionType = PossibleAction.MOVE_AND_PICK;
        } else if (ownersActions.contains(PossibleAction.ADRENALINE_PICK)) {
            actionType = PossibleAction.ADRENALINE_PICK;
        } else if (ownersActions.contains(PossibleAction.FRENZY_PICK)) {
            actionType = PossibleAction.FRENZY_PICK;
        } else if (ownersActions.contains(PossibleAction.LIGHT_FRENZY_PICK)) {
            actionType = PossibleAction.LIGHT_FRENZY_PICK;
        } else {
            throw new MissingActionException();
        }

        // movement setting
        for (; ; ) {
            System.out.println("Do you want to move or stay and just pick? (0: stay, 1: move&pick) ");
            int decision = Integer.parseInt(in.nextLine());
            if (decision == 1) {
                System.out.println("Provide the X coordinate >>> ");
                coordX = Integer.parseInt(in.nextLine());
                System.out.println("Provide the Y coordinate >>> ");
                coordY = Integer.parseInt(in.nextLine());
                movingPos = new PlayerPosition(coordX, coordY);
                movingSquare = gameInstance.getGameMap().getSquare(coordX, coordY);
                break;
            } else if (decision == 0) {
                movingPos = null;
                movingSquare = gameInstance.getGameMap().getSquare(turnOwner.getPosition());
                break;
            } else {
                // typo or not accepted command, will be asked again
            }
        }

        if (movingSquare.getSquareType() == SquareType.TILE) {
            pickRequest = new MovePickRequest(turnOwner.getUsername(), movingPos, null);
            return new PickAction(turnOwner, movingPos, actionType, null, null, pickRequest);
        } else if (movingSquare.getSquareType() == SquareType.SPAWN) {
            return handleWeaponPick(turnOwner, movingPos, actionType, (SpawnSquare) movingSquare);
        } else {
            throw new NullPointerException("A square must always have a type!");
        }
    }

    /**
     * Method that builds the pick action decided by the TurnOwner and returns true if the action is
     * executed, otherwise the action is not executed and the player would do an other one
     *
     * @param turnOwner UserPlayer who is currently playing
     * @return true if the action is executed, otherwise false
     */
    private boolean handlePickAction(UserPlayer turnOwner) {
        PickAction builtAction = setPickAction(turnOwner);

        try {
            if (builtAction.validate()) {
                builtAction.execute();
                return true;
            } else {
                return false;
            }
        } catch (InvalidActionException e) {
            return false;
        }
    }

    private PickAction handleWeaponPick(UserPlayer turnOwner, PlayerPosition movingPos, PossibleAction actionType, SpawnSquare pickingSquare) {
        Scanner in = new Scanner(System.in);
        ActionRequest pickRequest;
        WeaponCard[] weaponsOnSquare = pickingSquare.getWeapons();
        ArrayList<Integer> powerups;
        WeaponCard pickingWeapon;
        WeaponCard discardingWeapon;

        for (WeaponCard weapon : weaponsOnSquare) {
            System.out.println(weapon.toString() + "\n");
        }
        for (; ; ) {
            // CLI/GUI weapons in square display
            System.out.println("Choose the weapon you want to pick between the ones displayed (0,1,2) >>> ");
            int weaponChosen = Integer.parseInt(in.nextLine());
            if (weaponChosen < 0 || weaponChosen > 2) {
                // invalid index will be asked again
            } else {
                pickingWeapon = pickingSquare.getWeapons()[weaponChosen];
                break;
            }
        }

        System.out.println("Do you want to use any powerup to pay the weapon ? (yes/no) ");
        powerups = payWithPowerups();

        for (; ; ) {
            if (turnOwner.getWeapons().length == 3) {
                for (WeaponCard weapon : turnOwner.getWeapons()) {
                    System.out.println(weapon.toString() + "\n");
                }
                System.out.println("You already have 3 weapons in your hand choose one to be discarded (0,1,2) >>> ");
                int discardingWeaponChosen = Integer.parseInt(in.nextLine());
                if (discardingWeaponChosen < 0 || discardingWeaponChosen > 2) {
                    // invalid index will be asked again
                } else {
                    discardingWeapon = turnOwner.getWeapons()[discardingWeaponChosen];
                    break;
                }
            }
        }

        // all informations have been decided
        pickRequest = new MovePickRequest(turnOwner.getUsername(), movingPos, powerups);
        return new PickAction(turnOwner, movingPos, actionType, pickingWeapon, discardingWeapon, pickRequest);

    }

    private ArrayList<Integer> payWithPowerups() {
        Scanner in = new Scanner(System.in);
        ArrayList<Integer> powerups = new ArrayList<>();

        String usePowerup = in.nextLine();
        if (usePowerup.equalsIgnoreCase("yes")) {
            for (; ; ) {
                System.out.println("Choose the powerup index to use (-1 to stop choosing) >>> ");
                int powerupChosen = Integer.parseInt(in.nextLine());
                if (powerupChosen == -1) {
                    break;
                } else if (powerupChosen < 0 || powerupChosen > 2) {
                    // invalid index will be asked again
                } else {
                    if (powerups.size() == 3) {
                        System.out.println("It's impossible you can have other powerups in your hand!");
                        break;
                    }
                    powerups.add(powerupChosen);
                }
            }
        } else if (usePowerup.equalsIgnoreCase("no")) {
            return powerups;
        }

        return powerups;
    }

    private ShootAction setShootAction(UserPlayer turnOwner) {
        Scanner in = new Scanner(System.in);
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
            throw new MissingActionException();
        }

        return null;

    }

    private boolean handleShootAction(UserPlayer turnOwner) {
        turnManager.setDamagedPlayers(null);
        // TODO decidere come fare la interazione + request del cazzo
        return true;
    }

    private ReloadAction setReloadAction(UserPlayer turnOwner) {
        Scanner in = new Scanner(System.in);
        ReloadRequest reloadRequest;
        ArrayList<Integer> reloadingIndexes = new ArrayList<>();
        ArrayList<WeaponCard> reloadingWeapons = new ArrayList<>();
        ArrayList<Integer> powerups;

        for (WeaponCard weapon : turnOwner.getUnloadedWeapons()) {
            System.out.println(weapon.toString() + "\n");
        }

        System.out.println("Choose the weapons you want to reload specifying their index (0,1,2), -1 to stop choosing >>> ");
        for (; ; ) {
            int weaponChosen = Integer.parseInt(in.nextLine());
            if (weaponChosen == -1) {
                break;
            } else if (weaponChosen < 0 || weaponChosen > 2) {
                // wrong index will be asked again
            } else {
                if (reloadingIndexes.size() == 3) {
                    System.out.println("It's impossible you have other weapons to reload!");
                    break;
                }
                reloadingIndexes.add(weaponChosen);
                reloadingWeapons.add(turnOwner.getUnloadedWeapons().get(weaponChosen));
            }
        }

        System.out.println("Do you want to use any powerup to reload the weapon ? (yes/no) ");
        powerups = payWithPowerups();

        // every information about a reloading action has been received
        reloadRequest = new ReloadRequest(turnOwner.getUsername(), reloadingIndexes, powerups);
        return new ReloadAction(turnOwner, reloadingWeapons, reloadRequest);
    }

    /**
     * Method that builds the ReloadAction the TurnOwner decided to do and executes it returning
     * true if the action is valid, otherwise false
     *
     * @param turnOwner UserPlayer containing the action
     * @return true if the action is executed, otherwise false
     */
    private boolean handleReloadAction(UserPlayer turnOwner) {
        ReloadAction builtAction = setReloadAction(turnOwner);

        try {
            if (builtAction.validate()) {
                builtAction.execute();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {     // here different exceptions are thrown we should even catch each and print different messages
            return false;
        }
    }

}
