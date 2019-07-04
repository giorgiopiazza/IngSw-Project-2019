package view.cli;

import network.client.ClientGameManager;
import enumerations.*;
import enumerations.Properties;
import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.actions.WeaponCardsNotFoundException;
import exceptions.client.CancelledActionException;
import exceptions.game.InexistentColorException;
import exceptions.map.InvalidSpawnColorException;
import exceptions.utility.InvalidPropertiesException;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.cards.effects.Effect;
import model.map.GameMap;
import model.map.SpawnSquare;
import model.map.Square;
import model.player.*;
import network.client.DisconnectionListener;
import network.message.*;
import utility.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;

public class Cli extends ClientGameManager implements DisconnectionListener {
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
        doConnection();
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
    private String askUsername() {
        boolean firstError = true;
        String username = null;

        out.println("Enter your username:");

        do {
            out.print(">>> ");

            if (in.hasNextLine()) {
                final String currentUsername = in.nextLine();

                if (currentUsername.equals("") ||
                        GameConstants.getForbiddenUsernames().stream().anyMatch(u -> u.equalsIgnoreCase(currentUsername))) {
                    firstError = promptInputError(firstError, "Invalid username!");
                } else {
                    username = currentUsername;
                }
            } else {
                in.nextLine();
                firstError = promptInputError(firstError, INVALID_STRING);
            }
        } while (username == null);

        CliPrinter.clearConsole(out);
        return username;
    }

