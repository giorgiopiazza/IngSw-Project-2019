package model;

import enumerations.PlayerColor;
import exceptions.game.InvalidMapNumberException;
import model.map.GameMap;
import model.player.Bot;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameSerializedTest {

    Game game;

    @BeforeEach
    void before() throws InvalidMapNumberException {
        game = Game.getInstance();
        game.init();
        game.initializeDecks();
        game.setGameMap(GameMap.MAP_3);
    }

    @Test
    void noBot() {
        UserPlayer p1 = new UserPlayer("1");
        UserPlayer p2 = new UserPlayer("2");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(new UserPlayer("asd"));

        GameSerialized gameSerialized = new GameSerialized("1");

        gameSerialized.setPlayers(List.of(mock(UserPlayer.class)));
        gameSerialized.setPlayers(null);
        gameSerialized.getPlayers();
        gameSerialized.getPowerups();
        gameSerialized.getAllPlayers();
        gameSerialized.getSpawningPowerup();
        gameSerialized.getGameMap();
        gameSerialized.getPoints();
        gameSerialized.getCurrentState();
        gameSerialized.getFinalFrenzyKillShots();
        gameSerialized.getKillShotNum();
        gameSerialized.getKillShotsTrack();
        gameSerialized.getSpawnPowerup();
        gameSerialized.toString();
        gameSerialized.setBot(new Bot(PlayerColor.GREEN, new PlayerBoard()));
        gameSerialized.setGameMap(new GameMap(GameMap.MAP_3));
    }

    @Test
    void bot() {

    }
}