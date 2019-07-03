package model.player;

import enumerations.PlayerColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    @Test
    void bot() {
        Bot bot = new Bot(PlayerColor.GREEN, new PlayerBoard());
        bot.setPosition(new PlayerPosition(0,0));

        Bot bot1 = new Bot(bot);

        assertEquals(bot, bot1);
        bot.setSpawnTurn(!bot.isSpawnTurn());
        bot.hashCode();
        assertNotEquals(bot.isSpawnTurn(), bot1.isSpawnTurn());
    }
}