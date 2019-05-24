package controller;

import enumerations.*;
import exceptions.AdrenalinaException;
import exceptions.game.InvalidGameStateException;
import exceptions.game.InvalidKillshotNumberException;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.cards.PowerupCard;
import model.player.KillShot;
import model.player.Player;
import model.player.PlayerBoard;
import model.player.Terminator;
import model.player.UserPlayer;
import network.message.*;
import network.server.Server;

import java.util.*;
import java.util.stream.Collectors;


public class GameManager {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 5;

    private final Server server;
    private PossibleGameState gameState;
    private final Game gameInstance;
    private GameLobby lobby;
    private RoundManager roundManager;
    private ShootParameters shootParameters;

    public GameManager(Server server) {
        this.server = server;
        gameState = PossibleGameState.GAME_ROOM;
        this.lobby = new GameLobby();
        this.gameInstance = Game.getInstance();
        this.roundManager = new RoundManager(this);
    }

    void changeState(PossibleGameState changeState) {
        gameState = changeState;
    }

    void endGame() {
        WinnersResponse winners;
        handleLastPointsDistribution();
        handleKillShotTrackDistribution();
        winners = declareWinner();
        server.sendMessageToAll(winners);

    }

    public Message onMessage(Message receivedMessage) {
        // on the game setup messages can be received from any player
        if(gameState == PossibleGameState.GAME_ROOM) {
            return firstStateHandler(receivedMessage);
        }

        // if the message received comes from a client that is not the turn owner it is never executed!
        if (!gameInstance.getPlayerByName(receivedMessage.getSenderUsername()).equals(roundManager.getTurnManager().getTurnOwner())) {
            return new Response("Message from a player that is not his turn!", MessageStatus.ERROR);
        }

        // SPECIAL CASES
        if (gameState == PossibleGameState.GRANADE_USAGE) {
            if (receivedMessage.getContent() == MessageContent.POWERUP) {
                return onGranadeMessage(receivedMessage);
            } else {
                return buildInvalidResponse();
            }
        }

        if (gameState == PossibleGameState.SCOPE_USAGE) {
            if (receivedMessage.getContent() == MessageContent.POWERUP) {
                if (!((PowerupRequest) receivedMessage).getPowerup().isEmpty()) {
                    return roundManager.handleShootAction(shootParameters.shootRequest, (PowerupRequest) receivedMessage, shootParameters.secondAction);
                } else {
                    return roundManager.handleShootAction(shootParameters.shootRequest, null, shootParameters.secondAction);
                }
            } else {
                return buildInvalidResponse();
            }
        }

        if (gameState == PossibleGameState.TERMINATOR_RESPAWN) {
            Response tempResponse;
            if (receivedMessage.getContent() == MessageContent.TERMINATOR_SPAWN) {
                tempResponse = roundManager.handleTerminatorRespawn((TerminatorSpawnRequest) receivedMessage);
                if (tempResponse.getStatus() == MessageStatus.OK) {
                    // if the Respawn message is validated I can distribute the points of the terminator's playerboard, move the skull from the tracker and then reset his playerboard
                    distributePoints(gameInstance.getTerminator());
                    moveSkull(gameInstance.getTerminator());
                    gameInstance.getTerminator().getPlayerBoard().onDeath();

                    // if the state changed to FINAL_FRENZY, no other players died and the FRENZY MODE starts
                    if (gameState == PossibleGameState.FINAL_FRENZY) {
                        gameInstance.setState(GameState.FINAL_FRENZY);
                        finalFrenzySetup();
                    }
                    return tempResponse;
                } else {
                    return tempResponse;
                }
            } else {
                return buildInvalidResponse();
            }
        }

        if (gameState == PossibleGameState.MANAGE_DEATHS) {
            Response tempResponse;
            if (receivedMessage.getContent() == MessageContent.DISCARD_POWERUP) {
                tempResponse = roundManager.handlePlayerRespawn((DiscardPowerupRequest) receivedMessage);
                if (tempResponse.getStatus() == MessageStatus.OK) {
                    // if the player respawn is validated I can distribute the points of the terminator's playerboard, move the skull from the tracker and then reset his playerboard
                    UserPlayer respawnedPlayer = gameInstance.getUserPlayerByUsername(receivedMessage.getSenderUsername());
                    distributePoints(respawnedPlayer);
                    moveSkull(respawnedPlayer);
                    respawnedPlayer.getPlayerBoard().onDeath();

                    // if the state changed to FINAL_FRENZY, no other players died and the FRENZY MODE starts
                    if (gameState == PossibleGameState.FINAL_FRENZY) {
                        gameInstance.setState(GameState.FINAL_FRENZY);
                        finalFrenzySetup();
                    }
                    return tempResponse;
                } else {
                    return tempResponse;
                }
            } else {
                buildInvalidResponse();
            }
        }

        if (gameState == PossibleGameState.MISSING_TERMINATOR_ACTION) {
            // only messages to use the terminator action and a powerup can be used!
            return handleTerminatorAsLastAction(receivedMessage);
        }

        switch (receivedMessage.getContent()) {
            case TERMINATOR_SPAWN:
                if (gameState == PossibleGameState.GAME_READY) {
                    // remember a player must see the powerups he has drawn before spawning the terminator!
                    return roundManager.handleTerminatorFirstSpawn((TerminatorSpawnRequest) receivedMessage);
                } else {
                    return buildInvalidResponse();
                }
            case DISCARD_POWERUP:
                if (gameState == PossibleGameState.GAME_READY) {
                    return roundManager.handleFirstSpawn((DiscardPowerupRequest) receivedMessage);
                } else {
                    return buildInvalidResponse();
                }
            case TERMINATOR:
                if (gameState == PossibleGameState.GAME_READY) {
                    return roundManager.handleTerminatorAction((UseTerminatorRequest) receivedMessage, gameState);
                } else if (gameState == PossibleGameState.GAME_STARTED) {
                    return roundManager.handleTerminatorAction((UseTerminatorRequest) receivedMessage, gameState);
                } else {
                    return buildInvalidResponse();
                }
            case POWERUP:
                if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY
                        || gameState == PossibleGameState.ACTIONS_DONE || gameState == PossibleGameState.FRENZY_ACTIONS_DONE) {
                    return roundManager.handlePowerupAction((PowerupRequest) receivedMessage);
                } else {
                    return buildInvalidResponse();
                }
            case MOVE:
                if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY) {
                    return roundManager.handleMoveAction((MoveRequest) receivedMessage, handleSecondAction());
                } else {
                    return buildInvalidResponse();
                }
            case MOVE_PICK:
                if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY) {
                    return roundManager.handlePickAction((MovePickRequest) receivedMessage, handleSecondAction());
                } else {
                    return buildInvalidResponse();
                }
            case SHOOT:
                if (gameState == PossibleGameState.GAME_STARTED || gameState == PossibleGameState.SECOND_ACTION || gameState == PossibleGameState.FINAL_FRENZY) {
                    return onShootMessage((ShootRequest) receivedMessage, handleSecondAction());
                } else {
                    return buildInvalidResponse();
                }
            case RELOAD:
                if (gameState == PossibleGameState.ACTIONS_DONE) {
                    return roundManager.handleReloadAction((ReloadRequest) receivedMessage);
                } else {
                    return buildInvalidResponse();
                }
            case PASS_TURN:
                if (gameState == PossibleGameState.ACTIONS_DONE || gameState == PossibleGameState.FRENZY_ACTIONS_DONE) {
                    return roundManager.handlePassAction();
                } else {
                    return buildInvalidResponse();
                }
            default:
                throw new InvalidGameStateException();
        }

    }

    private Message firstStateHandler(Message receivedMessage) {
        switch(receivedMessage.getContent()) {
            case GET_IN_LOBBY:
                return lobbyMessageHandler((LobbyMessage) receivedMessage);
            case GAME_SETUP:
                return setupMessageHandler((GameSetupMessage) receivedMessage);
            case COLOR:
                return colorRequestHandler();
            default:
                return buildInvalidResponse();
        }
    }

    private ColorResponse colorRequestHandler() {
        return new ColorResponse(lobby.getUnusedColors());
    }

    private void gameSetupHandler() {
        // first of all I set the terminator presence
        gameInstance.setTerminator(lobby.getTerminatorPresence());

        // then I can start adding players to the game with the color specified in their Lobby Message
        for(LobbyMessage player : lobby.getInLobbyPlayers()) {
            gameInstance.addPlayer(new UserPlayer(player.getSenderUsername(), player.getChosenColor(), new PlayerBoard()));
        }

        // in the end I set the map and the number of Skulls chosen
        try {
            gameInstance.setGameMap(lobby.getFavouriteMap());
        } catch (InvalidMapNumberException e) {
            // never reached here the lobby returns always a valid number
        }

        try {
            gameInstance.setKillShotNum(lobby.getFavouriteSkullsNum());
        } catch (InvalidKillshotNumberException e) {
            // never reached here the lobby returns always a valid number
        }

        // at this point gme should always be ready to start
        if(gameInstance.isGameReadyToStart()) {
            startingStateHandler();
        }

        // nothing to do here as we said game should always be ready to start at this point
    }

    private void startingStateHandler() {
        // first I start the game, the turnManager and set the state of the game
        gameInstance.startGame();
        roundManager.initTurnManager();
        changeState(PossibleGameState.GAME_READY);

        UserPlayer firstPlayer = roundManager.getTurnManager().getTurnOwner();

        // if the game has the terminator I set the first player state depending on the presence of the terminator
        if(gameInstance.isTerminatorPresent()) {
            firstPlayer.changePlayerState(PossiblePlayerState.SPAWN_TERMINATOR);
        } // else the state remains first spawn and it's ok to start

        // I first need to pick the two powerups for the first player playing
        roundManager.pickTwoPowerups();

        server.sendMessageToAll(new GameStartMessage(roundManager.getTurnManager().getTurnOwner().getUsername()));
    }

    private Response lobbyMessageHandler(LobbyMessage lobbyMessage) {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();

        // here time expiration has to be verified
        if(lobbyMessage.getContent() == MessageContent.GET_IN_LOBBY && !inLobbyPlayers.contains(lobbyMessage)) {
            if((lobby.getTerminatorPresence() && inLobbyPlayers.size() < 4) ||
                    (!lobby.getTerminatorPresence() && inLobbyPlayers.size() < 5) && lobbyMessage.getChosenColor() != null) {
                inLobbyPlayers.add(lobbyMessage);
            } else {
                return buildInvalidResponse();
            }
        } else if(lobbyMessage.getContent() == MessageContent.DISCONNECTION && inLobbyPlayers.contains(lobbyMessage)) {
            inLobbyPlayers.remove(lobbyMessage);
            removeVote(lobbyMessage.getSenderUsername());
            return new Response("Player removed from Lobby", MessageStatus.OK);
        } else {
            return buildInvalidResponse();
        }

        if(inLobbyPlayers.size() == MIN_PLAYERS) {
            // timer starts here
        }

        // then if the game has reached the maximum number of players also considering the terminator presence, it starts
        if((lobby.getTerminatorPresence() && inLobbyPlayers.size() == MAX_PLAYERS - 1) ||
                (!lobby.getTerminatorPresence() && inLobbyPlayers.size() == MAX_PLAYERS)) {
            gameSetupHandler();
            return new Response("Last player added to lobby, game is starting...", MessageStatus.OK);
        } else {
            return new Response("Player added to lobby", MessageStatus.OK);
        }
    }

    private void removeVote(String disconnectedPlayer) {
        ArrayList<GameSetupMessage> playersVotes = lobby.getVotedPlayers();

        for(GameSetupMessage setupMessage : playersVotes) {
            if(setupMessage.getSenderUsername().equals(disconnectedPlayer)) {
                playersVotes.remove(setupMessage);
            }
        }
    }

    private Response setupMessageHandler(GameSetupMessage setupMessage) {
        ArrayList<LobbyMessage> inLobbyPlayers = lobby.getInLobbyPlayers();
        ArrayList<GameSetupMessage> alreadyVotedPlayers = lobby.getVotedPlayers();

        for(LobbyMessage lobbyPlayer : inLobbyPlayers) {
            if(lobbyPlayer.getSenderUsername().equals(setupMessage.getSenderUsername()) && !alreadyVotedPlayers.contains(setupMessage)) {
                alreadyVotedPlayers.add(setupMessage);
                return new Response("Vote added", MessageStatus.OK);
            }
        }

        return new Response("Vote NOT added player already voted!", MessageStatus.ERROR);
    }



    private Response onGranadeMessage(Message receivedMessage) {
        switch (receivedMessage.getContent()) {
            case POWERUP:
                return roundManager.handleGranadeUsage((PowerupRequest) receivedMessage);
            case PASS_TURN:     // this is a "false" PASS_TURN, turn goes to the next damaged player by the "real" turnOwner
                // implementation then goes directly here
                roundManager.getTurnManager().increaseCount();
                roundManager.getTurnManager().giveTurn(roundManager.getTurnManager().getDamagedPlayers().get(roundManager.getTurnManager().getTurnCount()));
                return new Response("Granade not used", MessageStatus.OK);
            default:
                return new Response("Invalid Message while in granade state", MessageStatus.ERROR);
        }
    }

    private Response onShootMessage(ShootRequest shootRequest, boolean secondAction) {
        shootParameters = null;
        PowerupCard[] ownersPowerups = roundManager.getTurnManager().getTurnOwner().getPowerups();

        for (PowerupCard powerupCard : ownersPowerups) {
            if (powerupCard.getName().equals("TARGETING SCOPE")) {
                shootParameters = new ShootParameters(shootRequest, secondAction);
                changeState(PossibleGameState.SCOPE_USAGE);
                return new Response("Shoot Action can have SCOPE usage", MessageStatus.OK);
            }
        }

        // if turnOwner has no SCOPEs the shoot action is handled normally
        return roundManager.handleShootAction(shootRequest, null, secondAction);
    }

    private boolean handleSecondAction() {
        switch (gameState) {
            case GAME_STARTED:
                return false;
            case SECOND_ACTION:
                return true;
            case FINAL_FRENZY:
                return roundManager.getTurnManager().getAfterFrenzy().contains(roundManager.getTurnManager().getTurnOwner());
            default:
                throw new InvalidGameStateException();
        }
    }

    private Response handleTerminatorAsLastAction(Message receivedMessage) {
        switch (receivedMessage.getContent()) {
            case TERMINATOR:
                return roundManager.handleTerminatorAction((UseTerminatorRequest) receivedMessage, PossibleGameState.MISSING_TERMINATOR_ACTION);
            case POWERUP:
                return roundManager.handlePowerupAction((PowerupRequest) receivedMessage);
            default:
                return buildInvalidResponse();
        }
    }

    private void finalFrenzySetup() {
        // boards flipping setup
        if (gameInstance.isTerminatorPresent() && gameInstance.getTerminator().getPlayerBoard().getDamageCount() == 0) {
            try {
                gameInstance.getTerminator().getPlayerBoard().flipBoard();
            } catch (AdrenalinaException e) {
                // exceptions thrown can never be reached thanks to the control done
            }
        }

        for (UserPlayer player : gameInstance.getPlayers()) {
            if (player.getPlayerBoard().getDamageCount() == 0) {
                try {
                    player.getPlayerBoard().flipBoard();
                } catch (AdrenalinaException e) {
                    // exceptions thrown can never be reached thanks to the control done
                }
            }
        }
    }

    private void distributePoints(Player deathPlayer) {
        PlayerBoard deathsPlayerBoard = deathPlayer.getPlayerBoard();
        Integer[] boardPoints = deathsPlayerBoard.getBoardPoints();
        List<String> pointsReceivers = deathsPlayerBoard.getDamages().stream().distinct().collect(Collectors.toList());
        Map<String, DamageCountWrapper> receivers = new HashMap<>();
        Player firstBlooder;

        for (int i = 0; i < pointsReceivers.size(); ++i) {
            int frequency = Collections.frequency(deathsPlayerBoard.getDamages(), pointsReceivers.get(i));
            DamageCountWrapper damageCountWrapper = new DamageCountWrapper(i, frequency);
            receivers.put(pointsReceivers.get(i), damageCountWrapper);
        }

        Map<String, DamageCountWrapper> orderedReceivers = receivers
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        if (deathsPlayerBoard.isBoardFlipped()) {     // first blood assignment
            firstBlooder = gameInstance.getUserPlayerByUsername(deathsPlayerBoard.getDamages().get(0));
            firstBlooder.addPoints(1);
        }

        int pointsIndex = 0;
        for (Map.Entry entry : orderedReceivers.entrySet()) {
            Player tempReceiver = gameInstance.getUserPlayerByUsername((String) entry.getKey());
            tempReceiver.addPoints(boardPoints[pointsIndex]);
            ++pointsIndex;
        }
    }

    private void moveSkull(Player deathPlayer) {
        int points;
        KillShot killShot;
        String killer = deathPlayer.getPlayerBoard().getDamages().get(10);

        if (deathPlayer.getPlayerBoard().getDamages().size() == 12) {
            points = 2;
        } else {
            points = 1;
        }

        killShot = new KillShot(killer, points);
        if (gameInstance.remainingSkulls() == 0) {
            gameInstance.getFinalFrenzyKillShots().add(killShot);
        } else {
            gameInstance.addKillShot(killShot);
        }
    }

    private void handleLastPointsDistribution() {
        List<UserPlayer> players = gameInstance.getPlayers();
        Terminator terminator = (Terminator) gameInstance.getTerminator();

        if (gameInstance.isTerminatorPresent() && terminator.getPlayerBoard().getDamageCount() > 0) {
            // in the last distribution each damaged player counts as a dead one to calculate points
            distributePoints(terminator);
        }

        for (UserPlayer player : players) {
            if (player.getPlayerBoard().getDamageCount() > 0) {
                // in the last distribution each damaged player counts as a dead one to calculate points
                distributePoints(player);
                if (gameInstance.getDeathPlayers().contains(player)) {
                    moveSkull(player);
                }
            }
        }
    }

    private void handleKillShotTrackDistribution() {
        Integer[] trackerPoints = gameInstance.getTrackerPoints();
        ArrayList<KillShot> killShotTracker = gameInstance.getKillShotTrack();
        ArrayList<KillShot> finalFrenzyTracker = gameInstance.getFinalFrenzyKillShots();
        ArrayList<String> killers = new ArrayList<>();
        ArrayList<String> distinctKillers;
        Map<String, DamageCountWrapper> receivers = new HashMap<>();

        // first I add the killers on the tracker
        for (KillShot killShot : killShotTracker) {
            killers.add(killShot.getKiller());
        }

        // then I add the killers of the frenzy mode
        for (KillShot killShot : finalFrenzyTracker) {
            killers.add(killShot.getKiller());
        }

        distinctKillers = killers.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        for (int i = 0; i < distinctKillers.size(); ++i) {
            int frequency = Collections.frequency(killers, distinctKillers.get(i));
            frequency += getPointsOnKillShots(distinctKillers.get(i), killShotTracker, finalFrenzyTracker);
            DamageCountWrapper damageCountWrapper = new DamageCountWrapper(i, frequency);
            receivers.put(distinctKillers.get(i), damageCountWrapper);
        }

        Map<String, DamageCountWrapper> orderedReceivers = receivers
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int pointsIndex = 0;
        for (Map.Entry entry : orderedReceivers.entrySet()) {
            Player tempReceiver = gameInstance.getUserPlayerByUsername((String) entry.getKey());
            tempReceiver.addPoints(trackerPoints[pointsIndex]);
            ++pointsIndex;
        }
    }

    private int getPointsOnKillShots(String killer, ArrayList<KillShot> killShotTracker, ArrayList<KillShot> finalFrenzyTracker) {
        int pointsOnKillShots = 0;

        for (KillShot killShot : killShotTracker) {
            if (killShot.getKiller().equals(killer)) {
                pointsOnKillShots += killShot.getPoints();
            }
        }

        for (KillShot killShot : finalFrenzyTracker) {
            if (killShot.getKiller().equals(killer)) {
                pointsOnKillShots += killShot.getPoints();
            }
        }

        return pointsOnKillShots;
    }

    private WinnersResponse declareWinner() {
        List<UserPlayer> players = gameInstance.getPlayers();
        ArrayList<Player> tiePlayers = new ArrayList<>();
        ArrayList<Player> winners = new ArrayList<>();

        ArrayList<Player> orderedPlayers = players.stream().sorted().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        int maxPoints = orderedPlayers.get(0).getPoints();
        tiePlayers.add(orderedPlayers.get(0));
        for (int i = 1; i < orderedPlayers.size(); ++i) {
            if (orderedPlayers.get(i).getPoints() == maxPoints) {
                tiePlayers.add(orderedPlayers.get(i));
            }
        }

        if (gameInstance.isTerminatorPresent()) {
            if (gameInstance.getTerminator().getPoints() > maxPoints) {
                winners.add(gameInstance.getTerminator());
                return new WinnersResponse(winners);
            } else if (gameInstance.getTerminator().getPoints() == maxPoints) {
                tiePlayers.add(gameInstance.getTerminator());
            }
        }

        if (tiePlayers.size() == 1) {
            return new WinnersResponse(tiePlayers);
        } else {
            winners = handleTiePlayers(tiePlayers);
            return new WinnersResponse(winners);
        }
    }

    private ArrayList<Player> handleTiePlayers(ArrayList<Player> tiePlayers) {
        ArrayList<KillShot> killShotTracker = gameInstance.getKillShotTrack();
        ArrayList<Player> winner = new ArrayList<>();

        for (KillShot killShot : killShotTracker) {
            for (Player player : tiePlayers) {
                if (player.getUsername().equals(killShot.getKiller())) {
                    winner.add(player);
                    return winner;
                }
            }
        }

        return tiePlayers;
    }

    private Response buildInvalidResponse() {
        return new Response("Invalid message", MessageStatus.ERROR);
    }

    class ShootParameters {
        ShootRequest shootRequest;
        boolean secondAction;

        ShootParameters(ShootRequest shootRequest, boolean secondAction) {
            this.shootRequest = shootRequest;
            this.secondAction = secondAction;
        }
    }

    class DamageCountWrapper implements Comparable<DamageCountWrapper> {
        final int position;
        final int damage;

        DamageCountWrapper(int position, int damage) {
            this.position = position;
            this.damage = damage;
        }

        @Override
        public int compareTo(DamageCountWrapper otherDamageCountWrapper) {
            // Reversed compare to get the best damage dealer first
            if (this.damage == otherDamageCountWrapper.damage) {
                return this.position - otherDamageCountWrapper.position;
            } else {
                return otherDamageCountWrapper.damage - this.damage;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DamageCountWrapper that = (DamageCountWrapper) o;
            return position == that.position &&
                    damage == that.damage;
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, damage);
        }
    }
}