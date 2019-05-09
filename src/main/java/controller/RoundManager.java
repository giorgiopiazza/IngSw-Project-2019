package controller;

import enumerations.*;
import exceptions.player.MaxCardsInHandException;
import model.Game;
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
                if(currentPlayerActions.contains(PossibleAction.SPAWN_TERMINATOR)) {
                    System.out.println(currentPlayer.getUsername() + " you are the first player and then you must spawn the terminator first! \n");
                    spawnTerminator();
                }

                if(currentPlayerActions.contains(PossibleAction.CHOOSE_SPAWN)) {
                    System.out.println(currentPlayer.getUsername() + " decide where you want to spawn \n");
                    chooseSpawn(currentPlayer);
                }

                if(currentPlayerActions.contains(PossibleAction.TERMINATOR_ACTION)) {
                    // TODO
                }
            }

            turnManager.nextTurn();
        }

        GameManager.changeState(PossibleGameState.GAME_STARTED);
    }

    private void spawnTerminator() {
        Scanner in = new Scanner(System.in);
        int coordX;
        int coordY;

        System.out.println("Choose the coordinates where to spawn the terminator \n\n");
        System.out.println("Provide the X coordinate >>> ");
        coordX = Integer.parseInt(in.nextLine());
        System.out.println("Provide the Y coordinate >>> ");
        coordY = Integer.parseInt(in.nextLine());

        gameInstance.getTerminator().setPosition(new PlayerPosition(coordX, coordY));
    }

    private void chooseSpawn(UserPlayer spawningPlayer) {
        Scanner in = new Scanner(System.in);
        List<PowerupCard> twoDrawn = new ArrayList<>();
        String spawningPowerup;
        Color spawnColor;

        for(int i = 0; i < 2; ++i) {
            PowerupCard cardDrawn = (PowerupCard) gameInstance.getPowerupCardsDeck().draw();
            twoDrawn.add(cardDrawn);
        }

        System.out.println("You have drawn these two powerups: \n");
        System.out.println("First powerup is: " + twoDrawn.get(0).toString());
        System.out.println("Second powerup is: " + twoDrawn.get(1).toString());
        System.out.println("Choose the one you want to spawn from, the other will be added to your hand \n");

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

        spawningPlayer.setPosition(gameInstance.getGameMap().getSpawnSquare(spawnColor));
        spawningPlayer.changePlayerState(PossiblePlayerState.WAITING_TO_PLAY);
    }
}
