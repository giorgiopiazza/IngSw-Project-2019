package view.cli;

import enumerations.*;
import exceptions.AdrenalinaRuntimeException;
import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.player.PlayerNotFoundException;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.player.Player;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.client.*;
import network.message.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import utility.LobbyTimer;
import utility.MessageBuilder;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Cli implements ClientUpdateListener {
    private final Object lock = new Object();

    private Client client;
    private Scanner in;
    private AdrenalinePrintStream out;
    private String username;
    private PlayerColor playerColor;
    private GameSerialized gameSerialized;
    private int timerTime;

    private Timer timer;
    private TimerTask timerTask;
    private ClientUpdater clientUpdater;

    private boolean started;
    private boolean finished;
    private List<Player> winners;
    private PossibleGameState gameState;
    private Player firstPlayer;

    public Cli() {
        this.in = new Scanner(System.in);
        this.out = new AdrenalinePrintStream();
        this.timerTime = 0;
        this.started = false;
        this.finished = false;
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

        clientUpdater = new ClientUpdater(client, this);


        timer = new Timer();
        timerTask = new LobbyTimer(() -> {
            synchronized (lock) {
                if (!started) started = false;
                else {
                    timer.cancel();
                    checkStartGame();
                }
            }
        });
        timer.schedule(timerTask, 1000, 1000);
    }

    private void checkStartGame() {
        while (!Thread.currentThread().isInterrupted()) {
            doSomething();
        }
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

        int port = askPort(connection);
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

    private int askPort(int connection) {
        boolean firstError = true;

        int defaultPort = connection == 0 ? 2727 : 7272;
        out.println("\nEnter the server port (default " + defaultPort + "):");

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
            } else if (in.hasNextLine()) {
                in.nextLine();
                return defaultPort;
                // if (in.nextLine().equals(""))
                //firstError = promptInputError(firstError, "Invalid integer!");
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
        out.print(AnsiCode.CLEAR_LINE);
        if (!firstError) {
            out.print(AnsiCode.CLEAR_LINE);
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

    private void doSomething() {
        out.println("Choose your next move:");
        out.println("\t0 - Print map");
        out.println("\t1 - Print players board");
        out.println("\t2 - Move (only your turn)");
        out.println("\t3 - Shoot (only your turn)");
        out.println("\t4 - Move and pickup power up or weapon (only your turn)");
        out.println("\t5 - Spawn (only your turn)");
        out.println("\t6 - Pass turn (only your turn)");
        out.println();

        boolean accepted = false;
        boolean firstError = true;

        int choose = -1;

        do {
            out.print(">>> ");
            if(in.hasNextInt()) {
                choose = in.nextInt();
                if (choose >= 0 && choose <= 6) accepted = true;
                else firstError = promptInputError(firstError, "Input not valid!");
            } else {
                firstError = promptInputError(firstError, "Invalid integer!");
                in.nextLine();
            }
        } while(!accepted);

        switchChooses(choose);
    }

    private void switchChooses(int choose) {
        Player player;
        Message message;

        switch (choose) {
            case 0:
                synchronized (lock) {
                    CliPrinter.printMap(out, gameSerialized);
                }
                break;

            case 1:
                synchronized (lock) {
                    CliPrinter.printPlayerBoards(out, gameSerialized);
                }
                break;

            case 2:
                synchronized (lock) {
                    CliPrinter.printMap(out, gameSerialized);
                }

                player = getPlayer(username);

                message = MessageBuilder.buildMoveRequest(client.getToken(), player, getCoordinates());

                try {
                    client.sendMessage(message);
                } catch (IOException e) {
                    promptError(e.getMessage(), true);
                }
                break;

            case 3:
                break;

            case 4:
                break;

            case 5:
                PowerupCard powerupCard = askPowerUpSpawn();
                player = getPlayer(username);
                try {
                    client.sendMessage(MessageBuilder.buildDiscardPowerupRequest(client.getToken(), (UserPlayer) player, powerupCard));
                } catch (IOException | PowerupCardsNotFoundException e) {
                    promptError(e.getMessage(), true);
                }
                break;

            case 6:
                player = getPlayer(username);
                try {
                    client.sendMessage(MessageBuilder.buildPassTurnRequest(client.getToken(), (UserPlayer) player));
                } catch (IOException e) {
                    promptError(e.getMessage(), true);
                }
                break;
        }
    }

    private PowerupCard askPowerUpSpawn() {
        UserPlayer player = (UserPlayer) getPlayer(username);

        List<PowerupCard> powerups = Arrays.asList(player.getPowerups());

        powerups.add(player.getSpawningCard());

        out.println("Where do you want to re spawn?");
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

        out.println("Write the new coordinates (0,1):");
        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                String[] split = in.nextLine().split(",");

                if (split.length != 2) firstError = promptInputError(firstError, "Wrong input (must be like \"0,0\" or \"0, 0\")");
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
    public void onUpdate(List<Message> messages) {
        for (Message message : messages) {
            out.println(message);

            switch (message.getContent()) {
                case RESPONSE:
                    Response response = (Response) message;
                    if (response.getStatus().equals(MessageStatus.ERROR)) {
                        // TODO: torno indietro con la macchina a stati
                        // Sei uno stronzo
                    } else {
                        // TODO: vado avanti con la macchina a stati
                    }
                    break;

                case GAME_STATE:
                    GameStateMessage stateMessage = (GameStateMessage) message;
                    out.println();
                    synchronized (lock) {
                        gameSerialized = stateMessage.getGameSerialized();
                        out.println(gameSerialized.toString());
                        out.println();
                    }
                    break;

                case READY:
                    GameStartMessage gameStartMessage = (GameStartMessage) message;
                    firstPlayer = getPlayer(gameStartMessage.getFirstPlayer());
                    synchronized (lock) {
                        started = true;
                    }
                    break;

                case LAST_RESPONSE:
                    WinnersResponse winnersList = (WinnersResponse) message;
                    synchronized (lock) {
                        this.finished = true;
                        this.winners = winnersList.getWinners();
                    }
                    break;

                case DISCONNECTION:
                    break;

                default:
            }

            Logger.getGlobal().log(Level.INFO, "{0}", message);
        }
    }

    private Player getPlayer(String username) {
        synchronized (lock) {
            Player player = gameSerialized.getPlayers().stream().filter(p -> p.getUsername().equals(username)).findFirst().orElse(null);
            if (player == null) throw new PlayerNotFoundException("player not found, cannot continue with the game");
            return player;
        }
    }
}
