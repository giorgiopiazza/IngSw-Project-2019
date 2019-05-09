package controller;

import enumerations.Color;
import enumerations.PossibleGameState;
import exceptions.game.InvalidGameStateException;
import exceptions.game.MaxPlayerException;
import model.Game;
import model.player.PlayerBoard;
import model.player.UserPlayer;

import java.util.Scanner;

public class GameManager {
    private final Game gameInstance;
    private static PossibleGameState gameState;
    private RoundManager roundManager;

    public GameManager() {
        gameState = PossibleGameState.GAME_ROOM;
        this.gameInstance = Game.getInstance();
        this.roundManager = new RoundManager(this.gameInstance);
    }

    public static void changeState(PossibleGameState changeState) {
        gameState = changeState;
    }


    public void gameSetup() {
        Scanner in = new Scanner(System.in);

        System.out.println("Provide the game setup informations: \n\n");

        for (; ; ) {
            System.out.println("Insert the map you want to play with (0-3): \n");

            try {
                gameInstance.setGameMap(in.nextInt());
                break;
            } catch (Exception e) {
                System.out.println("Invalid Map Number!\n");
                // Re-ask input
            }
        }

        for (; ; ) {
            System.out.println("Decide if you want to play with a terminator (true/false): \n");

            try {
                gameInstance.setTerminator(in.nextBoolean());
                break;
            } catch (Exception e) {
                System.out.println("Not a boolean value found!\n");
                // Re-ask input
            }
        }

        for (; ; ) {
            System.out.println("Insert the number of killshots you want to play with (>= 5 and <= 8): \n");

            try {
                gameInstance.setKillShotNum(in.nextInt());
                break;
            } catch (Exception e) {
                System.out.println("Invalid killshot Number!\n");
                // Re-ask input
            }
        }
    }

    public void roomSetup() {
        Scanner in = new Scanner(System.in);

        String userName;
        boolean ready = false;

        System.out.println("Please insert the usernames of the players playing: \n");

        while (!gameInstance.isGameReadyToStart(ready)) {
            System.out.println("Username >>> ");
            userName = in.nextLine();
            Color colorChosen;

            if (!gameInstance.doesPlayerExists(userName)) {
                for (; ; ) {
                    System.out.println(userName + " provide the color you have chosen: ");

                    try {
                        colorChosen = Color.getColor(in.nextLine());
                        if (!gameInstance.isColorUsed(colorChosen)) {
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid color chosen!\n");
                        // choose again a color
                    }
                }

                try {
                    gameInstance.addPlayer(new UserPlayer(userName, colorChosen, new PlayerBoard()));
                    System.out.println("The player: " + userName + " has been added to the game, do you want to start the game ?" +
                            " Type true to start, false to wait for other players: ");
                    ready = Boolean.parseBoolean(in.nextLine());
                } catch (MaxPlayerException e) {
                    // the game has reached the maximum number of players
                    break;
                }
            }
        }

        gameState = PossibleGameState.GAME_READY;
    }

    public void run() {
        System.out.println("Welcome to the very first version of the game: \n\n");

        // starting setup
        if(gameState == PossibleGameState.GAME_ROOM) {
            gameSetup();
            roomSetup();
        } else throw new InvalidGameStateException();

        // game is ready and can be started
        if(gameState == PossibleGameState.GAME_READY) {
            gameInstance.startGame();
            System.out.println("ADRENALINE is ready to start! \n");
            System.out.println(gameInstance.getPlayers().get(0).getUsername() + " is the first player \n");

            while (gameState == PossibleGameState.GAME_READY) {
                roundManager.initTurnManager();
                roundManager.handleFirstRound();
            }
        }

        // now game has started
        if(gameState == PossibleGameState.GAME_STARTED) {

        }
    }
}
