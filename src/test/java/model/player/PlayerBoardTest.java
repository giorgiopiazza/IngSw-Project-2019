package model.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class PlayerBoardTest {
    private PlayerBoard playerBoard;
    private Player damageDealer;

    @BeforeEach
    public void before() {
        playerBoard = new PlayerBoard();
        damageDealer = mock(Player.class);

    }
}
