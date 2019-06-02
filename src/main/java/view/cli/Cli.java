package view.cli;

import controller.ClientRoundManager;
import enumerations.*;
import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.actions.WeaponCardsNotFoundException;
import exceptions.player.ClientRoundManagerException;
import exceptions.player.PlayerNotFoundException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Cli implements ClientUpdateListener {
    private final Object lock = new Object();
    private final Object waiter = new Object();

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
    private Player firstPlayer; //the first player to play

    private boolean isTerminator; //terminator in this game
    private boolean terminatorMoved; //terminator already move in the current round
    private boolean firstRound;
    private boolean yourRound; //set to true when receive message that is my round

    private ClientRoundManager roundManager; //manage the rounds of this client
    private List<PossibleAction> possibleActions; //contains the possible actions for this round in a specific UserPlayerState

    public Cli() {
        this.in = new Scanner(System.in);
        this.out = new AdrenalinePrintStream();
        this.timerTime = 0;
        this.started = false;
        this.finished = false;
        this.firstRound = true;
        this.yourRound = false;
        this.terminatorMoved = false;
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

        clientUpdater = new ClientUpdater(client, this, waiter);


        timer = new Timer();
        timerTask = new LobbyTimer(() -> {
            boolean start;

            synchronized (lock) {
                start = this.started;
            }

            if (start) {
                timer.cancel();
                startGame();
            }
        });
        timer.schedule(timerTask, 1000, 1000);
    }

    private void startGame() {
        // TODO:  terminator present
        roundManager = new ClientRoundManager((UserPlayer) getPlayer(username), false);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                doSomething();
            } catch (InterruptedException e) {
                Logger.getGlobal().severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
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
        out.println("\nServer Port: " + port);

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

    private void doSomething() throws InterruptedException {
        if (firstRound) { // first round
            if (firstPlayer.getUsername().equals(username)) { // first player to play
                out.println("You are the first player");
                yourRound = true;
            } else { // other players
                out.println("The first player is: " + firstPlayer.getUsername());
            }
            firstRound = false;
        }

        if (yourRound) { // if the first round
            playRound();
        } else {
            out.println("Wait for your turn...");
            out.println();
            // wait while ClientUpdater receive something
            synchronized (waiter) {
                waiter.wait();
            }
        }
    }

    /**
     * Play the entire round of this player
     */
    private void playRound() {
        do {
            makeMove();
        } while (roundManager.roundEnded());
    }

    /**
     *
     */
    private void makeMove() {
        // TODO: player is dead
        boolean reloaded;
        boolean terminatorMove;
        switch (roundManager.getPlayerState()) {
            case BEGIN:
                roundManager.beginRound((UserPlayer) getPlayer(username));

                terminatorMove = askTerminator();
                roundManager.nextMove((UserPlayer) getPlayer(username), false, terminatorMoved);
                this.terminatorMoved = terminatorMove;
                break;

            case FIRST_MOVE:
            case SECOND_MOVE:
                reloaded = firstSecondMove();
                terminatorMove = askTerminator();
                roundManager.nextMove((UserPlayer) getPlayer(username), reloaded, terminatorMove);
                break;

            case TERMINATOR_MOVE:
                // TODO: move of terminator
                break;

            case END:
                // TODO: end round
                terminatorMoved = false;
                roundManager.endRound();
                break;

            default:
                throw new ClientRoundManagerException("Cannot be here");
        }
    }

    /**
     * Causes the user to perform all the moves it can make in this stage of this round
     *
     * @return true if player reload his weapons, otherwise false
     */
    private boolean firstSecondMove() {
        possibleActions = roundManager.possibleActions();
        boolean reload = false;

        switch (askActions(false)) {
            case MOVE:
                actionMove();
                break;
            case MOVE_AND_PICK:
                actionMoveAndPick();
                break;
            case SHOOT:
                actionShoot();
                break;
            case RELOAD:
                actionReload();
                reload = true;
                break;
            case ADRENALINE_PICK:
                // TODO
                break;
            case ADRENALINE_SHOOT:
                // TODO
                break;
            case FRENZY_MOVE:
                // TODO
                break;
            case FRENZY_PICK:
                // TODO
                break;
            case FRENZY_SHOOT:
                // TODO
                break;
            case LIGHT_FRENZY_PICK:
                // TODO
                break;
            case LIGHT_FRENZY_SHOOT:
                // TODO
                break;

            default:
                throw new ClientRoundManagerException("cannot be here");
        }

        if (reload) {
            switch (askActions(true)) {
                case MOVE:
                    actionMove();
                    break;
                case MOVE_AND_PICK:
                    actionMoveAndPick();
                    break;
                case SHOOT:
                    actionShoot();
                    break;
                case ADRENALINE_PICK:
                    // TODO
                    break;
                case ADRENALINE_SHOOT:
                    // TODO
                    break;
                case FRENZY_MOVE:
                    // TODO
                    break;
                case FRENZY_PICK:
                    // TODO
                    break;
                case FRENZY_SHOOT:
                    // TODO
                    break;
                case LIGHT_FRENZY_PICK:
                    // TODO
                    break;
                case LIGHT_FRENZY_SHOOT:
                    // TODO
                    break;

                default:
                    throw new ClientRoundManagerException("cannot be here");
            }
        }

        return reload;
    }

    private Message actionReload() {
        Message message = null;

        try {
            message = MessageBuilder.buildReloadRequest(client.getToken(), (UserPlayer) getPlayer(username), askWeapon());
            client.sendMessage(message);
        } catch (IOException | WeaponCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }

        return message;
    }

    private WeaponCard askWeapon() {
        UserPlayer player = (UserPlayer) getPlayer(username);
        WeaponCard[] weapons = player.getWeapons();
        int choose;

        do {
            out.println("Choose the weapon:");
            for (int i = 0; i< weapons.length; i++) {
                out.println("\t" + (i + 1) + " - " + weapons[i].getName());
            }
            choose = readInt(1, weapons.length);
        } while (choose <= 0 || choose > weapons.length);

        return weapons[choose - 1];
    }

    private Message actionShoot() {
        Message message = null;
        printMap();

        WeaponCard weapon = askWeapon();
        int effect = askWeaponEffect(weapon);

        try {
            message = MessageBuilder.buildShootRequest(client.getToken(), (UserPlayer) getPlayer(username), weapon, effect);
            client.sendMessage(message);
        } catch (IOException | WeaponCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }

        return message;
    }

    private int askWeaponEffect(WeaponCard weapon) {
        // todo
        return readInt();
    }

    private Message actionMoveAndPick() {
        Message message;
        printMap();

        // todo: porco dio devo accattarmi se è una weapon, se può essere pagata con il mana e tutte ste menate... amen, se deve pickuppare un powerup o salcazzo cos'altro

        return null;
    }

    private Message actionMove() {
        Message message;
        printMap();

        message = MessageBuilder.buildMoveRequest(client.getToken(), getPlayer(username), getCoordinates());

        try {
            client.sendMessage(message);
        } catch (IOException e) {
            promptError(e.getMessage(), true);
        }

        return message;
    }

    private void printMap() {
        synchronized (lock) {
            CliPrinter.printMap(out, gameSerialized);
        }
    }

    /**
     * This method asks the user what move he wants to make in this stage of the round, he even asks him if he wants to print to video the map, the player boards, his weapons and his mana.
     * This method returns the choice made by the user, if the choice is a print, the user is asked again what he wants to do until he chooses an action
     *
     * @param withoutReload if true, it removes the possibility of reload from the user's choices
     * @return the PossibleAction chosen by the user
     */
    private PossibleAction askActions(boolean withoutReload) {
        int choose;

        if (withoutReload) possibleActions.remove(PossibleAction.RELOAD);

        out.println("Choose the next move:");

        for (int i = 0; i < possibleActions.size(); i++) {
            out.println("\t" + (i + 1) + " - " + possibleActions.get(i));
        }

        // TODO: print map, print player boards, print weapons, print mana

        choose = readInt(1, possibleActions.size());
        return possibleActions.get(choose - 1);
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
                List<PowerupCard> powerupCards = getPowerUps();

                try {
                    client.sendMessage(MessageBuilder.buildDiscardPowerupRequest(client.getToken(), powerupCards, powerupCard, username));
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

    private List<PowerupCard> getPowerUps() {
        synchronized (lock) {
            return gameSerialized.getPowerUps();
        }
    }

    private PowerupCard askPowerUpSpawn() {
        List<PowerupCard> powerups = getPowerUps();

        out.println();
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

        out.println("Write the new coordinates " + (getPlayer(username).isDead() ? "(0,0)" : getPlayer(username).getPosition()) + ":");
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
                        CliPrinter.printMap(out, gameSerialized);
                        out.println();
                        CliPrinter.printPlayerBoards(out, gameSerialized);
                    }
                    break;

                case READY:
                    GameStartMessage gameStartMessage = (GameStartMessage) message;
                    synchronized (lock) {
                        firstPlayer = getPlayer(gameStartMessage.getFirstPlayer());
                        isTerminator = gameSerialized.isTerminatorPresent();
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

    private Terminator getTerminator() {
        synchronized (lock) {
            return gameSerialized.getTerminator();
        }
    }

    private Player getPlayer(String username) {
        synchronized (lock) {
            Player player = gameSerialized.getPlayers().stream().filter(p -> p.getUsername().equals(username)).findFirst().orElse(null);
            if (player == null) throw new PlayerNotFoundException("player not found, cannot continue with the game");
            return player;
        }
    }

    private boolean askTerminator() {
        if (roundManager.terminatorPresent() && !roundManager.terminatorMoved() && !terminatorMoved) {
            if (!roundManager.getPlayerState().equals(UserPlayerState.SECOND_MOVE)) {
                //TODO: do you want to move terminator now?
            } else {
                //TODO: you must move terminator the next move
            }
            return true;
        }

        return false;
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
