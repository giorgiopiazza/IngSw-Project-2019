package view.cli;

import controller.ClientGameManager;
import enumerations.*;
import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.actions.WeaponCardsNotFoundException;
import exceptions.game.InexistentColorException;
import exceptions.map.InvalidSpawnColorException;
import exceptions.utility.InvalidPropertiesException;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.cards.effects.Effect;
import model.map.GameMap;
import model.map.SpawnSquare;
import model.map.Square;
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

    private static final String SEND_ERROR = "Error while sending the request";
    private static final String INVALID_STRING = "Invalid String!";
    private static final String TARGET_NUM = "targetNum";
    private static final String MAX_TARGET_NUM = "maxTargetNum";
    private static final String MOVE = "move";
    private static final String MOVE_TARGET = "moveTarget";
    private static final String MAX_MOVE_TARGET = "maxMoveTarget";
    private static final String MOVE_TARGET_BEFORE = "moveTargetBefore";
    private static final String TP = "tp";

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
        doConnection();
        doLobbyJoin();

        startUpdater(client);
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
                firstError = promptInputError(firstError, INVALID_STRING);
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
                firstError = promptInputError(firstError, INVALID_STRING);
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
                firstError = promptInputError(firstError, INVALID_STRING);
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

        if (!connected) {
            client.close();
            client = null;
            doConnection();
        }
    }

    private void doConnection() {
        askUsername();
        askConnection();
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
                firstError = promptInputError(firstError, INVALID_STRING);
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
            client.sendMessage(MessageBuilder.buildGetInLobbyMessage(client.getToken(), getUsername(), getPlayerColor(), false));
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

                if (response.getStatus() == MessageStatus.ERROR) {
                    out.println(response.getMessage());
                    out.println();
                    doLobbyJoin();
                } else {
                    out.println("You joined the lobby!\n\nWait for the game to start...");
                }
            }
        }
    }

    private void doLobbyJoin() {
        askColor();
        askLobbyJoin();
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
        WeaponCard[] playersWeapons = getPlayer().getWeapons();
        ArrayList<Integer> rechargingWeapons = new ArrayList<>();
        ArrayList<Integer> paymentPowerups = new ArrayList<>();

        if (playersWeapons.length == 0) {
            cancelAction("You have no weapon to recharge");
            return;
        }

        CliPrinter.clearConsole(out);
        printWeapons(playersWeapons);

        out.println("Choose the weapons you want to reload (-1 to stop choosing)");
        for (int i = 0; i < playersWeapons.length; ++i) {
            int tempChoose = askWeapon(-1);
            if (tempChoose == -1) break;
            else rechargingWeapons.add(tempChoose);
        }

        if (!getPowerups().isEmpty()) {
            paymentPowerups = askPaymentPowerups();
        }

        try {
            if (paymentPowerups.isEmpty()) {
                if (!sendRequest(MessageBuilder.buildReloadRequest(client.getToken(), getPlayer(), rechargingWeapons))) {
                    promptError(SEND_ERROR, true);
                }
            } else {
                if (!sendRequest(MessageBuilder.buildReloadRequest(client.getToken(), getPlayer(), rechargingWeapons, paymentPowerups))) {
                    promptError(SEND_ERROR, true);
                }
            }
        } catch (WeaponCardsNotFoundException | PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), false);
        }
    }

    @Override
    public void passTurn() {
        if (!sendRequest(MessageBuilder.buildPassTurnRequest(client.getToken(), getPlayer()))) {
            promptError(SEND_ERROR, true);
        }
    }

    private int askWeapon(int minVal) {
        UserPlayer player = getPlayer();
        WeaponCard[] weapons = player.getWeapons();
        int choose;

        printWeapons(player.getWeapons());
        if (weapons.length == 0) return -1;

        do {
            out.println("Choose the weapon:");
            choose = readInt(minVal, weapons.length - 1);
        } while (choose < minVal || choose > weapons.length - 1);

        return choose;
    }

    private int askWeaponEffect(WeaponCard weapon) {
        WeaponCard[] chosenWeapon = new WeaponCard[]{weapon};
        int choose;

        printWeapons(chosenWeapon);
        do {
            out.println("Choose the weapon's effect:");
            choose = readInt(0, weapon.getSecondaryEffects().size());
        } while (choose < 0 || choose > weapon.getSecondaryEffects().size());

        return readInt();
    }

    private ArrayList<Integer> askPaymentPowerups() {
        ArrayList<Integer> paymentPowerups = new ArrayList<>();
        int tempChoose;

        printPowerups();

        do {
            out.println("Choose the powerups you want to pay with (-1 to stop choosing):");
            tempChoose = readInt(-1, getPowerups().size());
            if (tempChoose == -1) return paymentPowerups;
            paymentPowerups.add(tempChoose);
        } while (paymentPowerups.size() < getPowerups().size());

        return paymentPowerups;
    }

    private ShootRequest.ShootRequestBuilder buildShootRequest(Effect chosenEffect, ShootRequest.ShootRequestBuilder shootRequestBuilder) {
        Map<String, String> effectProperties = chosenEffect.getProperties();
        TargetType[] targets = chosenEffect.getTargets();
        ArrayList<String> targetsChosen = new ArrayList<>();

        // targets input ask
        for (TargetType target : targets) {
            switch (target) {
                case PLAYER:
                    targetsChosen = askTargetsUsernames(effectProperties);
                    shootRequestBuilder.targetPlayersUsernames(targetsChosen);
                    break;
                case SQUARE:
                    shootRequestBuilder.targetPositions(askTargetSquaresPositions(effectProperties));
                    break;
                case ROOM:
                    shootRequestBuilder.targetRoomColor(askTargetRoomColor(effectProperties));
                    break;
                default:
                    throw new InvalidPropertiesException();
            }
        }

        // now that I have the targets we need to handle the possible move decisions
        if (effectProperties.containsKey(MOVE)) {
            // move is always permitted both before and after, decision is then always asked
            shootRequestBuilder.senderMovePosition(askMovePositionInShoot());
            shootRequestBuilder.moveSenderFirst(askBeforeAfterMove());
        }

        // now that I have handled the Turn Owner movement I have to handle the targets ones
        if (effectProperties.containsKey(MOVE_TARGET) || effectProperties.containsKey(MAX_MOVE_TARGET)) {
            shootRequestBuilder.targetPlayersMovePositions(askTargetsMovePositions(targetsChosen));
        }

        // in the end if the targets movement can be done before or after the shoot action I ask when to the shooter
        if (effectProperties.containsKey(MOVE_TARGET_BEFORE)) {
            shootRequestBuilder.moveTargetsFirst(Boolean.parseBoolean(effectProperties.get(MOVE_TARGET_BEFORE)));
        } else if (effectProperties.containsKey(MOVE_TARGET) || effectProperties.containsKey(MAX_MOVE_TARGET)) {
            shootRequestBuilder.moveTargetsFirst(askBeforeAfterMove());
        }

        return shootRequestBuilder;
    }

    private ArrayList<String> askTargetsUsernames(Map<String, String> effectProperties) {
        ArrayList<String> targetsUsernames;

        if (effectProperties.containsKey(TARGET_NUM)) {
            targetsUsernames = askExactTargets(effectProperties.get(TARGET_NUM));
        } else if (effectProperties.containsKey(MAX_TARGET_NUM)) {
            targetsUsernames = askMaxTargets(effectProperties.get(MAX_TARGET_NUM));
        } else {
            throw new InvalidPropertiesException();
        }

        return targetsUsernames;
    }

    private ArrayList<String> askExactTargets(String exactStringNum) {
        int exactIntNum = Integer.parseInt(exactStringNum);
        ArrayList<String> chosenTargets = new ArrayList<>();

        do {
            out.println("Choose exactly " + exactIntNum + " target/s for your shoot action:");
            chosenTargets.add(readTargetUsername(getGameSerialized().getPlayers(), false));
        } while (chosenTargets.size() < exactIntNum);

        return chosenTargets;
    }

    private ArrayList<String> askMaxTargets(String maxStringNum) {
        int maxIntNum = Integer.parseInt(maxStringNum);
        ArrayList<String> chosenTargets = new ArrayList<>();

        do {
            String tempTarget;
            out.println("Choose up to " + maxIntNum + " target/s for your shoot action (-1 to stop choosing):");
            tempTarget = readTargetUsername(getGameSerialized().getPlayers(), true);
            if (tempTarget.equals("-1") && chosenTargets.size() > 1) return chosenTargets;
            chosenTargets.add(tempTarget);
        } while (chosenTargets.size() < maxIntNum);

        return chosenTargets;
    }

    private ArrayList<PlayerPosition> askTargetSquaresPositions(Map<String, String> effectProperties) {
        ArrayList<PlayerPosition> targetSquaresPositions;

        if (effectProperties.containsKey(TARGET_NUM)) {
            targetSquaresPositions = askExactSquares(effectProperties.get(TARGET_NUM));
        } else if (effectProperties.containsKey(MAX_TARGET_NUM)) {
            targetSquaresPositions = askMaxSquares(effectProperties.get(MAX_TARGET_NUM));
        } else {
            throw new InvalidPropertiesException();
        }

        return targetSquaresPositions;
    }

    private ArrayList<PlayerPosition> askExactSquares(String exactStringNum) {
        int exactIntNum = Integer.parseInt(exactStringNum);
        ArrayList<PlayerPosition> chosenSquares = new ArrayList<>();

        do {
            out.println("Choose exactly " + exactIntNum + " target/s squares for your shoot action:");
            chosenSquares.add(askCoordinates());
        } while (chosenSquares.size() < exactIntNum);

        return chosenSquares;
    }

    private ArrayList<PlayerPosition> askMaxSquares(String maxStringNum) {
        int maxIntNum = Integer.parseInt(maxStringNum);
        ArrayList<PlayerPosition> chosenSquares = new ArrayList<>();

        do {
            PlayerPosition tempPos;
            out.println("Choose up to " + maxIntNum + " target/s squares for your shoot action (-1 to stop choosing):");
            tempPos = askCoordinates();
            if (tempPos.getCoordX() == -1 && chosenSquares.size() > 1) return chosenSquares;
            chosenSquares.add(tempPos);
        } while (chosenSquares.size() < maxIntNum);

        return chosenSquares;
    }

    private RoomColor askTargetRoomColor(Map<String, String> effectProperties) {
        // remember that a room target is always 1, infact we just need to verify that the property is present, then we are sure its going to be one
        if (effectProperties.containsKey(TARGET_NUM)) {
            out.println("Choose the color of the target room for your shoot action:");

            return readRoomColor();
        } else {
            throw new InvalidPropertiesException();
        }
    }

    private PlayerPosition askMovePositionInShoot() {
        out.println("Choose the moving position for your shoot action:");
        return askCoordinates();
    }

    private boolean askBeforeAfterMove() {
        out.println("Choose if you want to move 'before' or 'after' shooting:");
        return readMoveDecision();
    }

    private ArrayList<PlayerPosition> askTargetsMovePositions(ArrayList<String> targetsChosen) {
        ArrayList<PlayerPosition> targetsMovePositions = new ArrayList<>();

        out.println("Choose each targets' moving position!");

        for (String target : targetsChosen) {
            out.println("Choose " + target + " moving position:");
            targetsMovePositions.add(askCoordinates());
        }

        return targetsMovePositions;
    }

    @Override
    public void botSpawn() {
        GameMap gameMap = getGameSerialized().getGameMap();
        PlayerPosition botSpawnPosition = null;
        boolean correctColor;

        out.println("Choose the Color of the square where to Spawn the bot:");
        do {
            try {
                botSpawnPosition = gameMap.getSpawnSquare(readRoomColor());
                correctColor = true;
            } catch (InvalidSpawnColorException e) {
                correctColor = false;
            }
        } while (!correctColor);

        if (!sendRequest(MessageBuilder.buildTerminatorSpawnRequest(client.getToken(), getGameSerialized().getBot(), gameMap.getSquare(botSpawnPosition)))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void adrenalinePick() {
        out.println("ADRENALINE ACTION!\n");
        moveAndPick();
    }

    @Override
    public void adrenalineShoot() {
        out.println("ADRENALINE ACTION!\n");
        ShootRequest.ShootRequestBuilder shootRequestBuilt;
        PlayerPosition adrenalineMovePosition;

        out.println("Choose the moving square for your adrenaline shoot action (same position not to move):");
        adrenalineMovePosition = askCoordinates();

        // now that I also know the moving position needed for the adrenaline shoot action I can build the shoot request and send it
        shootRequestBuilt = sharedShootBuilder();

        if (shootRequestBuilt == null) {
            return;
        } else {
            shootRequestBuilt.adrenalineMovePosition(adrenalineMovePosition);
        }

        if (!sendRequest(MessageBuilder.buildShootRequest(shootRequestBuilt))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void frenzyMove() {
        out.println("FRENZY ACTION! \n");   // succhiami il cazzo sonar ti owno aggiungendo uno spazio ad ogni stringa e stai muto
        move();
    }

    @Override
    public void frenzyPick() {
        out.println("FRENZY ACTION!\n");
        moveAndPick();
    }

    @Override
    public void frenzyShoot() {
        out.println("FRENZY ACTION!\n");
        sharedFrenzyShoot();
    }

    @Override
    public void lightFrenzyPick() {
        out.println("LIGHT FRENZY ACTION");
        moveAndPick();
    }

    @Override
    public void lightFrenzyShoot() {
        out.println("LIGHT FRENZY ACTION");
        sharedFrenzyShoot();
    }

    private void sharedFrenzyShoot() {
        ShootRequest.ShootRequestBuilder shootRequestBuilt;
        PlayerPosition frenzyMovePosition;
        ArrayList<Integer> rechargingWeapons = new ArrayList<>();

        out.println("Choose the moving square for your frenzy shoot action (same position not to move):");
        frenzyMovePosition = askCoordinates();

        out.println("Do you want to recharge your weapons before shooting (-1 to stop choosing)?");
        for (int i = 0; i < getPlayer().getWeapons().length; ++i) {
            int tempChoose = askWeapon(-1);
            if (tempChoose == -1) break;
            rechargingWeapons.add(tempChoose);
        }

        // now that I have everything I need more for a frenzy shoot I build the normal shoot request, add these and send it
        shootRequestBuilt = sharedShootBuilder();
        if (shootRequestBuilt == null) {
            return;
        } else {
            shootRequestBuilt.adrenalineMovePosition(frenzyMovePosition).rechargingWeapons(rechargingWeapons);
        }

        if (!sendRequest(MessageBuilder.buildShootRequest(shootRequestBuilt))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void shoot() {
        ShootRequest.ShootRequestBuilder shootRequestBuilder = sharedShootBuilder();

        if (shootRequestBuilder == null) return;

        if (!sendRequest(MessageBuilder.buildShootRequest(shootRequestBuilder))) {
            promptError(SEND_ERROR, true);
        }
    }

    private ShootRequest.ShootRequestBuilder sharedShootBuilder() {
        ShootRequest.ShootRequestBuilder shootRequestBuilder;
        ArrayList<Integer> paymentPowerups = new ArrayList<>();
        Effect chosenEffect;

        WeaponCard[] weapons = getPlayer().getWeapons();
        if (weapons.length == 0) {
            cancelAction("You have no weapon to use");
            return null;
        }

        printMap();
        out.println("Care your Ammo before choosing to shoot: ");
        printAmmo();

        int weapon = askWeapon(0);
        if (weapon == -1) return null;
        int effect = askWeaponEffect(getPlayer().getWeapons()[weapon]);
        if (!getPowerups().isEmpty()) {
            paymentPowerups = askPaymentPowerups();
        }

        // normal shoot does not require recharging weapons
        shootRequestBuilder = new ShootRequest.ShootRequestBuilder(client.getUsername(), client.getToken(), weapon, effect, null).paymentPowerups(paymentPowerups);

        // now we can build the fireRequest specific to each chosen weapon
        if (effect == 0) {
            chosenEffect = getPlayer().getWeapons()[weapon].getBaseEffect();
        } else {
            chosenEffect = getPlayer().getWeapons()[weapon].getSecondaryEffects().get(effect - 1);
        }

        return buildShootRequest(chosenEffect, shootRequestBuilder);
    }

    private void buildPickWeaponRequest(PlayerPosition newPos) {
        SpawnSquare weaponSquare = (SpawnSquare) getGameSerialized().getGameMap().getSquare(newPos);
        ArrayList<Integer> paymentPowerups = new ArrayList<>();
        WeaponCard pickingWeapon;
        WeaponCard discardingCard = null;

        if (weaponSquare.getWeapons().length == 0) { // very particular case in which a spawn square has no powerups
            // pick action not allowed
            return;
        }

        pickingWeapon = askPickWeapon(weaponSquare);
        if (!getPowerups().isEmpty()) {
            paymentPowerups = askPaymentPowerups();
        }

        // now that we know the WeaponCard the acting player wants to pick, if he has already 3 cards in his hand we ask him which one to discard
        if (getPlayer().getWeapons().length == 3) {
            out.println("You already have 3 weapons in your hand, choose one and discard it!");
            discardingCard = getPlayer().getWeapons()[askWeapon(0)];
        }

        try {
            if (!paymentPowerups.isEmpty()) {
                if (!sendRequest(MessageBuilder.buildMovePickRequest(client.getToken(), getPlayer(), newPos, paymentPowerups, pickingWeapon, discardingCard))) {
                    promptError(SEND_ERROR, true);
                }
            } else {
                if (!sendRequest(MessageBuilder.buildMovePickRequest(client.getToken(), getPlayer(), newPos, pickingWeapon, discardingCard))) {
                    promptError(SEND_ERROR, true);
                }
            }
        } catch (PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }
    }

    private WeaponCard askPickWeapon(SpawnSquare weaponSquare) {
        WeaponCard[] weapons = weaponSquare.getWeapons();
        int choose;

        CliPrinter.clearConsole(out);
        out.println("Weapons on your moving spawn square are:");
        printWeapons(weaponSquare.getWeapons());

        do {
            out.println("\nChoose the weapon. Your ammo are:\n");
            printAmmo();
            choose = readInt(0, weapons.length - 1);
        } while (choose < 0 || choose > weapons.length - 1);

        return weapons[choose];
    }

    @Override
    public void moveAndPick() {
        PlayerPosition newPos;

        printMap();

        out.println("\nChoose the moving square for your pick action (same position not to move):");
        newPos = askCoordinates();

        Square square = getGameSerialized().getGameMap().getSquare(newPos.getCoordX(), newPos.getCoordY());

        if (square == null) {
            cancelAction("Position not valid");
            return;
        }

        if (square.getSquareType() == SquareType.TILE) {
            if (!sendRequest(MessageBuilder.buildMovePickRequest(client.getToken(), getPlayer(), newPos))) {
                promptError(SEND_ERROR, true);
            }
        } else {
            buildPickWeaponRequest(newPos);
        }
    }

    @Override
    public void move() {
        printMap();
        out.println();

        if (!sendRequest(MessageBuilder.buildMoveRequest(client.getToken(), getPlayer(), askCoordinates()))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void spawn() {
        printMap();
        out.println();
        printPowerups();

        PowerupCard powerupCard = askPowerupSpawn();
        List<PowerupCard> powerupCards = getPowerups();

        try {
            if (!sendRequest(MessageBuilder.buildDiscardPowerupRequest(client.getToken(), powerupCards, powerupCard, getUsername()))) {
                promptError(SEND_ERROR, true);
            }
        } catch (PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }
    }

    @Override
    public void firstPlayerCommunication(String username) {
        if (username.equals(getUsername())) {
            out.println("You are the first player\n");
        } else {
            out.println("The first player is: " + getFirstPlayer() + "\n");
        }
    }

    @Override
    public void notYourTurn() {
        out.println("Wait for your turn...");
    }

    @Override
    public void gameStateUpdate(GameSerialized gameSerialized) {
        printMap();
        out.println();
    }

    @Override
    public void responseError(String error) {
        promptError(error + "\n", false);
    }

    @Override
    public void notifyGameEnd(List<Player> winners) {
        // TODO
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

        printAmmo();
        out.println();
        printPowerupsNum();
        out.println("Choose the next move:");

        for (int i = 0; i < possibleActions.size(); i++) {
            out.println("\t" + (i) + " - " + possibleActions.get(i).getDescription());
        }

        choose = readInt(0, possibleActions.size() - 1);
        return possibleActions.get(choose);
    }

    @Override
    public void powerup() {
        CliPrinter.clearConsole(out);
        printPowerups();

        PowerupCard powerupCard = askPowerupCli();


        if (!powerupCard.getName().equals(NEWTON) && !powerupCard.getName().equals(TELEPORTER)) {
            cancelAction("ERROR: You can't use this powerup!");
            return;
        }

        ArrayList<Integer> powerups = new ArrayList<>();
        powerups.add(getPowerups().indexOf(powerupCard));

        PowerupRequest.PowerupRequestBuilder powerupRequestBuilder = new PowerupRequest.PowerupRequestBuilder(getUsername(), client.getToken(), powerups);

        Effect baseEffect = powerupCard.getBaseEffect();
        Map<String, String> effectProperties = baseEffect.getProperties();

        if (effectProperties.containsKey(TP)) {
            out.println("Choose your teleporting position:");
            powerupRequestBuilder.senderMovePosition(askCoordinates());
        }

        if (effectProperties.containsKey(MAX_MOVE_TARGET)) {
            out.println("Choose target username:");
            powerupRequestBuilder.targetPlayersUsername(askExactTargets("1"));
            powerupRequestBuilder.targetPlayersMovePositions(new ArrayList<>(List.of(askCoordinates())));
        }

        try {
            if (!sendRequest(MessageBuilder.buildPowerupRequest(powerupRequestBuilder))) {
                promptError(SEND_ERROR, true);
            }
        } catch (PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), false);
        }
    }

    @Override
    public void onPlayerDisconnect(String username) {
        out.println("Player " + username + " DISCONNECTED from the game!");
    }

    private void cancelAction(String message) {
        out.println(message);
        cancelAction();
    }

    private void cancelAction() {
        CliPrinter.clearConsole(out);
        reAskAction();
    }

    private PowerupCard askPowerupCli() {
        List<PowerupCard> powerups = getPowerups();
        List<PowerupCard> newList = new ArrayList<>();

        for (PowerupCard powerup : powerups) {
            if (powerup.getName().equals(ClientGameManager.NEWTON) || powerup.getName().equals(ClientGameManager.TELEPORTER)) {
                newList.add(powerup);
            }
        }

        out.println();
        out.println("Which power up do you want to use?");

        for (int i = 0; i < newList.size(); i++) {
            out.println("\t" + i + " - " + CliPrinter.toStringPowerUpCard(newList.get(i)) + " (" + Ammo.toColor(newList.get(i).getValue()) + " room)");
        }

        out.println();

        return powerups.get(readInt(0, powerups.size() - 1));
    }

    private PowerupCard askPowerupSpawn() {
        List<PowerupCard> powerups = getPowerups();

        out.println();
        out.println("Where do you want to spawn?");

        for (int i = 0; i < powerups.size(); i++) {
            out.println("\t" + i + " - " + CliPrinter.toStringPowerUpCard(powerups.get(i)) + " (" + Ammo.toColor(powerups.get(i).getValue()) + " room)");
        }

        out.println();

        int choose = readInt(0, powerups.size() - 1);

        return powerups.get(choose);
    }

    @NotNull
    @Contract(" -> new")
    private PlayerPosition askCoordinates() {
        boolean exit = false;
        boolean firstError = true;

        int x = -1;
        int y = -1;

        if (in.hasNextLine()) {
            String checkStop = in.nextLine();
            if (checkStop.equals("-1")) return new PlayerPosition(x, y);
        }

        // a target is meant both as: target for a moving action or for choosing a target square
        out.println("Write the target moving coordinates " + (getPlayer().isDead() ? "(0,0)" : getPlayer().getPosition()) + ":");
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

    private String readString() {
        return readString("");
    }

    private String readString(String defVal) {
        String readString;

        out.print(">>> ");
        readString = in.nextLine();

        if (readString.equals("")) return defVal;

        return readString;
    }

    @Override
    public void botAction() {
        PlayerPosition newPos;
        String target;

        printMap();

        out.println("Choose the position for the bot action (same position not to move):");
        newPos = askCoordinates();

        out.println("Choose the target for the bot action (-1 not to shoot):");
        target = readBotTarget(getGameSerialized().getPlayers());

        if (!sendRequest(MessageBuilder.buildUseTerminatorRequest(client.getToken(), getGameSerialized().getBot(), newPos, (UserPlayer) getPlayerByName(target)))) {
            promptError(SEND_ERROR, true);
        }
    }

    private void printMap() {
        CliPrinter.clearConsole(out);
        CliPrinter.printMap(out, getGameSerialized());
    }

    private void printPowerups() {
        CliPrinter.printPowerups(out, getPowerups().toArray(PowerupCard[]::new));
    }

    private void printPowerupsNum() {
        out.println("Powerups: " + getPowerups().size() + "\n");
    }

    private void printWeapons(WeaponCard[] weapons) {
        CliPrinter.printWeapons(out, weapons);
    }

    private void printPlayerBoard() {
        CliPrinter.printPlayerBoards(out, getGameSerialized());
    }

    private void printAmmo() {
        CliPrinter.printAmmo(out, getPlayer().getPlayerBoard().getAmmo());
    }

    private int readInt() {
        return readInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private int readInt(int minVal, int maxVal) {
        boolean firstError = true;
        boolean accepted = false;
        int choose = Integer.MIN_VALUE;

        do {
            out.print(">>> ");

            try {
                choose = Integer.valueOf(in.nextLine());

                if (choose >= minVal && choose <= maxVal) accepted = true;
                else firstError = promptInputError(firstError, "Not valid input!");

            } catch (NumberFormatException e) {
                promptInputError(firstError, "Not valid input!");
            }
        } while (!accepted);

        return choose;
    }

    private String readBotTarget(ArrayList<UserPlayer> inGamePlayers) {
        boolean firstError = true;
        boolean accepted = false;
        String chosenTarget;

        do {
            out.print(">>> ");
            chosenTarget = in.nextLine();
            if (chosenTarget.equals("-1")) return null;
            if (!chosenTarget.equals("bot")) {       // no one can shoot itself!
                for (UserPlayer player : inGamePlayers) {
                    if (player.getUsername().equals(chosenTarget)) {
                        accepted = true;
                        break;
                    }
                }
            } else {
                firstError = promptInputError(firstError, "Bot Target Not Valid");
            }
        } while (!accepted);

        return chosenTarget;
    }

    private String readTargetUsername(ArrayList<UserPlayer> inGamePlayers, boolean stoppable) {
        boolean firstError = true;
        boolean accepted = false;
        boolean isTerminatorPresent = getGameSerialized().isBotPresent();

        String chosenTarget;
        do {
            out.print(">>> ");
            chosenTarget = in.nextLine();
            if (stoppable && chosenTarget.equals("-1")) return chosenTarget;
            if (isTerminatorPresent && chosenTarget.equals("bot")) {
                accepted = true;
            } else if (!chosenTarget.equals(getPlayer().getUsername())) {
                for (UserPlayer player : inGamePlayers) {
                    if (player.getUsername().equals(chosenTarget)) {
                        accepted = true;
                        break;
                    }
                }
            } else {
                firstError = promptInputError(firstError, "Target Not Valid");
            }
        } while (!accepted);

        return chosenTarget;
    }

    private RoomColor readRoomColor() {
        boolean firstError = true;
        boolean accepted = false;
        String stringColor;
        RoomColor roomColor = null;

        do {
            out.print(">>> ");
            stringColor = in.nextLine();
            try {
                roomColor = RoomColor.getColor(stringColor);
                accepted = true;
            } catch (InexistentColorException e) {
                firstError = promptInputError(firstError, "Invalid Room Color");
            }
        } while (!accepted);

        return roomColor;
    }

    private boolean readMoveDecision() {
        boolean firstError = true;
        boolean accepted = false;
        final String BEFORE = "before";
        final String AFTER = "after";
        String stringDecision;
        Boolean finalDecision = null;

        do {
            out.print(">>> ");
            stringDecision = in.nextLine();
            if (stringDecision.equalsIgnoreCase(BEFORE)) {
                finalDecision = true;
                accepted = true;
            } else if (stringDecision.equalsIgnoreCase(AFTER)) {
                finalDecision = false;
                accepted = true;
            } else {
                firstError = promptInputError(firstError, "Invalid input");
            }
        } while (!accepted);

        return finalDecision;
    }
}
