package controller;

import enumerations.Color;
import enumerations.PossibleState;
import exceptions.game.MaxPlayerException;
import model.Game;
import model.player.PlayerBoard;
import model.player.UserPlayer;

import java.util.Scanner;

public class GameManager {
    private PossibleState gameState;
    private Game gameInstance;

    public GameManager() {
        this.gameState = PossibleState.GAME_ROOM;
        this.gameInstance = Game.getInstance();
    }


    public void gameSetup() {
        Scanner in = new Scanner(System.in);
        System.out.println("Provide the game setup informations: \n\n");

        for (;;) {
            System.out.println("Insert the map you want to play with (0-3): \n");

            try {
                gameInstance.setGameMap(in.nextInt());
                break;
            } catch (Exception e) {
                System.out.println("Invalid Map Number!\n");
                // Re-ask input
            }
        }

        for (;;) {
            System.out.println("Decide if you want to play with a terminator (true/false): \n");

            try {
                gameInstance.setTerminator(in.nextBoolean());
                break;
            } catch (Exception e) {
                System.out.println("Not a boolean value found!\n");
                // Re-ask input
            }
        }

        for (;;) {
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

        System.out.println("Please insert the usernames of the players playing: \n");
        do {
            System.out.println("Username >>> ");
            userName = in.nextLine();
            Color colorChosen;
            if(!gameInstance.doesPlayerExists(userName)) {
                for (;;) {
                    System.out.println(userName + " provide the color you have chosen: ");

                    try {
                        colorChosen = Color.getColor(in.nextLine());
                        if(!gameInstance.isColorUsed(colorChosen)) {
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
                } catch (MaxPlayerException e) {
                    // the game has reached the maximum number of players
                    break;
                }
            }
        } while (!gameInstance.isGameReadyToStart(in.nextBoolean()));

    }

    public void run() {
        System.out.println("Welcome to the very first version of a game: \n\n");

        // starting setup
        gameSetup();
        roomSetup();
    }


}
