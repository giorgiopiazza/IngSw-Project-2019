package view.cli;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import enumerations.PlayerColor;
import model.GameSerialized;
import model.map.GameMap;
import model.player.Player;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import network.client.Client;
import network.client.ClientRMI;
import network.client.ClientSocket;
import network.message.Message;
import network.message.Response;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private static final int RMI_PORT = 7272;
    private static final int SOCKET_PORT = 2727;
    private static final String ADDRESS = "localhost";
    private Scanner in;
    private AdrenalinePrintStream out;
    private String username;
    private Client client;

    public Cli() {
        in = new Scanner(System.in);
        out = new AdrenalinePrintStream();
    }

    /**
     * Starts the view.cli
     */
    public void start() {

        GameSerialized gs = new GameSerialized();
        PlayerBoard pb = new PlayerBoard();
        UserPlayer p1 = new UserPlayer("Pippo", PlayerColor.BLUE, pb);
        UserPlayer p2 = new UserPlayer("Pluto", PlayerColor.GREEN, new PlayerBoard());
        UserPlayer p3 = new UserPlayer("Topolino", PlayerColor.PURPLE, new PlayerBoard());
        UserPlayer p4 = new UserPlayer("Minnie", PlayerColor.GREY, new PlayerBoard());

        p1.getPlayerBoard().addDamage(p2, 3);
        p1.getPlayerBoard().addDamage(p4, 1);
        p2.getPlayerBoard().addDamage(p1, 2);
        p2.getPlayerBoard().addDamage(p4, 3);
        p3.getPlayerBoard().addDamage(p4, 2);
        p3.getPlayerBoard().addDamage(p1, 2);
        p4.getPlayerBoard().addDamage(p2, 1);
        p4.getPlayerBoard().addDamage(p3, 2);

        pb.addMark(p2, 1);
        pb.addMark(p3, 3);
        pb.addMark(p4, 2);
        pb.addMark(p2, 2);
        pb.addMark(p4, 1);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        gs.setPlayers(players);
        gs.setGameMap(new GameMap(GameMap.MAP_1));


        printLogo();
        askUsername();
        try {
            askConnection();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        askColor();

        CliPrinter.printPlayerBoards(out, gs);


        CliPrinter.printMap(out, gs);
    }

    private void printLogo() {
        String adrenalineLogo = "             _____   _____   _______ _   _            _       _  _   _  _______\n" +
                "      /\\    |  __ \\ |  __ \\ |  ____/| \\ | |    /\\    | |     | || \\ | ||  ____/\n" +
                "     /  \\   | |  | || |__) || |__   |  \\| |   /  \\   | |     | ||  \\| || |__   \n" +
                "    / /\\ \\  | |  | ||  _  / |  __|  | . ` |  / /\\ \\  | |     | || . ` ||  __|  \n" +
                "   / /__\\ \\ | |__| || | \\ \\ | |_____| |\\  | / /__\\ \\ | |____ | || |\\  || |_____\n" +
                "  /_/|_____\\|_____/ |_|  \\_\\|______/|_| \\_|/_/|_____\\|______\\|_||_| \\_||______/\n\n" +
                "Welcome to Adrenaline Board Game made by Giorgio Piazza, Francesco Piro and Lorenzo Tosetti.\n" +
                "Before starting playing you need to setup some things:\n";

        out.println(adrenalineLogo);
    }

    private void askConnection() throws Exception {
        boolean validConnection = false;
        boolean firstError = true;
        int connection = -1;

        out.println("Choose the connection type (0 = RMI or 1 = Sockets):");

        do {
            out.print(">>> ");

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

        out.println();

        if (connection == 0) {
            out.println("You chose RMI connection");

            client = new ClientRMI(username, ADDRESS, RMI_PORT);
        } else {
            out.println("You chose Socket connection");

            client = new ClientSocket(username, ADDRESS, SOCKET_PORT);
        }

        startConnection();
    }

    private void startConnection() throws Exception {
        client.startConnection();
        List<Message> messages;

        do {
            messages = client.receiveMessages();
        } while (messages.isEmpty());

        boolean connected = false;

        for (Message message : messages) {
            if (message.getContent().equals(MessageContent.RESPONSE)) {
                Response response = (Response) message;

                if (response.getStatus().equals(MessageStatus.ERROR)) {
                    promptError(false, "Error: " + response.getMessage());
                    out.println("Please retry...");
                    break;
                } else {
                    out.println("Connected with username: " + username);
                    connected = true;
                }
            }
        }

        if (!connected) askConnection();
    }

    private void askUsername() {
        boolean validUsername = false;
        boolean firstError = true;

        out.println("\nChoose your username:");

        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                username = in.nextLine();

                if (!username.equals("god") && !username.equals("bot")) {
                    validUsername = true;
                } else {
                    firstError = promptError(firstError, "Invalid username!");
                }
            } else {
                in.nextLine();
                firstError = promptError(firstError, "Invalid string!");
            }
        } while (!validUsername);

        out.printf("Hi %s, wait for server connection.%n", username);
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
        out.printf("%nAvailable colors are %s%n", colorString );

        do {
            out.print(">>> ");

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

        out.printf("%nYou picked %s color.%n", color.toUpperCase());
    }

    private boolean promptError(boolean firstError, String errorMessage) {
        out.print("\33[1A\33[2K");
        if (!firstError) {
            out.print("\33[1A\33[2K");
        }

        out.println(errorMessage);
        return false;
    }
}
