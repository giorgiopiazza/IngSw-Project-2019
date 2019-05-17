package controller;

import enumerations.GameState;
import enumerations.PlayerColor;
import enumerations.PossibleGameState;
import exceptions.AdrenalinaException;
import exceptions.game.InvalidGameStateException;
import exceptions.game.MaxPlayerException;
import model.Game;
import model.player.KillShot;
import model.player.Player;
import model.player.PlayerBoard;
import model.player.Terminator;
import model.player.UserPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private final Game gameInstance;
    private static PossibleGameState gameState;
    private RoundManager roundManager;

    public GameManager() {
        gameState = PossibleGameState.GAME_ROOM;
        this.gameInstance = Game.getInstance();
        this.roundManager = new RoundManager(this.gameInstance);
    }

    public static void changeState(PossibleGameState changeState) {
        gameState = changeState;
    }


    private void gameSetup() {
        Scanner in = new Scanner(System.in);

        System.out.println("Provide the game setup informations: \n\n");

        for (; ; ) {
            System.out.println("Insert the map you want to play with (1-4): \n");

            try {
                gameInstance.setGameMap(in.nextInt());
                break;
            } catch (Exception e) {
                System.out.println("Invalid Map Number!\n");
                // Re-ask input
            }
        }

        for (; ; ) {
            System.out.println("Decide if you want to play with a terminator (true/false): \n");

            try {
                gameInstance.setTerminator(in.nextBoolean());
                break;
            } catch (Exception e) {
                System.out.println("Not a boolean value found!\n");
                // Re-ask input
            }
        }

        for (; ; ) {
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

    private void roomSetup() {
        Scanner in = new Scanner(System.in);

        String userName;
        boolean ready = false;

        System.out.println("Please insert the usernames of the players playing: \n");

        while (!gameInstance.isGameReadyToStart(ready)) {
            System.out.println("Username >>> ");
            userName = in.nextLine();
            PlayerColor colorChosen;

            if (!gameInstance.doesPlayerExists(userName)) {
                for (; ; ) {
                    System.out.println(userName + " provide the color you have chosen: ");

                    try {
                        colorChosen = PlayerColor.getColor(in.nextLine());
                        if (!gameInstance.isColorUsed(colorChosen)) {
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
                    ready = Boolean.parseBoolean(in.nextLine());
                } catch (MaxPlayerException e) {
                    // the game has reached the maximum number of players
                    break;
                }
            }
        }

        gameState = PossibleGameState.GAME_READY;
    }

    public void run() {
        System.out.println("Welcome to the very first version of the game: \n\n");

        // starting setup
        if (gameState == PossibleGameState.GAME_ROOM) {
            gameSetup();
            roomSetup();
        } else throw new InvalidGameStateException();

        // game is ready and can be started
        if (gameState == PossibleGameState.GAME_READY) {
            gameInstance.startGame();
            System.out.println("ADRENALINE is ready to start! \n");
            System.out.println(gameInstance.getPlayers().get(0).getUsername() + " is the first player \n");

            while (gameState == PossibleGameState.GAME_READY) {
                roundManager.initTurnManager();
                roundManager.handleFirstRound();
            }
        } else throw new InvalidGameStateException();

        // now game has started
        roundManager.setInitialActions();
        if (gameState == PossibleGameState.GAME_STARTED) {
            PossibleGameState changingState = gameState;
            // this while manages the entire game changing turns between players
            while (changingState == PossibleGameState.GAME_STARTED || changingState == PossibleGameState.SECOND_ACTION ||
                    changingState == PossibleGameState.TERMINATOR_USED || changingState == PossibleGameState.FINAL_FRENZY) {
                changingState = roundManager.handleDecision(changingState);

                if (changingState == PossibleGameState.TERMINATOR_USED) {
                    roundManager.removeTerminatorAction();
                }

                if (changingState == PossibleGameState.MISSING_TERMINATOR_ACTION) {
                    changingState = roundManager.handleTerminatorAsLastAction();
                }

                while (changingState == PossibleGameState.ACTIONS_DONE) {
                    roundManager.setReloadAction();
                    changingState = roundManager.handleReloadDecision(changingState);
                    if (changingState == PossibleGameState.PASS_TURN) {
                        if (!gameInstance.getDeathPlayers().isEmpty()) {
                            changingState = handleDeathPlayers();
                        }

                        // after death players have been respawned if no skulls are left on the track frenzy mode is activated
                        if (changingState == PossibleGameState.FINAL_FRENZY) {
                            gameInstance.setState(GameState.FINAL_FRENZY);
                            finalFrenzySetup();
                        }

                        // current player must have only one possible action when passing his turn!
                        if (roundManager.getTurnManager().getTurnOwner().getPossibleActions().size() == 1) {
                            changingState = roundManager.handleNextTurn();
                            roundManager.setInitialActions();
                        }
                    }
                }

                while (changingState == PossibleGameState.FRENZY_ACTIONS_DONE) {
                    changingState = roundManager.handlePowerupDecision(changingState);
                    if (changingState == PossibleGameState.PASS_TURN) {
                        if (roundManager.getTurnManager().getTurnOwner().equals(roundManager.getTurnManager().getLastPlayer())) {
                            gameState = PossibleGameState.ENDGAME;
                            changingState = PossibleGameState.ENDGAME;
                        }

                        if (!gameInstance.getDeathPlayers().isEmpty() && changingState != PossibleGameState.ENDGAME) {
                            changingState = handleDeathPlayers();
                        }

                        if (changingState == PossibleGameState.PASS_TURN) {
                            changingState = roundManager.handleNextTurn();
                            roundManager.setInitialActions();
                        }
                    }
                }
            }
        } else throw new InvalidGameStateException();

        // end of the game management: last damaged boards points distribution, killShotTracker distribution and winner declaration
        if (gameState == PossibleGameState.ENDGAME) {
            handleLastPointsDistribution();
            handleKillShotTrackDistribution();
            declareWinner();
        }
    }

    private PossibleGameState handleDeathPlayers() {
        boolean terminatorDied = false;
        List<UserPlayer> deathPlayers = gameInstance.getDeathPlayers();

        // terminator deaths management
        if (gameInstance.isTerminatorPresent() && gameInstance.getTerminator().getPlayerBoard().getDamageCount() > 10) {
            terminatorDied = true;
            distributePoints(gameInstance.getTerminator());
            roundManager.handleTerminatorRespawn();
            moveSkull(gameInstance.getTerminator());
            // then I clear the board and add a skull to its points counter
            gameInstance.getTerminator().getPlayerBoard().onDeath();
        }

        // players death management
        for (UserPlayer player : deathPlayers) {
            distributePoints(player);
            roundManager.handlePlayerRespawn(player);
            moveSkull(player);
            // then I clear the board and add a skull to its points counter
            player.getPlayerBoard().onDeath();
        }

        if (deathPlayers.size() > 1 || (deathPlayers.size() == 1 && terminatorDied)) {
            addDoubleKillPoint();
        }

        if (gameInstance.remainingSkulls() == 0 && !gameInstance.getState().equals(GameState.FINAL_FRENZY)) {
            return PossibleGameState.FINAL_FRENZY;
        } else {
            return PossibleGameState.PASS_TURN;
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

    private void addDoubleKillPoint() {
        roundManager.getTurnManager().getTurnOwner().addPoints(1);
    }

    private void handleLastPointsDistribution() {
        List<UserPlayer> players = gameInstance.getPlayers();
        Terminator terminator = (Terminator) gameInstance.getTerminator();

        if (gameInstance.isTerminatorPresent()) {
            if (terminator.getPlayerBoard().getDamageCount() > 0) {
                // in the last distribution each damaged player counts as a dead one to calculate points
                distributePoints(terminator);
            }
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

    private void finalFrenzySetup() {
        roundManager.getTurnManager().setFrenzyPlayers();

        // boards flipping setup
        if (gameInstance.isTerminatorPresent()) {
            if (gameInstance.getTerminator().getPlayerBoard().getDamageCount() == 0) {
                try {
                    gameInstance.getTerminator().getPlayerBoard().flipBoard();
                } catch (AdrenalinaException e) {
                    // exceptions thrown can never be reached thanks to the control done
                }
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

        roundManager.getTurnManager().setLastPlayer();
        roundManager.handleNextTurn();
    }

    private void declareWinner() {
        List<UserPlayer> players = gameInstance.getPlayers();
        List<Player> tiePlayers = new ArrayList<>();
        List<Player> winners;

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
                System.out.println("GAME HAS ENDED, THE WINNER IS THE TERMINATOR YOU NOOBS!");
                return;
            } else if (gameInstance.getTerminator().getPoints() == maxPoints) {
                tiePlayers.add(gameInstance.getTerminator());
            }
        }

        if (tiePlayers.size() == 1) {
            System.out.println("GAME HAS ENDED, THE WINNER IS: " + orderedPlayers.get(0).getUsername());
        } else {
            winners = handleTiePlayers(tiePlayers);
            if (winners.size() == 1) {
                System.out.println("GAME HAS ENDED, THE WINNER IS: " + winners.get(0));
            } else {
                System.out.println("GAME HAS ENDED WITH A TIE! WINNERS ARE: ");
                for (Player player : winners) {
                    System.out.println(player.getUsername());
                }
            }
        }
    }

    private List<Player> handleTiePlayers(List<Player> tiePlayers) {
        ArrayList<KillShot> killShotTracker = gameInstance.getKillShotTrack();
        List<Player> winner = new ArrayList<>();

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