package model;

import enumerations.PlayerColor;
import enumerations.PossibleAction;
import exceptions.game.InvalidMapNumberException;
import exceptions.player.MaxCardsInHandException;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.GameMap;
import model.player.Bot;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.PowerupParser;
import utility.WeaponParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameSerializedTest {

    Game game;

    @BeforeEach
    void before() {
        game = Game.getInstance();
    }

    @Test
    void noBot() throws InvalidMapNumberException, MaxCardsInHandException {
        game.init();
        game.initializeDecks();
        game.setGameMap(GameMap.MAP_3);

        UserPlayer p1 = new UserPlayer("1");
        p1.setWeapons(new ArrayList<>(List.of((WeaponCard) WeaponParser.parseCards().draw())));
        UserPlayer p2 = new UserPlayer("2");
        p2.setWeapons(new ArrayList<>(List.of((WeaponCard) WeaponParser.parseCards().draw())));
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(new UserPlayer("asd"));

        GameSerialized gameSerialized = new GameSerialized("1");

        gameSerialized.getPlayerWeapons("1");
        gameSerialized.getPlayerWeapons("2");
        gameSerialized.getPlayerWeapons("3");
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
    void bot() throws InvalidMapNumberException {
        game.init();
        game.initializeDecks();
        game.setGameMap(GameMap.MAP_3);

        UserPlayer p1 = new UserPlayer("1");
        p1.setPossibleActions(new HashSet<>(List.of(PossibleAction.BOT_ACTION)));
        p1.setSpawningCard((PowerupCard) PowerupParser.parseCards().draw());
        UserPlayer p2 = new UserPlayer("2");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(new UserPlayer("asd"));
        game.setBot(true);
        game.buildTerminator();

        GameSerialized gameSerialized = new GameSerialized("1");
        gameSerialized.getBot();
        gameSerialized.getPowerupCards();
        gameSerialized.getPowerupCards();
        gameSerialized.isBotActionDone();
        gameSerialized.getAllPlayers();
        assertTrue(gameSerialized.isBotPresent());
    }
}