    /**
     * Asks the connection type
     */
    private void doConnection() {
        boolean validConnection = false;
        boolean firstError = true;
        int connection = -1;

        String username = askUsername();

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
            createConnection(connection, username, address, port, this);
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

        int defaultPort = (connection == 0 ? 2727 : 7272);
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
     * Send an unused colors request to the server
     */
    private void sendUnusedColorsRequest() {
        if (!sendRequest(MessageBuilder.buildColorRequest(getClientToken(), getUsername()))) {
            promptError(SEND_ERROR, true);
        }
    }

    /**
     * Send a lobby join request to the server
     */
    private void sendLobbyJoinRequest(PlayerColor playerColor) {
        if (!sendRequest(MessageBuilder.buildGetInLobbyMessage(getClientToken(), getUsername(), playerColor, false))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void connectionResponse(ConnectionResponse response) {
        CliPrinter.clearConsole(out);

        if (response.getStatus().equals(MessageStatus.ERROR)) {
            promptError(response.getMessage(), false);
            out.println();
            doConnection();
        } else {
            out.println("Connected to server with username " + getUsername());
            sendUnusedColorsRequest();
        }
    }

    @Override
    public void loadResponse() {
        CliPrinter.clearConsole(out);

        out.println("You joined a loaded game.\nWaiting for other players!");
    }

    @Override
    public void askColor(List<PlayerColor> availableColors) {
        boolean validColor = false;
        boolean firstError = true;

        PlayerColor playercolor = null;

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

        sendLobbyJoinRequest(playercolor);
    }

    @Override
    public void lobbyJoinResponse(Response response) {
        CliPrinter.clearConsole(out);

        if (response.getStatus() == MessageStatus.ERROR) {
            out.println(response.getMessage());
            out.println();
            sendUnusedColorsRequest();
        } else {
            out.println("You joined the lobby!\n\nWait for the game to start...\n");
            askVoteMap();
        }
    }

    @Override
    public void playersLobbyUpdate(List<String> users) {
        StringBuilder players = new StringBuilder();

        for (String user : users) {
            players.append(user);
            players.append(", ");
        }

        out.println("Players in lobby: " + players.substring(0, players.length() - 2));
    }

    @Override
    public void voteResponse(GameVoteResponse gameVoteResponse) {
        if (gameVoteResponse.getStatus() == MessageStatus.ERROR) {
            out.println(gameVoteResponse.getMessage());
            out.println();
            askVoteMap();
        } else {
            out.println("\nYou successfully voted the map.");
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
    public void notYourTurn(String turnOwner) {
        out.println(turnOwner + " is playing. Wait for your turn...");
    }

    @Override
    public void gameStateUpdate() {
        printMap();
        out.println();
        printPlayerBoard();
        out.println();
    }

    @Override
    public void responseError(String error) {
        promptError(error + "\n", false);
    }

    @Override
    public void displayActions(List<PossibleAction> possibleActions) {
        int choose = Integer.MIN_VALUE;
        printPlayers();
        printAmmo();
        out.println();
        printPowerupsNum();
        out.println("Choose the next move:");

        for (int i = 0; i < possibleActions.size(); i++) {
            out.println("\t" + (i) + " - " + possibleActions.get(i).getDescription());
        }

        LOGGER.log(INFO, "possibleActions: {0}", Arrays.toString(possibleActions.toArray()));
        LOGGER.log(INFO, "userplayer: {0}", getPlayer());
        LOGGER.log(INFO, "powerups: {0}", Arrays.toString(getGameSerialized().getPowerups().toArray()));
        LOGGER.log(INFO, "other players: {0}", Arrays.toString(getPlayers().toArray()));

        try {
            choose = readInt(0, possibleActions.size() - 1, false);
        } catch (CancelledActionException e) {
            // Can't happen
        }

        doAction(possibleActions.get(choose));
    }

    @Override
    public void botSpawn() {
        GameMap gameMap = getGameSerialized().getGameMap();
        PlayerPosition botSpawnPosition = null;
        boolean correctColor;

        printMap();
        out.println("You drew these two powerups, care where to spawn the bot!");
        printPowerups();

        out.println("Choose the Color of the square where to Spawn the bot:");
        do {
            try {
                botSpawnPosition = gameMap.getSpawnSquare(readRoomColor());
                correctColor = true;
            } catch (InvalidSpawnColorException e) {
                correctColor = false;
            } catch (CancelledActionException e) {
                cancelAction();
                return;
            }
        } while (!correctColor);

        if (!sendRequest(MessageBuilder.buildBotSpawnRequest(getPlayer(), getClientToken(), gameMap.getSquare(botSpawnPosition)))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void botRespawn() {
        botSpawn();
    }

    @Override
    public void spawn() {
        printMap();
        out.println();
        printPowerups();

        PowerupCard powerupCard;
        try {
            powerupCard = askPowerupSpawn();
        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        List<PowerupCard> powerupCards = getPowerups();

        try {
            if (!sendRequest(MessageBuilder.buildSpawnDiscardPowerupRequest(getClientToken(), powerupCards, getSpawnPowerup(), powerupCard, getUsername()))) {
                promptError(SEND_ERROR, true);
            }
        } catch (PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }
    }

    @Override
    public void move() {
        printMap();
        out.println();

        try {
            if (!sendRequest(MessageBuilder.buildMoveRequest(getClientToken(), getPlayer(), readCoordinates()))) {
                promptError(SEND_ERROR, true);
            }
        } catch (CancelledActionException e) {
            cancelAction();
        }
    }

    @Override
    public void moveAndPick() {
        PlayerPosition newPos;
        Square square;

        printMap();

        out.println("\nChoose the moving square for your pick action (same position not to move):");
        try {
            newPos = readCoordinates();
            square = getGameSerialized().getGameMap().getSquare(newPos.getRow(), newPos.getColumn());
        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        if (square != null) {
            if (square.getSquareType() == SquareType.TILE) {
                if (!sendRequest(MessageBuilder.buildMovePickRequest(getClientToken(), getPlayer(), newPos))) {
                    promptError(SEND_ERROR, true);
                }
            } else {
                try {
                    buildPickWeaponRequest(newPos);
                } catch (CancelledActionException e) {
                    cancelAction();
                }
            }
        } else {
            cancelAction("Invalid Pick Square");
        }
    }

    @Override
    public void shoot() {
        try {
            ShootRequest.ShootRequestBuilder shootRequestBuilder = sharedShootBuilder();

            if (shootRequestBuilder == null) {
                cancelAction();
                return;
            }

            if (!sendRequest(MessageBuilder.buildShootRequest(shootRequestBuilder))) {
                promptError(SEND_ERROR, true);
            }

        } catch (CancelledActionException e) {
            cancelAction();
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

        out.println("Choose the moving square for your adrenaline shoot action (same position not to move)");
        try {
            adrenalineMovePosition = readCoordinates();

            // now that I also know the moving position needed for the adrenaline shoot action I can build the shoot request and send it
            shootRequestBuilt = sharedShootBuilder();
        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        if (shootRequestBuilt == null) {
            cancelAction();
            return;
        } else {
            shootRequestBuilt.moveBeforeShootPosition(adrenalineMovePosition);
        }

        if (!sendRequest(MessageBuilder.buildShootRequest(shootRequestBuilt))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void frenzyMove() {
        out.println("FRENZY ACTION! \n");
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
        buildFrenzyShoot();
    }

    @Override
    public void lightFrenzyPick() {
        out.println("LIGHT FRENZY ACTION");
        moveAndPick();
    }

    @Override
    public void lightFrenzyShoot() {
        out.println("LIGHT FRENZY ACTION");
        buildFrenzyShoot();
    }

    @Override
    public void botAction() {
        PlayerPosition newPos;
        String target;

        printMap();

        try {
            out.println("Choose the position for the bot action (same position not to move):");
            newPos = readCoordinates();

            out.println("Choose the target for the bot action (-1 not to shoot):");
            target = readBotTarget(getGameSerialized().getPlayers());
        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        if (!sendRequest(MessageBuilder.buildUseTerminatorRequest(getPlayer(), getClientToken(), newPos, target == null ? null : (UserPlayer) getPlayerByName(target)))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void targetingScope() {
        List<PowerupCard> powerups = getPowerups();
        List<PowerupCard> scopeList = new ArrayList<>();
        PowerupRequest.PowerupRequestBuilder builder;

        for (PowerupCard powerup : powerups) {
            if (powerup.getName().equals(TARGETING_SCOPE)) {
                scopeList.add(powerup);
            }
        }

        if (!scopeList.isEmpty()) {
            try {
                builder = buildTargetingScopeRequest(powerups, scopeList);
            } catch (CancelledActionException e) {
                cancelAction();
                return;
            }
        } else {
            builder = new PowerupRequest.PowerupRequestBuilder(getUsername(), getClientToken(), new ArrayList<>());
        }

        if (!sendRequest(MessageBuilder.buildPowerupRequest(builder))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void tagbackGrenade() {
        List<PowerupCard> newList = getOnlyGrenades();
        ArrayList<PowerupCard> chosenGrenades = new ArrayList<>();

        CliPrinter.clearConsole(out);
        CliPrinter.printPowerups(out, newList.toArray(PowerupCard[]::new));

        out.println("Choose the TAGBACK GRENADE/S you want to use to mark the shooting player (-1 to stop choosing)!");
        out.println();

        for (int i = 0; i < newList.size(); i++) {
            out.println("\t" + i + " - " + CliPrinter.toStringPowerUpCard(newList.get(i)) + " (" + Ammo.toColor(newList.get(i).getValue()) + " room)");
        }

        try {
            for (int i = 0; i < newList.size(); ++i) {
                int tempChoose = readInt(-1, newList.size() - 1, true);

                if (tempChoose == -1 && !chosenGrenades.isEmpty()) break;
                if (tempChoose != -1) chosenGrenades.add(newList.get(tempChoose));
            }
        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        if (chosenGrenades.isEmpty()) {
            if (!sendRequest(MessageBuilder.buildPassTurnRequest(getClientToken(), getPlayer()))) {
                promptError(SEND_ERROR, true);
            }
        } else {
            ArrayList<Integer> indexes = new ArrayList<>(getPowerupsIndexesFromList(getPowerups(), chosenGrenades));

            PowerupRequest.PowerupRequestBuilder grenadeRequestBuilder = new PowerupRequest.PowerupRequestBuilder(getUsername(), getClientToken(), indexes);

            if (!sendRequest(MessageBuilder.buildPowerupRequest(grenadeRequestBuilder))) {
                promptError(SEND_ERROR, true);
            }
        }
    }

    @Override
    public void reload() {
        WeaponCard[] playersWeapons = getPlayer().getWeapons();
        List<Integer> rechargingWeapons = new ArrayList<>();
        ArrayList<Integer> paymentPowerups = new ArrayList<>();

        if (playersWeapons.length == 0) {
            cancelAction("You have no weapon to recharge");
            return;
        }

        try {
            int tempChoose;
            do {
                CliPrinter.clearConsole(out);
                out.println("Choose the weapons you want to reload (-1 to stop choosing). Your ammo are:");
                printAmmo();
                out.println();

                if (!rechargingWeapons.isEmpty()) {
                    out.println("You are reloading weapons: " + rechargingWeapons
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")) + ".\n");
                }

                tempChoose = askWeapon(true);
                if (tempChoose != -1) {
                    rechargingWeapons.add(tempChoose);
                    rechargingWeapons = rechargingWeapons.stream().distinct().collect(Collectors.toList());

                    if (rechargingWeapons.size() == playersWeapons.length) {
                        tempChoose = -1;
                    }
                }
            } while (tempChoose != -1);


            if (rechargingWeapons.isEmpty()) {
                cancelAction();
                return;
            }

            if (!getPowerups().isEmpty()) {
                paymentPowerups = askPaymentPowerups();
            }
        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        paymentPowerupsCheck(paymentPowerups, rechargingWeapons);
    }

    private void paymentPowerupsCheck(List<Integer> paymentPowerups, List<Integer> rechargingWeapons) {
        try {
            if (paymentPowerups.isEmpty()) {
                if (!sendRequest(MessageBuilder.buildReloadRequest(getClientToken(), getPlayer(), new ArrayList<>(rechargingWeapons)))) {
                    promptError(SEND_ERROR, true);
                }
            } else {
                if (!sendRequest(MessageBuilder.buildReloadRequest(getClientToken(), getPlayer(), new ArrayList<>(rechargingWeapons), paymentPowerups))) {
                    promptError(SEND_ERROR, true);
                }
            }
        } catch (WeaponCardsNotFoundException | PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), false);
        }
    }

    @Override
    public void powerup() {
        CliPrinter.clearConsole(out);
        printPowerups();
        PowerupRequest.PowerupRequestBuilder powerupRequestBuilder;

        try {
            PowerupCard powerupCard = askPowerup();

            if (!powerupCard.getName().equals(NEWTON) && !powerupCard.getName().equals(TELEPORTER)) {
                cancelAction("You can't use this powerup!");
                return;
            }

            ArrayList<Integer> powerups = new ArrayList<>();
            powerups.add(getPowerups().indexOf(powerupCard));

            powerupRequestBuilder = new PowerupRequest.PowerupRequestBuilder(getUsername(), getClientToken(), powerups);

            Effect baseEffect = powerupCard.getBaseEffect();
            Map<String, String> effectProperties = baseEffect.getProperties();


            if (effectProperties.containsKey(Properties.TP.getJKey())) {
                out.println("Choose your teleporting position:");
                powerupRequestBuilder.senderMovePosition(readCoordinates());
            }

            if (effectProperties.containsKey(Properties.MAX_MOVE_TARGET.getJKey())) {
                out.println("Choose target username:");
                powerupRequestBuilder.targetPlayersUsername(askExactTargets("1"));
                powerupRequestBuilder.targetPlayersMovePositions(new ArrayList<>(List.of(readCoordinates())));
            }

        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        if (!sendRequest(MessageBuilder.buildPowerupRequest(powerupRequestBuilder))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void passTurn() {
        if (!sendRequest(MessageBuilder.buildPassTurnRequest(getClientToken(), getPlayer()))) {
            promptError(SEND_ERROR, true);
        }
    }

    @Override
    public void onPlayerDisconnect(String username) {
        out.println("Player " + username + " DISCONNECTED from the game!");
    }

    @Override
    public void notifyGameEnd(List<PlayerPoints> winners) {
        printWinners(winners);
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

    /**
     * Asks the map that the player wants to play with
     */
    private void askVoteMap() {
        out.println("Which map would you like to play with (1 - 4)?");
        int mapVote = Integer.MIN_VALUE;

        try {
            mapVote = readInt(1, 4, false);
        } catch (CancelledActionException e) {
            // Can't happen
        }

        if (sendRequest(MessageBuilder.buildVoteMessage(getClientToken(), getUsername(), mapVote))) {
            votedMap();
        } else {
            promptError(SEND_ERROR, true);
        }
    }

    /**
     * Asks to choose a weapon
     *
     * @param optional {@code true} if it's an optional chose, {@code false} if it is not
     * @return -1 if no weapon was chosen, the index of the weapon otherwise
     * @throws CancelledActionException if the action was cancelled
     */
    private int askWeapon(boolean optional) throws CancelledActionException {
        UserPlayer player = getPlayer();
        WeaponCard[] weapons = player.getWeapons();
        int choose;

        if (weapons.length == 0) return -1;
        int minVal = optional ? -1 : 0;

        printWeapons(player.getWeapons());

        do {
            out.println("Choose the weapon:");
            choose = readInt(minVal, weapons.length - 1, true);
        } while ((choose < minVal || choose > weapons.length - 1));

        return choose;
    }

    /**
     * Asks to choose a weapon's effect
     *
     * @param weapon the weapon card of the weapon selected
     * @return the index of the weapon's effect
     * @throws CancelledActionException if the action was cancelled
     */
    private Integer askWeaponEffect(WeaponCard weapon) throws CancelledActionException {
        WeaponCard[] chosenWeapon = new WeaponCard[]{weapon};
        Integer choose;

        printWeapons(chosenWeapon);
        do {
            out.println("Choose the weapon's effect:");
            choose = readInt(0, weapon.getSecondaryEffects().size(), true);
        } while (choose < 0 || choose > weapon.getSecondaryEffects().size());

        return choose;
    }

    /**
     * Asks to choose payment powerups
     *
     * @return the list of powerups read
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<Integer> askPaymentPowerups() throws CancelledActionException {
        List<Integer> paymentPowerups = new ArrayList<>();
        Integer tempChoose;

        printPowerups();

        do {
            out.println("Choose the powerups you want to pay with (-1 to stop choosing):");
            tempChoose = readInt(-1, getPowerups().size(), true);

            if (tempChoose == -1) {
                return new ArrayList<>(paymentPowerups);
            }

            paymentPowerups.add(tempChoose);
            paymentPowerups = paymentPowerups.stream().distinct().collect(Collectors.toList());
        } while (paymentPowerups.size() < getPowerups().size());

        return new ArrayList<>(paymentPowerups);
    }

    /**
     * Based on the effect properties asks a number of player targets
     *
     * @param effectProperties properties of effect
     * @return the list of username targets
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<String> askTargetsUsername(Map<String, String> effectProperties) throws CancelledActionException {
        ArrayList<String> targetsUsername;

        printMap();
        out.println();
        printPlayers();

        if (effectProperties.containsKey(Properties.TARGET_NUM.getJKey())) {
            targetsUsername = askExactTargets(effectProperties.get(Properties.TARGET_NUM.getJKey()));
        } else if (effectProperties.containsKey(Properties.MAX_TARGET_NUM.getJKey())) {
            targetsUsername = askMaxTargets(effectProperties.get(Properties.MAX_TARGET_NUM.getJKey()));
        } else {
            throw new InvalidPropertiesException();
        }

        return targetsUsername;
    }

    /**
     * Asks an exact number of player targets
     *
     * @param exactStringNum exact number of targets
     * @return the list of username targets
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<String> askExactTargets(String exactStringNum) throws CancelledActionException {
        int exactIntNum = Integer.parseInt(exactStringNum);
        ArrayList<String> chosenTargets = new ArrayList<>();

        do {
            out.println("Choose exactly " + exactIntNum + " target/s for your shoot action:");
            String target = readTargetUsername(getPlayersWithBot(), false);

            chosenTargets.add(target);

        } while (chosenTargets.size() < exactIntNum);

        return chosenTargets;
    }

    /**
     * Asks a max number of player targets
     *
     * @param maxStringNum max number of targets
     * @return the list of username targets
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<String> askMaxTargets(String maxStringNum) throws CancelledActionException {
        int maxIntNum = Integer.parseInt(maxStringNum);
        ArrayList<String> chosenTargets = new ArrayList<>();
        out.println("Choose up to " + maxIntNum + " target/s for your shoot action (-1 to stop choosing):");

        do {
            String target = readTargetUsername(getPlayersWithBot(), true);

            if (target != null) {
                chosenTargets.add(target);
            } else if (!chosenTargets.isEmpty()) {
                return chosenTargets;
            } else {
                throw new CancelledActionException();
            }

        } while (chosenTargets.size() < maxIntNum);

        return chosenTargets;
    }

    /**
     * Based on the effect properties asks a number of square positions
     *
     * @param effectProperties properties of effect
     * @return the list of square targets
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<PlayerPosition> askTargetSquaresPositions(Map<String, String> effectProperties) throws CancelledActionException {
        ArrayList<PlayerPosition> targetSquaresPositions;

        if (effectProperties.containsKey(Properties.TARGET_NUM.getJKey())) {
            targetSquaresPositions = askExactSquares(effectProperties.get(Properties.TARGET_NUM.getJKey()));
        } else if (effectProperties.containsKey(Properties.MAX_TARGET_NUM.getJKey())) {
            targetSquaresPositions = askMaxSquares(effectProperties.get(Properties.MAX_TARGET_NUM.getJKey()));
        } else {
            throw new InvalidPropertiesException();
        }

        return targetSquaresPositions;
    }

    /**
     * Asks an exact number of square targets
     *
     * @param exactStringNum exact number of targets
     * @return the list of square targets
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<PlayerPosition> askExactSquares(String exactStringNum) throws CancelledActionException {
        int exactIntNum = Integer.parseInt(exactStringNum);
        ArrayList<PlayerPosition> chosenSquares = new ArrayList<>();

        do {
            out.println("Choose exactly " + exactIntNum + " target/s squares for your shoot action:");

            chosenSquares.add(readCoordinates());
        } while (chosenSquares.size() < exactIntNum);

        return chosenSquares;
    }

    /**
     * Asks an exact number of square targets
     *
     * @param maxStringNum max number of targets
     * @return the list of square targets
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<PlayerPosition> askMaxSquares(String maxStringNum) throws CancelledActionException {
        int maxIntNum = Integer.parseInt(maxStringNum);
        ArrayList<PlayerPosition> chosenSquares = new ArrayList<>();

        do {
            out.println("Choose up to " + maxIntNum + " target/s squares for your shoot action (-1 to stop choosing):");
            PlayerPosition coord = readCoordinates();

            if (coord.getRow() == -1 && chosenSquares.size() > 1) {
                return chosenSquares;
            }

            chosenSquares.add(coord);
        } while (chosenSquares.size() < maxIntNum);

        return chosenSquares;
    }

    /**
     * Asks a color of target room
     *
     * @param effectProperties properties of the effect
     * @return the room color read
     * @throws CancelledActionException if the action was cancelled
     */
    private RoomColor askTargetRoomColor(Map<String, String> effectProperties) throws CancelledActionException {
        if (effectProperties.containsKey(Properties.TARGET_NUM.getJKey())) {
            out.println("Choose the color of the target room for your shoot action:");

            return readRoomColor();
        } else {
            throw new InvalidPropertiesException();
        }
    }

    /**
     * Asks the moving position of the shoot action
     *
     * @return the position read
     * @throws CancelledActionException if the action was cancelled
     */
    private PlayerPosition askMovePositionInShoot() throws CancelledActionException {
        out.println("Choose the moving position for your shoot action:");
        return readCoordinates();
    }

    /**
     * Asks if the player move has to be executed before of after shooting
     *
     * @return the move decision read
     * @throws CancelledActionException if the action was cancelled
     */
    private Boolean askBeforeAfterMove() throws CancelledActionException {
        out.println("Choose if you want to move 'before' or 'after' shooting:");
        return readMoveDecision();
    }

    /**
     * Asks if the player wants to move during the shoot action
     *
     * @return the move decision read
     * @throws CancelledActionException if the action was cancelled
     */
    private Boolean askMiddleMove() throws CancelledActionException {
        out.println("Choose if you want to do a: 'inMiddle'(0) or 'before'/'after'(1) movement");
        return readInt(0, 1, true) == 0;
    }

    /**
     * Ask for each target the moving position
     *
     * @param targetsChosen list of targets
     * @return the list of positions
     * @throws CancelledActionException if the action was cancelled
     */
    private ArrayList<PlayerPosition> askTargetsMovePositions(ArrayList<String> targetsChosen) throws CancelledActionException {
        ArrayList<PlayerPosition> targetsMovePositions = new ArrayList<>();

        out.println("Choose each target moving position!");

        for (String target : targetsChosen) {
            out.println("Choose " + target + " moving position:");
            PlayerPosition coord = readCoordinates();

            targetsMovePositions.add(coord);
        }

        return targetsMovePositions;
    }

    /**
     * Build a shoot request adding properties to an existent one
     *
     * @param chosenEffect        the chosen effect
     * @param shootRequestBuilder the already existent builder
     * @return the shoot request builder
     * @throws CancelledActionException if the action was cancelled
     */
    private ShootRequest.ShootRequestBuilder buildShootRequest(Effect chosenEffect, ShootRequest.ShootRequestBuilder shootRequestBuilder) throws CancelledActionException {
        Map<String, String> effectProperties = chosenEffect.getProperties();
        TargetType[] targets = chosenEffect.getTargets();
        ArrayList<String> targetsChosen = new ArrayList<>();

        // targets input ask
        for (TargetType target : targets) {
            switch (target) {
                case PLAYER:
                    targetsChosen = askTargetsUsername(effectProperties);
                    shootRequestBuilder.targetPlayersUsernames(targetsChosen);
                    break;
                case SQUARE:
                    if (effectProperties.containsKey(Properties.SAME_POSITION.getJKey())) {
                        shootRequestBuilder.targetPositions(new ArrayList<>(List.of(getPlayerByName(targetsChosen.get(0)).getPosition())));
                    } else {
                        shootRequestBuilder.targetPositions(askTargetSquaresPositions(effectProperties));
                    }
                    break;
                case ROOM:
                    shootRequestBuilder.targetRoomColor(askTargetRoomColor(effectProperties));
                    break;
                default:
                    throw new InvalidPropertiesException();
            }
        }

        // now that I have the targets we need to handle the possible move decisions
        if (effectProperties.containsKey(Properties.MOVE.getJKey())) {
            // move is always permitted both before and after, decision is then always asked
            shootRequestBuilder.senderMovePosition(askMovePositionInShoot());
            if (effectProperties.containsKey(Properties.MOVE_IN_MIDDLE.getJKey()) && askMiddleMove()) {
                shootRequestBuilder.moveInMiddle(true);
            } else {
                shootRequestBuilder.moveSenderFirst(askBeforeAfterMove());
            }
        } else if (effectProperties.containsKey(Properties.MOVE_TO_LAST_TARGET.getJKey())) {
            shootRequestBuilder.senderMovePosition(getPlayerByName(targetsChosen.get(targetsChosen.size() - 1)).getPosition());
            shootRequestBuilder.moveToLastTarget(true);
        }

        turnOwnerTargetsCheck(effectProperties, shootRequestBuilder, targetsChosen);

        return shootRequestBuilder;
    }

    private void turnOwnerTargetsCheck(Map<String, String> effectProperties, ShootRequest.ShootRequestBuilder shootRequestBuilder, ArrayList<String> targetsChosen) throws CancelledActionException {
        // now that I have handled the Turn Owner movement I have to handle the targets ones
        if (effectProperties.containsKey(Properties.MOVE_TARGET.getJKey()) || effectProperties.containsKey(Properties.MAX_MOVE_TARGET.getJKey())) {
            shootRequestBuilder.targetPlayersMovePositions(askTargetsMovePositions(targetsChosen));
        }

        // in the end if the targets movement can be done before or after the shoot action I ask when to the shooter
        if (effectProperties.containsKey(Properties.MOVE_TARGET_BEFORE.getJKey())) {
            shootRequestBuilder.moveTargetsFirst(Boolean.parseBoolean(effectProperties.get(Properties.MOVE_TARGET_BEFORE.getJKey())));
        } else if (effectProperties.containsKey(Properties.MOVE_TARGET.getJKey()) || effectProperties.containsKey(Properties.MAX_MOVE_TARGET.getJKey())) {
            shootRequestBuilder.moveTargetsFirst(askBeforeAfterMove());
        }
    }

    /**
     * Creates and sends a frenzy shot request
     */
    private void buildFrenzyShoot() {
        ShootRequest.ShootRequestBuilder shootRequestBuilt;
        PlayerPosition frenzyMovePosition;
        ArrayList<Integer> rechargingWeapons = new ArrayList<>();

        try {
            out.println("Choose the moving square for your frenzy shoot action (same position not to move):");
            frenzyMovePosition = readCoordinates();

            out.println("Do you want to recharge your weapons before shooting (-1 to stop choosing)?");
            for (int i = 0; i < getPlayer().getWeapons().length; ++i) {
                int tempChoose = askWeapon(true);
                if (tempChoose == -1) break;
                rechargingWeapons.add(tempChoose);
            }

            // now that I have everything I need more for a frenzy shoot I build the normal shoot request, add these and send it
            shootRequestBuilt = sharedShootBuilder();
        } catch (CancelledActionException e) {
            cancelAction();
            return;
        }

        if (shootRequestBuilt == null) {
            return;
        } else {
            shootRequestBuilt.moveBeforeShootPosition(frenzyMovePosition).rechargingWeapons(rechargingWeapons);
        }

        if (!sendRequest(MessageBuilder.buildShootRequest(shootRequestBuilt))) {
            promptError(SEND_ERROR, true);
        }
    }

    /**
     * Utility method used to create the shoot builder for a shoot action
     *
     * @return the created builder
     * @throws CancelledActionException if the action was cancelled
     */
    private ShootRequest.ShootRequestBuilder sharedShootBuilder() throws CancelledActionException {
        ShootRequest.ShootRequestBuilder shootRequestBuilder;
        ArrayList<Integer> paymentPowerups = new ArrayList<>();
        Effect chosenEffect;

        WeaponCard[] weapons = getPlayer().getWeapons();
        if (weapons.length == 0) {
            throw new CancelledActionException();
        }

        printMap();
        out.println("Care of your Ammo before choosing to shoot: ");
        printAmmo();

        int weapon = askWeapon(false);
        int effect = askWeaponEffect(getPlayer().getWeapons()[weapon]);
        if (!getPowerups().isEmpty()) {
            paymentPowerups = askPaymentPowerups();
        }

        // normal shoot does not require recharging weapons
        shootRequestBuilder = new ShootRequest.ShootRequestBuilder(getUsername(), getClientToken(), weapon, effect).paymentPowerups(paymentPowerups);

        // now we can build the fireRequest specific to each chosen weapon
        if (effect == 0) {
            chosenEffect = getPlayer().getWeapons()[weapon].getBaseEffect();
        } else {
            chosenEffect = getPlayer().getWeapons()[weapon].getSecondaryEffects().get(effect - 1);
        }

        return buildShootRequest(chosenEffect, shootRequestBuilder);
    }

    /**
     * Builds a pick weapon request
     *
     * @param newPos the position where the player is picking
     * @throws CancelledActionException if the action was cancelled
     */
    private void buildPickWeaponRequest(PlayerPosition newPos) throws CancelledActionException {
        SpawnSquare weaponSquare = (SpawnSquare) getGameSerialized().getGameMap().getSquare(newPos);
        ArrayList<Integer> paymentPowerups = new ArrayList<>();
        WeaponCard pickingWeapon;
        WeaponCard discardingCard = null;

        if (weaponSquare.getWeapons().length == 0) { // very particular case in which a spawn square has no powerups
            // pick action not allowed
            throw new CancelledActionException();
        }

        pickingWeapon = askPickWeapon(weaponSquare);
        if (!getPowerups().isEmpty()) {
            paymentPowerups = askPaymentPowerups();
        }

        // now that we know the WeaponCard the acting player wants to pick, if he has already 3 cards in his hand we ask him which one to discard
        if (getPlayer().getWeapons().length == 3) {
            out.println("You already have 3 weapons in your hand, choose one and discard it!");
            discardingCard = getPlayer().getWeapons()[askWeapon(false)];
        }

        try {
            if (!paymentPowerups.isEmpty()) {
                if (!sendRequest(MessageBuilder.buildMovePickRequest(getClientToken(), getPlayer(), newPos, paymentPowerups, pickingWeapon, discardingCard))) {
                    promptError(SEND_ERROR, true);
                }
            } else {
                if (!sendRequest(MessageBuilder.buildMovePickRequest(getClientToken(), getPlayer(), newPos, pickingWeapon, discardingCard))) {
                    promptError(SEND_ERROR, true);
                }
            }
        } catch (PowerupCardsNotFoundException e) {
            promptError(e.getMessage(), true);
        }
    }

    /**
     * Builds a powerup request builder
     *
     * @param powerups  list of all powerups
     * @param scopeList list of only targeting scopes
     * @return the builder of the powerup request
     * @throws CancelledActionException if the action was cancelled
     */
    private PowerupRequest.PowerupRequestBuilder buildTargetingScopeRequest(List<PowerupCard> powerups,
                                                                            List<PowerupCard> scopeList) throws CancelledActionException {
        List<PowerupCard> scopes = new ArrayList<>();
        ArrayList<String> targets = new ArrayList<>();
        ArrayList<Ammo> payingColors = new ArrayList<>();
        ArrayList<Integer> payingPowerups = new ArrayList<>();

        out.println();

        for (int i = 0; i < scopeList.size(); i++) {
            out.println("\t" + i + " - " + CliPrinter.toStringPowerUpCard(scopeList.get(i)));
        }

        out.println();
        out.println("Do you want to use some scope(s)? (-1 none or finish scope(s) selection)");

        int readVal;
        int cycles = 0;

        do {
            readVal = readInt(-1, scopeList.size() - 1, true);

            if (readVal != -1) {
                out.println("Choose the target player:");
                String user = readTargetUsername(getPlayersWithBot(), false);

                scopes.add(scopeList.get(readVal));
                targets.add(user);
            }
            cycles++;
        } while (readVal != -1 && cycles < scopeList.size());

        // after the targets have been chosen, how to pay the scopes is required
        List<PowerupCard> othersList = powerups.stream().filter(notUsed -> !scopeList.contains(notUsed)).collect(Collectors.toList());
        if (!(readVal == -1 && scopes.isEmpty())) {
            if (othersList.isEmpty()) {
                askOnlyAmmos(scopes.size(), payingColors);
            } else {
                askPaymentMethod(scopes.size(), payingColors, payingPowerups, powerups, othersList);
            }
        }

        ArrayList<Integer> indexes = new ArrayList<>(getPowerupsIndexesFromList(powerups, scopes));

        LOGGER.log(INFO, "indexes: {0}", Arrays.toString(indexes.toArray()));

        PowerupRequest.PowerupRequestBuilder builder = new PowerupRequest.PowerupRequestBuilder(getUsername(), getClientToken(), indexes);
        builder.targetPlayersUsername(targets).ammoColor(payingColors).paymentPowerups(payingPowerups);

        return builder;
    }

    private void askOnlyAmmos(int scopesUsed, ArrayList<Ammo> payingColors) {
        int cycles = 0;

        printAmmo();
        do {
            payingColors.add(askAmmoColor());

            ++cycles;
        } while (cycles < scopesUsed);
    }

    private Ammo askAmmoColor() {
        Ammo chosenAmmo = null;
        String chosenColor;
        boolean correctColor;

        do {
            out.println("Choose the Ammo color you want to pay with");
            out.println(">>> ");
            chosenColor = in.nextLine();

            try {
                Ammo.getColor(chosenColor);
                chosenAmmo = Ammo.valueOf(chosenColor.toUpperCase());
                correctColor = true;
            } catch (InexistentColorException e) {
                correctColor = false;
            }
        } while (!correctColor);

        return chosenAmmo;
    }

    private Integer askPowerupIndex(List<PowerupCard> possiblePowerups, List<PowerupCard> powerups) throws CancelledActionException {
        Integer chosenPowerup;

        out.println("Choose the powerup index");
        chosenPowerup = readInt(0, possiblePowerups.size(), true);

        return getPowerupsIndexesFromList(powerups, List.of(possiblePowerups.get(chosenPowerup))).get(0);
    }

    private void askPaymentMethod(int scopesUsed, ArrayList<Ammo> payingColors, ArrayList<Integer> payingPowerups, List<PowerupCard> powerups, List<PowerupCard> possiblePowerups) throws CancelledActionException {
        int cycles = 0;

        do {
            printAmmo();
            CliPrinter.printPowerups(out, possiblePowerups.toArray(PowerupCard[]::new));
            out.println("Choose to pay with Ammos(0) or Powerups(1):");

            if (readInt(0, 1, true) == 0) {
                payingColors.add(askAmmoColor());
                ++cycles;
            } else if (payingPowerups.size() < possiblePowerups.size()) {
                payingPowerups.add(askPowerupIndex(possiblePowerups, powerups));
                ++cycles;
            }

        } while (cycles < scopesUsed);
    }

    /**
     * Asks which weapon the player wants to pick
     *
     * @param weaponSquare square where the player is picking
     * @return the picked weapon
     * @throws CancelledActionException if the action was cancelled
     */
    private WeaponCard askPickWeapon(SpawnSquare weaponSquare) throws CancelledActionException {
        WeaponCard[] weapons = weaponSquare.getWeapons();
        Integer choose;

        CliPrinter.clearConsole(out);
        out.println("Weapons on your moving spawn square are:");
        printWeapons(weaponSquare.getWeapons());

        do {
            out.println("\nChoose the weapon. Your ammo are:\n");
            printAmmo();
            choose = readInt(0, weapons.length - 1, true);
        } while (weapons[choose] == null || choose > weapons.length - 1);

        return weapons[choose];
    }

    /**
     * Asks to choose a powerup
     *
     * @return the powerup chosen
     * @throws CancelledActionException if the action was cancelled
     */
    private PowerupCard askPowerup() throws CancelledActionException {
        List<PowerupCard> powerups = getPowerups();
        List<PowerupCard> newList = new ArrayList<>();

        out.println();
        out.println("Which power up do you want to use?");

        for (PowerupCard powerup : powerups) {
            if (powerup.getName().equals(ClientGameManager.NEWTON) || powerup.getName().equals(ClientGameManager.TELEPORTER)) {
                newList.add(powerup);
            }
        }

        for (int i = 0; i < newList.size(); i++) {
            out.println("\t" + i + " - " + CliPrinter.toStringPowerUpCard(newList.get(i)));
        }

        out.println();

        return newList.get(readInt(0, newList.size() - 1, true));
    }

    /**
     * Asks to choose a powerup for spawn
     *
     * @return the powerup chosen
     * @throws CancelledActionException if the action was cancelled
     */
    private PowerupCard askPowerupSpawn() throws CancelledActionException {
        List<PowerupCard> powerups = getPowerups();

        out.println();
        out.println("Where do you want to spawn?");

        for (int i = 0; i < powerups.size(); i++) {
            out.println("\t" + i + " - " + CliPrinter.toStringPowerUpCard(powerups.get(i)) + " (" + Ammo.toColor(powerups.get(i).getValue()) + " room)");
        }

        out.println();

        int choose = readInt(0, powerups.size() - 1, true);

        return powerups.get(choose);
    }


    /**
     * @return a list of only tagback grenades
     */
    private List<PowerupCard> getOnlyGrenades() {
        return getGameSerialized().getPowerups()
                .stream()
                .filter(p -> p.getName().equals(TAGBACK_GRENADE))
                .collect(Collectors.toList());
    }

    /**
     * Returns the indexes of scope in the powerups list
     *
     * @param powerups list of all powerups
     * @param scopes   list of scopes
     * @return the list of indexes
     */
    private List<Integer> getPowerupsIndexesFromList(List<PowerupCard> powerups, List<PowerupCard> scopes) {
        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < powerups.size(); i++) {
            for (PowerupCard powerup : scopes) {
                if (powerup.equals(powerups.get(i))) indexes.add(i);
            }
        }

        return indexes;
    }

    /**
     * Print message and force the resend of possible actions
     *
     * @param message message to print
     */
    private void cancelAction(String message) {
        CliPrinter.clearConsole(out);
        out.println(message);
        makeMove();
    }

    /**
     * Forces the resend of possible actions
     */
    private void cancelAction() {
        CliPrinter.clearConsole(out);
        makeMove();
    }

    /**
     * Reads a coordinate
     *
     * @return the coordinates read
     * @throws CancelledActionException if the action was cancelled
     */
    private PlayerPosition readCoordinates() throws CancelledActionException {
        boolean firstError = true;
        PlayerPosition coord = null;

        // a target is meant both as: target for a moving action or for choosing a target square
        out.println("Write the target position coordinates " + (getPlayer().isDead() ? "" : getPlayer().getPosition()) + ":");
        do {
            out.print(">>> ");

            String line = in.nextLine();

            if (line.equalsIgnoreCase(GameConstants.CANCEL_KEYWORD)) {
                throw new CancelledActionException();
            }

            String[] split = line.split(",");

            if (split.length != 2)
                firstError = promptInputError(firstError, "Wrong input (must be like \"0,0\" or \"0, 0\")");
            try {
                int x = Integer.parseInt(split[0].trim());
                int y = Integer.parseInt(split[1].trim());
                getGameSerialized().getGameMap().getSquare(x, y);

                coord = new PlayerPosition(x, y);
            } catch (NumberFormatException e) {
                firstError = promptInputError(firstError, "Wrong input (must be like \"0,0\" or \"0, 0\")");
            } catch (ArrayIndexOutOfBoundsException e) {
                firstError = promptInputError(firstError, "Invalid coordinates, out of bounds");
            }
        } while (coord == null);

        return coord;
    }

    /**
     * Read an integer in the desired interval
     *
     * @param minVal      minimum value
     * @param maxVal      maximum value
     * @param cancellable {@code true} if the read is cancellable with "-1"
     * @return the integer read
     * @throws CancelledActionException if the action was cancelled
     */
    private Integer readInt(int minVal, int maxVal, boolean cancellable) throws CancelledActionException {
        boolean firstError = true;
        boolean accepted = false;
        Integer choose = Integer.MIN_VALUE;

        do {
            out.print(">>> ");
            String line = in.nextLine();

            if (cancellable && line.equalsIgnoreCase(GameConstants.CANCEL_KEYWORD)) {
                throw new CancelledActionException();
            }

            try {
                choose = Integer.valueOf(line);

                if (choose >= minVal && choose <= maxVal) {
                    accepted = true;
                } else {
                    firstError = promptInputError(firstError, "Not valid input!");
                }
            } catch (NumberFormatException e) {
                promptInputError(firstError, "Not valid input!");
            }
        } while (!accepted);

        return choose;
    }

    /**
     * Reads a target player for the bot action
     *
     * @param inGamePlayers list of players
     * @return the target
     * @throws CancelledActionException if the action was cancelled
     */
    private String readBotTarget(List<UserPlayer> inGamePlayers) throws CancelledActionException {
        boolean firstError = true;
        boolean accepted = false;
        String chosenTarget;

        do {
            out.print(">>> ");
            chosenTarget = in.nextLine();

            if (chosenTarget.equals(GameConstants.CANCEL_KEYWORD)) {
                throw new CancelledActionException();
            } else if (chosenTarget.equals("-1")) {
                return null;
            } else if (!chosenTarget.equals("bot") && !chosenTarget.equals(getTurnOwner())) { // no one can shoot itself, and the bot can't shoot the turn owner!
                final String target = chosenTarget;
                if (inGamePlayers.stream().anyMatch(p -> p.getUsername().equals(target))) {
                    accepted = true;
                } else {
                    firstError = promptInputError(firstError, "Bot Target Not Valid");
                }
            } else {
                firstError = promptInputError(firstError, "Bot Target Not Valid");
            }
        } while (!accepted);

        return chosenTarget;
    }

    /**
     * Reads a target username
     *
     * @param inGamePlayers list of players
     * @param stoppable     {@code true} if the read is stoppable by typing "-1"
     * @return the username read
     * @throws CancelledActionException if the action was cancelled
     */
    private String readTargetUsername(List<Player> inGamePlayers, boolean stoppable) throws CancelledActionException {
        boolean firstError = true;
        boolean accepted = false;
        boolean isTerminatorPresent = getGameSerialized().isBotPresent();

        String chosenTarget;
        do {
            out.print(">>> ");
            in.reset();

            chosenTarget = in.nextLine().trim();

            if (chosenTarget.equalsIgnoreCase(GameConstants.CANCEL_KEYWORD)) {
                throw new CancelledActionException();
            }

            if (stoppable && chosenTarget.equals("-1")) {
                return null;
            }

            if (isTerminatorPresent && chosenTarget.equals("bot")) {
                accepted = true;
            } else if (!chosenTarget.equals(getPlayer().getUsername())) {
                final String target = chosenTarget;
                if (inGamePlayers.stream().anyMatch(p -> p.getUsername().equals(target))) {
                    accepted = true;
                } else {
                    firstError = promptInputError(firstError, "Target Not Valid");
                }
            } else {
                firstError = promptInputError(firstError, "Target Not Valid");
            }
        } while (!accepted);

        return chosenTarget;
    }

    /**
     * Reads a room color
     *
     * @return the room color read
     * @throws CancelledActionException if the action was cancelled
     */
    private RoomColor readRoomColor() throws CancelledActionException {
        boolean firstError = true;
        boolean accepted = false;
        String stringColor;
        RoomColor roomColor = null;

        do {
            out.print(">>> ");
            stringColor = in.nextLine();

            if (stringColor.equalsIgnoreCase(GameConstants.CANCEL_KEYWORD)) {
                throw new CancelledActionException();
            }

            try {
                roomColor = RoomColor.getColor(stringColor);
                accepted = true;
            } catch (InexistentColorException e) {
                firstError = promptInputError(firstError, "Invalid Room Color");
            }
        } while (!accepted);

        return roomColor;
    }

    /**
     * Request a move decision about doing the move before or after shooting
     *
     * @return the move decision read
     * @throws CancelledActionException if the action was cancelled
     */
    private Boolean readMoveDecision() throws CancelledActionException {
        boolean firstError = true;
        boolean accepted = false;
        final String BEFORE = "before";
        final String AFTER = "after";
        String stringDecision;
        Boolean finalDecision = null;

        do {
            out.print(">>> ");
            stringDecision = in.nextLine();

            if (stringDecision.equalsIgnoreCase(GameConstants.CANCEL_KEYWORD)) {
                throw new CancelledActionException();
            } else if (stringDecision.equalsIgnoreCase(BEFORE)) {
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

    /**
     * Prints the game map
     */
    private void printMap() {
        CliPrinter.clearConsole(out);
        CliPrinter.printMap(out, getGameSerialized());
    }

    /**
     * Prints the list of players
     */
    private void printPlayers() {
        CliPrinter.printUsername(out, getPlayersWithBot());
        out.println();
    }

    /**
     * Prints the winners of the game
     *
     * @param winners the ArrayList of {@link PlayerPoints PlayerPoints}
     */
    private void printWinners(List<PlayerPoints> winners) {
        CliPrinter.printWinners(out, winners);
        out.println();
    }

    /**
     * Prints the powerups
     */
    private void printPowerups() {
        CliPrinter.printPowerups(out, getPowerups().toArray(PowerupCard[]::new));
    }

    /**
     * Prints the number of powerups
     */
    private void printPowerupsNum() {
        out.println("Powerups: " + getPowerups().size() + "\n");
    }

    /**
     * Prints the weapons
     *
     * @param weapons to print
     */
    private void printWeapons(WeaponCard[] weapons) {
        CliPrinter.printWeapons(out, weapons);
    }

    /**
     * Prints all player playerboards
     */
    private void printPlayerBoard() {
        CliPrinter.printPlayerBoards(out, getGameSerialized());
    }

    /**
     * Prints ammos
     */
    private void printAmmo() {
        CliPrinter.printAmmo(out, getPlayer().getPlayerBoard().getAmmo());
    }

    @Override
    public void onDisconnection() {
        promptError("Disconnected from the server, connection expired", true);
    }
}