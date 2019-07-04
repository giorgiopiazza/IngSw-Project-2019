package model.actions;

import enumerations.PlayerColor;
import model.Game;
import model.player.Bot;
import model.player.PlayerBoard;
import model.player.UserPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BotActionTest {

    Game game;
    UserPlayer p1;
    UserPlayer p2;
    UserPlayer p3;
    Bot bot;

    @BeforeEach
    void before() {
        game = Game.getInstance();
        game.init();
        game.initializeDecks();
        p1 = new UserPlayer("1", PlayerColor.GREEN, new PlayerBoard());
        p2 = new UserPlayer("2", PlayerColor.YELLOW, new PlayerBoard());
        p3 = new UserPlayer("3", PlayerColor.GREY, new PlayerBoard());
        bot = new Bot(PlayerColor.BLUE, new PlayerBoard());
    }

    @Test
    void botAction() {
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setBot(true);
        game.buildTerminator();
        bot = (Bot) game.getBot();
    }
}