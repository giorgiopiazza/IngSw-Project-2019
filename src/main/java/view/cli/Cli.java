package view.cli;

import controller.ClientGameManager;
import enumerations.*;
import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.actions.WeaponCardsNotFoundException;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.player.*;
import network.client.*;
import network.message.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import utility.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Cli extends ClientGameManager {
    private Client client;
    private Scanner in;
    private AdrenalinePrintStream out;

    public Cli() {
        super();
        this.in = new Scanner(System.in);
        this.out = new AdrenalinePrintStream();
    }

    /**
     * Starts the view.cli
     */
    public void start() {
        printLogo();
        askUsername();
        askConnection();
        askColor();
        askLobbyJoin();

        startWaiter(client, this);
    }

    /**
     * Prints Adrenaline Logo
     */
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

    /**
     * Asks the username
     */
    private void askUsername() {
        boolean validUsername = false;
        boolean firstError = true;

        out.println("Enter your username:");

        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                setUsername(in.nextLine());

                if (getUsername().equals("") ||
                        getUsername().equalsIgnoreCase("god") ||
                        getUsername().equalsIgnoreCase("bot")) {
                    firstError = promptInputError(firstError, "Invalid username!");
                } else {
                    validUsername = true;
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid string!");
            }
        } while (!validUsername);

        CliPrinter.clearConsole(out);
    }

    /**
     * Asks the connection tpye
     */
    private void askConnection() {
        boolean validConnection = false;
        boolean firstError = true;
        int connection = -1;

        out.printf("Hi %s!%n", getUsername());
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

        CliPrinter.clearConsole(out);

        if (connection == 0) {
            out.println("You chose Socket connection\n");
        } else {
            out.println("You chose RMI connection\n");
        }

        String address = askAddress();
        out.println("\nServer Address: " + address);

        int port = askPort(connection);
        out.println("\nServer Port: " + port);

        try {
            if (connection == 0) {
                client = new ClientSocket(getUsername(), address, port);
            } else {
                client = new ClientRMI(getUsername(), address, port);
            }

            startConnection();
        } catch (Exception e) {
            promptError(e.getMessage(), true);
        }
    }

    /**
     * Asks and verify the address
     *
     * @return a verified address
     */
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
                } else if (ServerAddressValidator.isAddressValid(address)) {
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

    /**
     * Asks and verify the port
     *
     * @param connection type to set the default port
     * @return a verified port
     */
    private int askPort(int connection) {
        boolean firstError = true;

        int defaultPort = connection == 0 ? 2727 : 7272;
        out.println("\nEnter the server port (default " + defaultPort + "):");
        in.reset();

        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                String line = in.nextLine();

                if (line.equals("")) {
                    return defaultPort;
                } else {
                    if (ServerAddressValidator.isPortValid(line)) {
                        return Integer.parseInt(line);
                    } else {
                        firstError = promptInputError(firstError, "Invalid Port!");
                    }
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid string!");
            }
        } while (true);
    }

    /**
     * Starts a connection with the server
     *
     * @throws Exception if communication with server fails
     */
    private void startConnection() throws Exception {
        CliPrinter.clearConsole(out);
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
                    out.println("Connected to " + client.getAddress() + ":" + client.getPort() + " with username " + getUsername());
                    client.setToken(response.getNewToken());
                    connected = true;
                }
            }
        }

        if (!connected) askUsername();
    }

    /**
     * Asks a color
     */
    private void askColor() {
        boolean validColor = false;
        boolean firstError = true;

        PlayerColor playercolor = null;
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
                    playercolor = PlayerColor.valueOf(color.toUpperCase());
                } catch (IllegalArgumentException e) {
                    firstError = promptInputError(firstError, "Invalid color!");
                    continue;
                }

                if (availableColors.contains(playercolor)) {
                    validColor = true;
                } else {
                    firstError = promptInputError(firstError, "Invalid color!");
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, "Invalid string!");
            }
        } while (!validColor);

        out.printf("%nYou picked %s color.%n", playercolor.name());
        setPlayerColor(playercolor);
    }

    /**
     * @return the list of unused colors
     */
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

    /**
     * Asks to the server the join to the lobby
     */
    private void askLobbyJoin() {
        List<Message> messages;

        try {
            client.sendMessage(MessageBuilder.buildGetInLobbyMessage(client.getToken(), getUsername(), getPlayerColor()));
        } catch (IOException e) {
            promptError(e.getMessage(), true);
        }

        do {
            messages = client.receiveMessages();
        } while (messages.isEmpty());

        for (Message message : messages) {
            if (message.getContent().equals(MessageContent.RESPONSE)) {
                Response response = (Response) message;

                CliPrinter.clearConsole(out);
                out.println(response.getMessage());
                if (response.getStatus() == MessageStatus.ERROR) {
                    out.println();
                    askColor();
                }
            }
        }
    }

    /**
     * Prompts an input error
     *
     * @param firstError   {@code true} if it's the first error made
     * @param errorMessage the error message
     * @return {@code false} meaning that this isn't the first error
     */
    private boolean promptInputError(boolean firstError, String errorMessage) {
        out.print(AnsiCode.CLEAR_LINE);
        if (!firstError) {
            out.print(AnsiCode.CLEAR_LINE);
        }

        out.println(errorMessage);
        return false;
    }

    /**
     * Prompts a generic error
     *
     * @param errorMessage the error message
     * @param close        {@code true} if you want to close the shell
     */
    private void promptError(String errorMessage, boolean close) {
        CliPrinter.clearConsole(out);

        out.println("ERROR: " + errorMessage);

        if (close) {
            out.println("\nPress ENTER to exit");
            in.nextLine();
            System.exit(1);
        }
    }

    @Override
    public void reload() {
        try {
            client.sendMessage(MessageBuilder.buildReloadRequest(client.getToken(), getPlayer(), askWeapon()));
        } catch (IOException | WeaponCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }
    }

    private WeaponCard askWeapon() {
        UserPlayer player = getPlayer();
        WeaponCard[] weapons = player.getWeapons();
        int choose;

        do {
            out.println("Choose the weapon:");
            for (int i = 0; i < weapons.length; i++) {
                out.println("\t" + (i + 1) + " - " + weapons[i].getName());
            }
            choose = readInt(1, weapons.length);
        } while (choose <= 0 || choose > weapons.length);

        return weapons[choose - 1];
    }

    @Override
    public void shoot() {
        printMap();

        WeaponCard weapon = askWeapon();
        int effect = askWeaponEffect(weapon);

        try {
            client.sendMessage(MessageBuilder.buildShootRequest(client.getToken(), getPlayer(), weapon, effect));
        } catch (IOException | WeaponCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }
    }

    @Override
    public void moveAndPick() {
        printMap();

        // todo: porco dio devo accattarmi se è una weapon, se può essere pagata con il mana e tutte ste menate... amen, se deve pickuppare un powerup o salcazzo cos'altro
    }

    @Override
    public void move() {
        printMap();

        try {
            client.sendMessage(MessageBuilder.buildMoveRequest(client.getToken(), getPlayer(), getCoordinates()));
        } catch (IOException e) {
            promptError(e.getMessage(), true);
        }
    }

    @Override
    public void spawn() {
        printPowerups();

        PowerupCard powerupCard = askPowerupSpawn();
        List<PowerupCard> powerupCards = getPowerups();

        try {
            client.sendMessage(MessageBuilder.buildDiscardPowerupRequest(client.getToken(), powerupCards, powerupCard, getUsername()));
        } catch (IOException | PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }

    }

    private int askWeaponEffect(WeaponCard weapon) {
        // todo
        return readInt();
    }

    @Override
    public void firstPlayerCommunication(String username) {
        if (username.equals(getUsername())) {
            out.println("You are the first player");
        } else {
            out.println("The first player is: " + getFirstPlayer().getUsername());
        }
    }

    @Override
    public void waitTurn() {
        out.println("Wait for your turn...");
        out.println();
    }

    @Override
    public void gameStateUpdate(GameSerialized gameSerialized) {
        printMap();
        out.println();
        printPlayerBoard();
    }

    /**
     * This method asks the user what move he wants to make in this stage of the round, he even asks him if he wants to print to video the map, the player boards, his weapons and his mana.
     * This method returns the choice made by the user, if the choice is a print, the user is asked again what he wants to do until he chooses an action
     *
     * @return the PossibleAction chosen by the user
     */
    @Override
    public PossibleAction askAction() {
        int choose;
        List<PossibleAction> possibleActions = getPossibleActions();

        out.println("Choose the next move:");

        for (int i = 0; i < possibleActions.size(); i++) {
            out.println("\t" + (i + 1) + " - " + possibleActions.get(i));
        }

        // TODO: print map, print player boards, print weapons, print mana

        choose = readInt(1, possibleActions.size());
        return possibleActions.get(choose - 1);
    }

    private PowerupCard askPowerupSpawn() {
        List<PowerupCard> powerups = getPowerups();

        out.println();
        out.println("Where do you want to spawn?");
        for (int i = 0; i < powerups.size(); i++) {
            out.println("\t" + i + " - " + CliPrinter.toStringPowerUpCard(powerups.get(i)) + " (" + Ammo.toColor(powerups.get(i).getValue()) + " room)");
        }

        out.println();
        boolean firstError = true;
        int choose = 0;

        do {
            if (choose >= powerups.size() || choose < 0) {
                firstError = promptInputError(firstError, "Value not valid");
            }

            out.print(">>> ");

            if (in.hasNextInt()) {
                choose = in.nextInt();
            } else {
                choose = -1;
                in.nextLine();
                firstError = promptInputError(firstError, "Value not valid");
            }
        } while (choose < 0 || choose >= powerups.size());

        return powerups.get(choose);
    }

    @NotNull
    @Contract(" -> new")
    private PlayerPosition getCoordinates() {
        boolean exit = false;
        boolean firstError = true;

        int x = -1;
        int y = -1;

        if (in.hasNextLine()) in.nextLine();

        out.println("Write the new coordinates " + (getPlayer().isDead() ? "(0,0)" : getPlayer().getPosition()) + ":");
        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                String[] split = in.nextLine().split(",");

                if (split.length != 2)
                    firstError = promptInputError(firstError, "Wrong input (must be like \"0,0\" or \"0, 0\")");
                else {
                    try {
                        x = Integer.parseInt(split[0].trim());
                        y = Integer.parseInt(split[1].trim());

                        exit = true;
                    } catch (NumberFormatException e) {
                        firstError = promptInputError(firstError, "Wrong input (must be like \"0,0\" or \"0, 0\")");
                    }
                }
            }
        } while (!exit);

        return new PlayerPosition(x, y);
    }

    @Override
    public boolean askBotMove() {
        if (!getUserPlayerState().equals(UserPlayerState.SECOND_ACTION)) {
            //TODO: do you want to move terminator now?
        } else {
            //TODO: you must move terminator the next move
        }

        return true;
    }

    @Override
    public boolean askReload() {
        return false;
    }

    @Override
    public void botMove() {
        // TODO
    }

    private void printMap() {
        CliPrinter.clearConsole(out);
        CliPrinter.printMap(out, getGameSerialized());
    }

    private void printPowerups() {
        CliPrinter.printPowerups(out, getPowerups().toArray(PowerupCard[]::new));
    }

    private void printPlayerBoard() {
        CliPrinter.printPlayerBoards(out, getGameSerialized());
    }

    private int readInt() {
        return readInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private int readInt(int minVal, int maxVal) {
        boolean firstError = true;
        boolean accepted = false;
        int choose = 0;
        do {
            out.print(">>> ");
            if (in.hasNextInt()) {
                choose = in.nextInt();
                if (choose >= minVal && choose <= maxVal) accepted = true;
                else firstError = promptInputError(firstError, "Input not valid!");
            } else {
                firstError = promptInputError(firstError, "Invalid integer!");
                in.nextLine();
            }
        } while (!accepted);

        return choose;
    }
}
