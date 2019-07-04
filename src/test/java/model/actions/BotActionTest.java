package model.actions;

import enumerations.PlayerColor;
import exceptions.actions.InvalidActionException;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.map.GameMap;
import model.player.Bot;
import model.player.PlayerBoard;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BotActionTest {

    Game game;
    UserPlayer p1;
    UserPlayer p2;
    UserPlayer p3;
    Bot bot;

    @BeforeEach
    void before() throws InvalidMapNumberException {
        game = Game.getInstance();
        game.init();
        game.initializeDecks();

        p1 = new UserPlayer("1", PlayerColor.GREEN, new PlayerBoard());
        p2 = new UserPlayer("2", PlayerColor.YELLOW, new PlayerBoard());
        p3 = new UserPlayer("3", PlayerColor.GREY, new PlayerBoard());

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.setGameMap(GameMap.MAP_3);
    }

    @Test
    void botAction() throws InvalidActionException {
        game.setBot(true);
        game.buildTerminator();
        bot = (Bot) game.getBot();
        game.startGame();
        p1.setPosition(new PlayerPosition(0,0));
        p2.setPosition(new PlayerPosition(0,1));
        p3.setPosition(new PlayerPosition(0,0));
        bot.setPosition(new PlayerPosition(0,0));

        BotAction action;

        action = new BotAction(p1, p1, new PlayerPosition(0,1));
        assertThrows(InvalidActionException.class, action::validate);
        action = new BotAction(p1, p2, new PlayerPosition(-1,1));
        assertThrows(InvalidActionException.class, action::validate);
        action = new BotAction(p1, p2, new PlayerPosition(3,1));
        assertThrows(InvalidActionException.class, action::validate);
        action = new BotAction(p1, p2, new PlayerPosition(0,-1));
        assertThrows(InvalidActionException.class, action::validate);
        action = new BotAction(p1, p2, new PlayerPosition(0,4));
        assertThrows(InvalidActionException.class, action::validate);

        action = new BotAction(p1, null, new PlayerPosition(0,0));
        assertThrows(InvalidActionException.class, action::validate);

        action = new BotAction(p1, p3, new PlayerPosition(0,0));
        assertTrue(action.validate());

        action = new BotAction(p1, p3, new PlayerPosition(0,2));
        assertThrows(InvalidActionException.class, action::validate);

        action = new BotAction(p1, p2, new PlayerPosition(0,1));
        assertTrue(action.validate());

        action = new BotAction(p1, null, new PlayerPosition(0,1));
        assertThrows(InvalidActionException.class, action::validate);

        bot.setPosition(new PlayerPosition(2,2));
        action = new BotAction(p1, null, new PlayerPosition(2,3));
        assertTrue(action.validate());
        action.execute();

        action = new BotAction(p1, p2, new PlayerPosition(0,1));
        assertEquals(0, bot.getPlayerBoard().getDamageCount());
        action.execute();
        assertEquals(1, p2.getPlayerBoard().getDamageCount());

        bot.getPlayerBoard().setDamages(List.of("", "", "", "", "", ""));
        action = new BotAction(p1, p2, new PlayerPosition(0,1));
        action.execute();
        assertEquals(2, p2.getPlayerBoard().getDamageCount());
        assertEquals(1, p2.getPlayerBoard().getMarkCount());
    }
}