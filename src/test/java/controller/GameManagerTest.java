package controller;

import enumerations.*;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import network.message.*;
import network.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;


class GameManagerTest {
    private GameManager gameManager;
    private Server server;
    private Game game;

    @BeforeEach
    void before() throws InvalidMapNumberException {
        server = mock(Server.class);

        game = Game.getInstance();
        game.init();
    }

    @Test
    void constructor() {
        gameManager = new GameManager(server, true, 8, 100);

        gameManager = new GameManager(server, mock(GameManager.class), 100);
    }

    @Test
    void generalMethods() {
        gameManager = new GameManager(server, true, 8, 100);
        assertEquals(game, gameManager.getGameInstance());

        gameManager.changeState(PossibleGameState.GAME_STARTED);
        assertEquals(PossibleGameState.GAME_STARTED, gameManager.getGameState());
    }

    @Test
    void lobby() throws Exception {
        gameManager = new GameManager(server, true, 8, 10000);

        Response response = (Response) gameManager.onMessage(
                new LobbyMessage("tose", null, PlayerColor.GREY, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        ColorResponse colorResponse = (ColorResponse) gameManager.onMessage(new ColorRequest("gio", null));
        assertEquals(4, colorResponse.getColorList().size());

        GameVoteResponse gameVoteResponse = (GameVoteResponse) gameManager.onMessage(new GameVoteMessage("tose", null, 1));
        assertEquals(MessageStatus.OK, gameVoteResponse.getStatus());

        gameVoteResponse = (GameVoteResponse) gameManager.onMessage(new GameVoteMessage("tose", null, 1));
        assertEquals(MessageStatus.ERROR, gameVoteResponse.getStatus());

        gameVoteResponse = (GameVoteResponse) gameManager.onMessage(new GameVoteMessage("gio", null, 1));
        assertEquals(MessageStatus.ERROR, gameVoteResponse.getStatus());

        response = (Response) gameManager.onMessage(new PassTurnRequest("gio", null));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        assertFalse(gameManager.isLobbyFull());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.GREY, true));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.GREY, false));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.GREY, false));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.BLUE, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        gameVoteResponse = (GameVoteResponse) gameManager.onMessage(new GameVoteMessage("gio", null, 1));
        assertEquals(MessageStatus.OK, gameVoteResponse.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.BLUE, true));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.BLUE, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("piro", null, PlayerColor.GREEN, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("test1", null, PlayerColor.PURPLE, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("test2", null, PlayerColor.YELLOW, false));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        assertEquals(PossibleGameState.GAME_STARTED, gameManager.getGameState());

        assertTrue(gameManager.isLobbyFull());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("piro", null, PlayerColor.GREEN, true));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new DisconnectionMessage("piro"));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onConnectionMessage(new LobbyMessage("piro", null, null, true));
        assertEquals(MessageStatus.OK, response.getStatus());

        ReconnectionMessage reconnectionMessage = (ReconnectionMessage) gameManager.onConnectionMessage(new LobbyMessage("piro", null, null, false));
        assertNotNull(reconnectionMessage.getGameStateMessage());

        response = (Response) gameManager.onConnectionMessage(new LobbyMessage("piro", null, null, false));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onConnectionMessage(new LobbyMessage("test1", null, null, true));
        assertEquals(MessageStatus.OK, response.getStatus());

        gameManager.sendGrenadePrivateUpdates();

        response = (Response) gameManager.onConnectionMessage(new LobbyMessage("tose", null, null, true));
        assertEquals(MessageStatus.OK, response.getStatus());

        assertEquals(PossibleGameState.GAME_ENDED, gameManager.getGameState());
    }

    @Test
    void gameWithBot() throws Exception {
        gameManager = new GameManager(server, true, 8, 10000);

        Response response = (Response) gameManager.onMessage(
                new LobbyMessage("tose", null, PlayerColor.GREY, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        gameManager.onMessage(new GameVoteMessage("tose", null, 1));

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.BLUE, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("piro", null, PlayerColor.GREEN, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("test1", null, PlayerColor.PURPLE, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        String turnOwner = gameManager.getRoundManager().getTurnManager().getTurnOwner().getUsername();

        assertNull(gameManager.getUserPlayerState(null));

        for (Player p : gameManager.getGameInstance().getPlayers()) {
            if (!p.getUsername().equals(turnOwner)) {
                assertEquals(UserPlayerState.FIRST_ACTION, gameManager.getUserPlayerState(p.getUsername()));
            }
        }

        assertEquals(UserPlayerState.BOT_SPAWN, gameManager.getUserPlayerState(turnOwner));

        response = (Response) gameManager.onMessage(
                new BotSpawnRequest(turnOwner, null, RoomColor.BLUE));
        assertEquals(MessageStatus.OK, response.getStatus());

        assertEquals(UserPlayerState.SPAWN, gameManager.getUserPlayerState(turnOwner));

        response = (Response) gameManager.onMessage(
                new DiscardPowerupRequest(turnOwner, null, 0));

        assertEquals(MessageStatus.OK, response.getStatus());

        assertEquals(UserPlayerState.FIRST_ACTION, gameManager.getUserPlayerState(turnOwner));

        response = (Response) gameManager.onMessage(
                new MoveRequest(turnOwner, null, getMovePosition(game.getPlayerByName(turnOwner).getPosition(), false)));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new MoveRequest(turnOwner, null, getMovePosition(game.getPlayerByName(turnOwner).getPosition(), true)));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new MovePickRequest(turnOwner, null, game.getPlayerByName(turnOwner).getPosition(), null, null, null));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new MovePickRequest(turnOwner, null, game.getPlayerByName(turnOwner).getPosition(), null, null, null));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new PassTurnRequest(turnOwner, null));
        assertEquals(MessageStatus.OK, response.getStatus());
    }

    @Test
    void gameWithoutBot() throws Exception {
        gameManager = new GameManager(server, false, 8, 10000);

        Response response = (Response) gameManager.onMessage(
                new LobbyMessage("tose", null, PlayerColor.GREY, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        gameManager.onMessage(new GameVoteMessage("tose", null, 1));

        response = (Response) gameManager.onMessage(
                new LobbyMessage("gio", null, PlayerColor.BLUE, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("piro", null, PlayerColor.GREEN, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("test1", null, PlayerColor.PURPLE, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new LobbyMessage("test2", null, PlayerColor.YELLOW, false));
        assertEquals(MessageStatus.OK, response.getStatus());

        String turnOwner = gameManager.getRoundManager().getTurnManager().getTurnOwner().getUsername();

        for (Player p : gameManager.getGameInstance().getPlayers()) {
            if (!p.getUsername().equals(turnOwner)) {
                assertEquals(UserPlayerState.FIRST_ACTION, gameManager.getUserPlayerState(p.getUsername()));
            }
        }

        assertEquals(UserPlayerState.SPAWN, gameManager.getUserPlayerState(turnOwner));

        response = (Response) gameManager.onMessage(
                new DiscardPowerupRequest(turnOwner, null, 0));

        assertEquals(MessageStatus.OK, response.getStatus());

        assertEquals(UserPlayerState.FIRST_ACTION, gameManager.getUserPlayerState(turnOwner));

        response = (Response) gameManager.onMessage(
                new MoveRequest(turnOwner, null, getMovePosition(game.getPlayerByName(turnOwner).getPosition(), false)));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new MoveRequest(turnOwner, null, getMovePosition(game.getPlayerByName(turnOwner).getPosition(), true)));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new MovePickRequest(turnOwner, null, game.getPlayerByName(turnOwner).getPosition(), null, null, null));
        assertEquals(MessageStatus.OK, response.getStatus());

        response = (Response) gameManager.onMessage(
                new MovePickRequest(turnOwner, null, game.getPlayerByName(turnOwner).getPosition(), null, null, null));
        assertEquals(MessageStatus.ERROR, response.getStatus());

        response = (Response) gameManager.onMessage(
                new PassTurnRequest(turnOwner, null));
        assertEquals(MessageStatus.OK, response.getStatus());
    }


    private PlayerPosition getMovePosition(PlayerPosition currentPosition, boolean valid) {
        if (currentPosition.equals(new PlayerPosition(1, 0))) {
            if (valid) {
                return new PlayerPosition(0, 1);
            } else {
                return new PlayerPosition(2, 0);
            }
        } else if (currentPosition.equals(new PlayerPosition(0, 2))) {
            if (valid) {
                return new PlayerPosition(0, 0);
            } else {
                return new PlayerPosition(2, 2);
            }
        } else {
            if (valid) {
                return new PlayerPosition(1, 1);
            } else {
                return new PlayerPosition(0, 0);
            }
        }
    }

    @Test
    void damageCount() {
        gameManager = new GameManager(server, true, 8, 10000);

        GameManager.DamageCountWrapper damageCountWrapper1 = gameManager.new DamageCountWrapper(1, 10);
        GameManager.DamageCountWrapper damageCountWrapper2 = gameManager.new DamageCountWrapper(2, 10);
        GameManager.DamageCountWrapper damageCountWrapper3 = gameManager.new DamageCountWrapper(0, 10);
        GameManager.DamageCountWrapper damageCountWrapper4 = gameManager.new DamageCountWrapper(2, 13);
        GameManager.DamageCountWrapper damageCountWrapper5 = gameManager.new DamageCountWrapper(2, 9);
        GameManager.DamageCountWrapper damageCountWrapper6 = gameManager.new DamageCountWrapper(1, 10);
        GameManager.DamageCountWrapper damageCountWrapper7 = gameManager.new DamageCountWrapper(1, 13);

        assertTrue(damageCountWrapper1.compareTo(damageCountWrapper2) < 0);
        assertTrue(damageCountWrapper1.compareTo(damageCountWrapper3) > 0);
        assertTrue(damageCountWrapper1.compareTo(damageCountWrapper4) > 0);
        assertTrue(damageCountWrapper1.compareTo(damageCountWrapper5) < 0);
        assertEquals(0, damageCountWrapper1.compareTo(damageCountWrapper6));

        assertEquals(damageCountWrapper1, damageCountWrapper1);
        assertEquals(damageCountWrapper6, damageCountWrapper1);
        assertEquals(damageCountWrapper6.hashCode(), damageCountWrapper1.hashCode());
        assertNotEquals(null, damageCountWrapper1);
        assertNotEquals(new PlayerPosition(0, 0), damageCountWrapper1);
        assertNotEquals(damageCountWrapper2, damageCountWrapper1);
        assertNotEquals(damageCountWrapper7, damageCountWrapper1);
    }

    @Test
    void shootParameters() {
        gameManager = new GameManager(server, true, 8, 10000);

        GameManager.ShootParameters shootParameters = gameManager.new ShootParameters(mock(ShootRequest.class), false);
    }
}
