package controller;

import enumerations.*;
import exceptions.actions.InvalidActionException;
import exceptions.player.MaxCardsInHandException;
import model.Game;
import model.actions.TerminatorAction;
import model.cards.PowerupCard;
import model.player.PlayerPosition;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class RoundManager {
    private Game gameInstance;
    private TurnManager turnManager;

    public RoundManager(Game gameInstance) {
        this.gameInstance = gameInstance;
    }

    public void initTurnManager() {
        this.turnManager = new TurnManager(gameInstance.getPlayers());
    }

    public void handleFirstRound() {
        for (int i = 0; i < gameInstance.getPlayers().size(); ++i) {
            UserPlayer currentPlayer = turnManager.getTurnOwner();
            Set<PossibleAction> currentPlayerActions = currentPlayer.getPossibleActions();
            System.out.println(currentPlayer.getUsername() + " it's your first turn!");

            if(currentPlayer.getPlayerState() == PossiblePlayerState.FIRST_SPAWN) {
                if(currentPlayerActions.contains(PossibleAction.CHOOSE_SPAWN)) {
                    boolean terminator = false;
                    if(currentPlayerActions.contains(PossibleAction.SPAWN_TERMINATOR)) {
                        System.out.println(currentPlayer.getUsername() + " you are the first player and then you must spawn the terminator first! \n");
                        terminator = true;
                    }
                    chooseSpawn(currentPlayer, terminator);
                }

                if(currentPlayerActions.contains(PossibleAction.TERMINATOR_ACTION)) {
                    System.out.println("Now the terminator action has to be done: \n");
                    useTerminatorAction(currentPlayer);
                }
            }

            turnManager.nextTurn();
        }

        GameManager.changeState(PossibleGameState.GAME_STARTED);
    }

    private void spawnTerminator() {
        Scanner in = new Scanner(System.in);
        RoomColor colorChosen;
        for(;;) {
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

    private void chooseSpawn(UserPlayer spawningPlayer, boolean terminator) {
        Scanner in = new Scanner(System.in);
        List<PowerupCard> twoDrawn = new ArrayList<>();
        String spawningPowerup;
        RoomColor spawnColor;

        for(int i = 0; i < 2; ++i) {
            PowerupCard cardDrawn = (PowerupCard) gameInstance.getPowerupCardsDeck().draw();
            twoDrawn.add(cardDrawn);
        }

        System.out.println("You have drawn these two powerups: \n");
        System.out.println("First powerup is: " + twoDrawn.get(0).toString());
        System.out.println("Second powerup is: " + twoDrawn.get(1).toString());

        if(terminator) {
            spawnTerminator();
        }

        System.out.println("Choose the powerup you want to spawn from, the other will be added to your hand \n");

        for (;;) {
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

    private TerminatorAction setTerminatorAction(UserPlayer currentPlayer) {
        Scanner in = new Scanner(System.in);
        int coordX;
        int coordY;
        UserPlayer targetPlayer;
        PlayerPosition movingPos;

        System.out.println("ALWAYS REMEMBER THAT IF THE TERMINATOR CAN SEE SOMEONE (THAT IT IS NOT YOU) " +
                "HE MUST ALWAYS SHOOT HIM, THAT IS: IF HE DOESN'T MOVE HE MUST HAVE A TARGET, OTHERWISE NOT");

        for(;;) {
            System.out.println("Do you want to move the terminator ? (yes/no) ");
            String moveDecision = in.nextLine();
            if(moveDecision.equals("yes")) {
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

        for(;;) {
            System.out.println("Does the terminator shoot someone ? (yes/no) ");
            String shootDecision = in.nextLine();
            if(shootDecision.equals("yes")) {
                System.out.println("Choose the terminator's target >>> ");
                String targetUserName = in.nextLine();
                if(gameInstance.isPlayerPresent(targetUserName)) {
                    targetPlayer = gameInstance.getUserPlayerByUsername(targetUserName);
                    if(targetPlayer.getPosition() != null) {
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

    private void useTerminatorAction(UserPlayer currentPlayer) {
        for(;;) {
            TerminatorAction builtAction = setTerminatorAction(currentPlayer);

            try {
                if(builtAction.validate()) {
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
}
