package controller;

import enumerations.*;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import network.message.*;
import network.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
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
        game.initializeDecks();
        game.setGameMap(1);
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
/*
        response = (Response) gameManager.onMessage(
                new LobbyMessage("test2", null, PlayerColor.YELLOW, false));
        assertEquals(MessageStatus.ERROR, response.getStatus());*/

        assertEquals(PossibleGameState.GAME_STARTED, gameManager.getGameState());

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

        response = (Response) gameManager.onConnectionMessage(new LobbyMessage("tose", null, null, true));
        assertEquals(MessageStatus.OK, response.getStatus());

        assertEquals(PossibleGameState.GAME_ENDED, gameManager.getGameState());
    }

    @Test
    void game() throws Exception {
        gameManager = new GameManager(server, true, 8, 10000);

        Response response = (Response) gameManager.onMessage(
                new LobbyMessage("tose", null, PlayerColor.GREY, false));
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

        String turnOwner = gameManager.getRoundManager().getTurnManager().getTurnOwner().getUsername();

        assertEquals(UserPlayerState.BOT_SPAWN, gameManager.getUserPlayerState(turnOwner));

        response = (Response) gameManager.onMessage(
                new BotSpawnRequest(turnOwner, null, RoomColor.BLUE));
        assertEquals(MessageStatus.OK, response.getStatus());

        assertEquals(UserPlayerState.SPAWN, gameManager.getUserPlayerState(turnOwner));

        response = (Response) gameManager.onMessage(
                new DiscardPowerupRequest(turnOwner, null, 0));

        assertEquals(UserPlayerState.FIRST_ACTION, gameManager.getUserPlayerState(turnOwner));
    }
}
