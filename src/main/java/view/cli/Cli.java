package view.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private Scanner in;

    public Cli() {
        in = new Scanner(System.in);
    }

    /**
     * Starts the cli
     */
    public void start() {
        printLogo();
        askConnection();
        askUsername();
        askColor();
    }

    private void printLogo() {
        System.out.println("             _____   _____   _______ _   _            _       _  _   _  _______");
        System.out.println("      /\\    |  __ \\ |  __ \\ |  ____/| \\ | |    /\\    | |     | || \\ | ||  ____/");
        System.out.println("     /  \\   | |  | || |__) || |__   |  \\| |   /  \\   | |     | ||  \\| || |__   ");
        System.out.println("    / /\\ \\  | |  | ||  _  / |  __|  | . ` |  / /\\ \\  | |     | || . ` ||  __|  ");
        System.out.println("   / /__\\ \\ | |__| || | \\ \\ | |_____| |\\  | / /__\\ \\ | |____ | || |\\  || |_____");
        System.out.println("  /_/|_____\\|_____/ |_|  \\_\\|______/|_| \\_|/_/|_____\\|______\\|_||_| \\_||______/");
        System.out.println();
        System.out.println("Welcome to Adrenaline Board Game made by Giorgio Piazza, Francesco Piro and Lorenzo Tosetti.");
        System.out.println("Before starting playing you need to setup some things:");
        System.out.println();
    }

    private void askConnection() {
        boolean validConnection = false;
        boolean firstError = true;
        int connection = -1;

        System.out.println("Choose the connection type (0 = RMI or 1 = Sockets):");

        do {
            System.out.print(">>> ");

            if (in.hasNextInt()) {
                connection = in.nextInt();
                in.nextLine();

                if (connection >= 0 && connection <= 1) {
                    validConnection = true;
                } else {
                    firstError = promptError(firstError, "Invalid selection!");
                }
            } else {
                in.nextLine();
                firstError = promptError(firstError, "Invalid integer!");
            }
        } while (!validConnection);

        System.out.println();

        if (connection == 0) {
            System.out.println("You chose RMI connection");
        } else {
            System.out.println("You chose Socket connection");
        }
    }


    /*
      _____
      |---|
      |---|
      |---|


      |P L|
      | N |
      |K G|


     */

    private void askUsername() {
        boolean validUsername = false;
        boolean firstError = true;
        String username = "";

        System.out.println();
        System.out.println("Choose your username:");

        do {
            System.out.print(">>> ");

            if (in.hasNextLine()) {
                username = in.nextLine();

                if (!username.equals("god")) {
                    validUsername = true;
                } else {
                    firstError = promptError(firstError, "Invalid username!");
                }
            } else {
                in.nextLine();
                firstError = promptError(firstError, "Invalid string!");
            }
        } while (!validUsername);

        System.out.println();
        System.out.printf("Hi %s, now pick your color.%n", username);
    }

    private void askColor() {
        boolean validColor = false;
        boolean firstError = true;
        String color = "";
        List<String> availableColors = new ArrayList<>();

        availableColors.add("GREEN");
        availableColors.add("YELLOW");
        availableColors.add("RED");

        String colorString = String.join(", ", availableColors.toArray(new String[0]));

        System.out.println();
        System.out.printf("Available colors are %s%n", colorString);

        do {
            System.out.print(">>> ");

            if (in.hasNextLine()) {
                color = in.nextLine();

                if (availableColors.contains(color.toUpperCase())) {
                    validColor = true;
                } else {
                    firstError = promptError(firstError, "Invalid color!");
                }
            } else {
                in.nextLine();
                firstError = promptError(firstError, "Invalid string!");
            }
        } while (!validColor);

        System.out.println();
        System.out.printf("You picked %s color.%n", color.toUpperCase());
    }

    private boolean promptError(boolean firstError, String errorMessage) {
        System.out.print("\33[1A\33[2K");
        if (!firstError) {
            System.out.print("\33[1A\33[2K");
        }

        System.out.println(errorMessage);
        return false;
    }
}
