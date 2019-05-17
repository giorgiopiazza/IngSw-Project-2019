package controller;

import enumerations.GameState;
import enumerations.PlayerColor;
import enumerations.PossibleGameState;
import exceptions.game.InvalidGameStateException;
import exceptions.game.MaxPlayerException;
import model.Game;
import model.player.KillShot;
import model.player.Player;
import model.player.PlayerBoard;
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
            while (changingState == PossibleGameState.GAME_STARTED || changingState == PossibleGameState.SECOND_ACTION) {
                changingState = roundManager.handleDecision(changingState);

                if (changingState == PossibleGameState.TERMINATOR_USED) {
                    roundManager.removeTerminatorAction();
                }

                while (changingState == PossibleGameState.ACTIONS_DONE) {
                    roundManager.setReloadAction();
                    changingState = roundManager.handleReloadDecision(changingState);
                    if (changingState == PossibleGameState.PASS_TURN) {
                        if (gameInstance.getDeathPlayers().size() != 0) {
                            changingState = handleDeathPlayers();
                        }

                        // after death players have been respawned if no skulls are left on the track frenzy mode is activated
                        if (changingState == PossibleGameState.FINAL_FRENZY) {
                            gameInstance.setState(GameState.FINAL_FRENZY);
                            finalFrenzySetup();
                            finalFrenzyRun();
                            // TODO handle frenzy and finish state
                        }

                        // TODO pass turn handling

                        roundManager.setInitialActions();
                    }
                }
            }
        } else throw new InvalidGameStateException();
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
        for(UserPlayer player : deathPlayers) {
            distributePoints(player);
            roundManager.handlePlayerRespawn(player);
            moveSkull(player);
            // then I clear the board and add a skull to its points counter
            player.getPlayerBoard().onDeath();
        }

        if(deathPlayers.size() > 1 || (deathPlayers.size() == 1 && terminatorDied)) {
            addDoubleKillPoint();
        }

        if(gameInstance.remainingSkulls() == 0) {
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

    }

    private void finalFrenzyRun() {

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


