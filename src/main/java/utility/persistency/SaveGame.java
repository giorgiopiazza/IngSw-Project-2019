package utility.persistency;

import controller.GameManager;
import exceptions.game.ReloadException;
import network.server.Server;

import java.io.*;
import java.util.logging.Level;

public class SaveGame {

    private SaveGame() {
        throw new IllegalStateException("Utility Class");
    }

    /**
     * Method used to store locally the status of the {@link model.Game Game} whenever used
     *
     * @param gameManager the only needed parameter used to save the status
     */
    public static void saveGame(GameManager gameManager) {
        PersistencyClass persistencyClass = new PersistencyClass(gameManager);

        try (FileOutputStream gameSaved = new FileOutputStream(new File("gameSaved.adrenaline"))) {
            ObjectOutputStream outGame = new ObjectOutputStream(gameSaved);

            outGame.writeObject(persistencyClass);
            Server.LOGGER.log(Level.INFO, "Game State saved after valid action");

            outGame.close();
        } catch (FileNotFoundException e) {
            // never reached we create the file we need each time
        } catch (IOException e) {
            Server.LOGGER.severe(e.getMessage());
        }
    }

    public static GameManager loadGame(Server server) {
        PersistencyClass persistencyClass;

        try (FileInputStream gameSaved = new FileInputStream(new File("gameSaved.adrenaline"))) {
            ObjectInputStream inGame = new ObjectInputStream(gameSaved);

            // first I read the saved State of the Game
            persistencyClass = (PersistencyClass) inGame.readObject();

            // then I restart the beginning class of each Game
            GameManager newGameManager = new GameManager(server, persistencyClass.getGameManager());

            // in the end I set back the state of the real Game
            newGameManager.getGameInstance().loadGame(persistencyClass.getGameManager().getGameInstance(), persistencyClass.getPlayersCopy());

            // in the end I also set back the TurnManager to the new GameManager
            newGameManager.getRoundManager().initTurnManager(persistencyClass.getTurnManager());
            return newGameManager;
        } catch (FileNotFoundException e) {
            Server.LOGGER.severe("There exist no file to be loaded!");
        } catch (IOException | ClassNotFoundException e) {
            Server.LOGGER.severe(e.getMessage());
        }

        throw new ReloadException();
    }
}
