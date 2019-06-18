package view.gui;

import controller.ClientGameManager;
import enumerations.PlayerColor;
import enumerations.PossibleAction;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.GameSerialized;
import model.player.Player;
import network.message.ConnectionResponse;
import network.message.GameVoteResponse;
import network.message.Response;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class GuiManager extends ClientGameManager {
    private static GuiManager instance = null;

    private ConnectionSceneController connectionSceneController;
    private ColorPickSceneController colorPickSceneController;
    private LobbySceneController lobbySceneController;
    private GameSceneController gameSceneController;

    private GuiManager() {
        super();
    }

    public static GuiManager getInstance() {
        if (instance == null)
            instance = new GuiManager();
        return instance;
    }

    void setConnectionSceneController(ConnectionSceneController connectionSceneController) {
        this.connectionSceneController = connectionSceneController;
    }

    void setColorPickSceneController(ColorPickSceneController colorPickSceneController) {
        this.colorPickSceneController = colorPickSceneController;
    }

    void setLobbySceneController(LobbySceneController lobbySceneController) {
        this.lobbySceneController = lobbySceneController;
    }

    void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
    }

    static <T> T setLayout(Scene scene, String path) {
        FXMLLoader loader = new FXMLLoader(GuiManager.class.getClassLoader().getResource(path));

        Pane pane;
        try {
            pane = loader.load();
            scene.setRoot(pane);
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
            return null;
        }

        return loader.getController();
    }

    static void showDialog(Stage window, String title, String text) {
        FXMLLoader loader = new FXMLLoader(GuiManager.class.getClassLoader().getResource("fxml/dialogScene.fxml"));

        Scene dialogScene;
        try {
            dialogScene = new Scene(loader.load(), 600, 300);
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
            return;
        }

        Stage dialog = new Stage();
        dialog.setScene(dialogScene);
        dialog.initOwner(window);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setAlwaysOnTop(true);

        dialogScene.lookup("#okButton").addEventHandler(MouseEvent.MOUSE_CLICKED, event -> dialog.close());

        ((Label) dialogScene.lookup("#dialogTitle")).setText(title);
        ((Label) dialogScene.lookup("#dialogText")).setText(text);

        dialog.showAndWait();
    }

    @Override
    public void connectionResponse(ConnectionResponse response) {
        Platform.runLater(() ->
                connectionSceneController.onConnectionResponse(response));
    }

    @Override
    public void askColor(List<PlayerColor> availableColors) {
        Platform.runLater(() ->
                colorPickSceneController.onColorResponse(availableColors));
    }

    @Override
    public void lobbyJoinResponse(Response response) {
        Platform.runLater(() ->
                colorPickSceneController.onLobbyJoinResponse(response));
    }

    @Override
    public void voteResponse(GameVoteResponse gameVoteResponse) {
        Platform.runLater(() ->
                lobbySceneController.onVoteResponse(gameVoteResponse));
    }

    @Override
    public void firstPlayerCommunication(String username) {
        // Not needed with GUI
    }

    @Override
    public void notYourTurn() {

    }

    @Override
    public void gameStateUpdate(GameSerialized gameSerialized) {
        if (gameSceneController == null) { // Game Start
            Platform.runLater(() ->
                    lobbySceneController.onGameStart(gameSerialized));
        } else {
            Platform.runLater(() ->
                    gameSceneController.onStateUpdate(gameSerialized));
        }
    }

    @Override
    public void responseError(String error) {

    }

    @Override
    public PossibleAction askAction() {
        return null;
    }

    @Override
    public void botSpawn() {

    }

    @Override
    public void botRespawn() {

    }

    @Override
    public void spawn() {

    }

    @Override
    public void move() {

    }

    @Override
    public void moveAndPick() {

    }

    @Override
    public void shoot() {

    }

    @Override
    public void adrenalinePick() {

    }

    @Override
    public void adrenalineShoot() {

    }

    @Override
    public void frenzyMove() {

    }

    @Override
    public void frenzyPick() {

    }

    @Override
    public void frenzyShoot() {

    }

    @Override
    public void lightFrenzyPick() {

    }

    @Override
    public void lightFrenzyShoot() {

    }

    @Override
    public void botAction() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void powerup() {

    }

    @Override
    public void grenadeUsage() {

    }

    @Override
    public void passTurn() {

    }

    @Override
    public void onPlayerDisconnect(String username) {

    }

    @Override
    public void notifyGameEnd(List<Player> winners) {

    }

    @Override
    public void askScope() {

    }
}
