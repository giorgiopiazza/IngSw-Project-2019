package view.cli;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import enumerations.PlayerColor;
import model.GameSerialized;
import model.map.GameMap;
import model.player.*;
import network.client.Client;
import network.client.ClientRMI;
import network.client.ClientSocket;
import network.message.ColorResponse;
import network.message.ConnectionResponse;
import network.message.Message;
import network.message.Response;
import utility.MessageBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Cli {
    private Scanner in;
    private AdrenalinePrintStream out;
    private String username;
    private PlayerColor playerColor;
    private Client client;

    public Cli() {
        in = new Scanner(System.in);
        out = new AdrenalinePrintStream();
    }

    /**
     * Starts the view.cli
     */
    public void start() {

        /* CLI DEBUGGING
        GameSerialized gs = new GameSerialized();
        PlayerBoard pb = new PlayerBoard();
        UserPlayer p1 = new UserPlayer("Pippo", PlayerColor.BLUE, pb);
        UserPlayer p2 = new UserPlayer("Pluto", PlayerColor.GREEN, new PlayerBoard());
        UserPlayer p3 = new UserPlayer("Topolino", PlayerColor.PURPLE, new PlayerBoard());
        UserPlayer p4 = new UserPlayer("Minnie", PlayerColor.GREY, new PlayerBoard());
        Terminator p5 = new Terminator(PlayerColor.YELLOW, new PlayerBoard());


        p1.setPosition(new PlayerPosition(0,0));
        p2.setPosition(new PlayerPosition(0,0));
        p3.setPosition(new PlayerPosition(0,0));
        p4.setPosition(new PlayerPosition(0,0));
        p5.setPosition(new PlayerPosition(0,0));


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
        gs.setTerminator(p5);
        gs.setGameMap(new GameMap(GameMap.MAP_4));


        printLogo();
        askUsername();
        askConnection();
        askColor();
        askLobbyJoin();

        //CliPrinter.printPlayerBoards(out, gs);
        CliPrinter.printMap(out, gs);
         */
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

    private void askUsername() {
        boolean validUsername = false;
        boolean firstError = true;

        out.println("Enter your username:");

        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                username = in.nextLine();

                if (username.equals("") ||
                        username.equalsIgnoreCase("god") ||
                        username.equalsIgnoreCase("bot")) {
                    firstError = promptInputError(firstError, "Invalid username!");
                } else {
                    validUsername = true;
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid string!");
            }
        } while (!validUsername);

        clearConsole();
    }

    private void askConnection() {
        boolean validConnection = false;
        boolean firstError = true;
        int connection = -1;

        out.printf("Hi %s!%n", username);
        out.println("\nEnter the connection type (0 = Sockets or 1 = RMI):");

        do {
            out.print(">>> ");

            if (in.hasNextInt()) {
                connection = in.nextInt();
                in.nextLine();

                if (connection >= 0 && connection <= 1) {
                    validConnection = true;
                } else {
                    firstError = promptInputError(firstError, "Invalid selection!");
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid integer!");
            }
        } while (!validConnection);

        clearConsole();

        if (connection == 0) {
            out.println("You chose Socket connection\n");
        } else {
            out.println("You chose RMI connection\n");
        }

        String address = askAddress();
        out.println("\nServer Address: " + address);

        int port = askPort();
        try {
            if (connection == 0) {
                client = new ClientSocket(username, address, port);
            } else {
                client = new ClientRMI(username, address, port);
            }

            startConnection();
        } catch (Exception e) {
            promptError(e.getMessage(), true);
        }
    }

    private String askAddress() {
        String address;
        boolean firstError = true;

        out.println("Enter the server address (default is \"localhost\"):");

        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                address = in.nextLine();

                if (address.equals("")) {
                    return "localhost";
                } else if (isAddressValid(address)) {
                    return address;
                } else {
                    firstError = promptInputError(firstError, "Invalid address!");
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid string!");
            }
        } while (true);
    }

    private boolean isAddressValid(String address) {
        if (address == null || address.equals("localhost")) {
            return true;
        }

        String[] groups = address.split("\\.");

        if (groups.length != 4)
            return false;

        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() > 1 && s.startsWith("0"))
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int askPort() {
        boolean firstError = true;

        out.println("\nEnter the server port:");

        do {
            out.print(">>> ");

            if (in.hasNextInt()) {
                int port = in.nextInt();
                in.nextLine();

                if (port >= 1 && port <= 65535) {
                    return port;
                } else {
                    firstError = promptInputError(firstError, "Invalid port!");
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid integer!");
            }
        } while (true);
    }

    private void startConnection() throws Exception {
        clearConsole();
        client.startConnection();
        List<Message> messages;

        do {
            messages = client.receiveMessages();
        } while (messages.isEmpty());

        boolean connected = false;

        for (Message message : messages) {
            if (message.getContent().equals(MessageContent.CONNECTION_RESPONSE)) {
                ConnectionResponse response = (ConnectionResponse) message;

                if (response.getStatus().equals(MessageStatus.ERROR)) {
                    promptError(response.getMessage(), false);
                    out.println();
                    break;
                } else {
                    out.println("Connected to " + client.getAddress() + ":" + client.getPort() + " with username " + username);
                    client.setToken(response.getNewToken());
                    connected = true;
                }
            }
        }

        if (!connected) askUsername();
    }

    private void askColor() {
        boolean validColor = false;
        boolean firstError = true;

        PlayerColor playerColor = null;
        List<PlayerColor> availableColors = getUnusedColors();


        if (availableColors.isEmpty()) {
            promptError("The game is full!", true);
        }

        String colorString = availableColors.stream()
                .map(PlayerColor::name)
                .collect(Collectors.joining(", "));

        out.printf("%nAvailable colors are %s%n", colorString);

        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                String color = in.nextLine();


                try {
                    playerColor = PlayerColor.valueOf(color.toUpperCase());
                } catch (IllegalArgumentException e) {
                    firstError = promptInputError(firstError, "Invalid color!");
                    continue;
                }

                if (availableColors.contains(playerColor)) {
                    validColor = true;
                } else {
                    firstError = promptInputError(firstError, "Invalid color!");
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid string!");
            }
        } while (!validColor);

        out.printf("%nYou picked %s color.%n", playerColor.name());
        this.playerColor = playerColor;
    }

    private List<PlayerColor> getUnusedColors() {
        List<Message> messages;
        List<PlayerColor> availableColors = new ArrayList<>();

        try {
            client.sendMessage(MessageBuilder.buildColorRequest(client.getToken(), client.getUsername()));
        } catch (IOException e) {
            promptError(e.getMessage(), true);
        }

        do {
            messages = client.receiveMessages();
        } while (messages.isEmpty());

        for (Message message : messages) {
            if (message.getContent().equals(MessageContent.COLOR_RESPONSE)) {
                ColorResponse response = (ColorResponse) message;

                availableColors = response.getColorList();
            }
        }

        return availableColors;
    }

    private void askLobbyJoin() {
        List<Message> messages;

        try {
            client.sendMessage(MessageBuilder.buildGetInLobbyMessage(client.getToken(), username, playerColor));
        } catch (IOException e) {
            promptError(e.getMessage(), true);
        }

        do {
            messages = client.receiveMessages();
        } while (messages.isEmpty());

        for (Message message : messages) {
            if (message.getContent().equals(MessageContent.RESPONSE)) {
                Response response = (Response) message;

                clearConsole();
                out.println(response.getMessage());
                if (response.getStatus() == MessageStatus.ERROR) {
                    out.println();
                    askColor();
                }
            }
        }
    }

    private void clearConsole() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    private boolean promptInputError(boolean firstError, String errorMessage) {
        out.print("\33[1A\33[2K");
        if (!firstError) {
            out.print("\33[1A\33[2K");
        }

        out.println(errorMessage);
        return false;
    }

    private void promptError(String error, boolean close) {
        clearConsole();

        out.println("ERROR: " + error);

        if (close) {
            out.println("\nPress ENTER to exit");
            in.nextLine();
            System.exit(1);
        }
    }
}
