package view.gui;

import enumerations.MessageStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.message.GameVoteResponse;
import utility.MessageBuilder;

import java.util.List;

public class LobbySceneController {
    private GuiManager guiManager;

    @FXML
    private Pane mainPane;
    @FXML
    private ImageView map1;
    @FXML
    private ImageView map2;
    @FXML
    private ImageView map3;
    @FXML
    private ImageView map4;
    @FXML
    private ImageView backButton;
    @FXML
    private VBox lobbyLabelsBox;

    @FXML
    public void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setLobbySceneController(this);

        bindEvents();
    }

    private void bindEvents() {
        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onBackButtonClick());

        map1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onMapButtonClick(1));
        map2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onMapButtonClick(2));
        map3.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onMapButtonClick(3));
        map4.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onMapButtonClick(4));
    }

    private void onMapButtonClick(int mapVote) {
        map1.setDisable(true);
        map2.setDisable(true);
        map3.setDisable(true);
        map4.setDisable(true);

        if (!guiManager.sendRequest(MessageBuilder.buildVoteMessage(guiManager.getClientToken(),
                guiManager.getUsername(), mapVote))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    GuiManager.SEND_ERROR);

            onBackButtonClick();
        }
    }

    private void onBackButtonClick() {
        guiManager.closeConnection();
        GuiManager.setLayout(mainPane.getScene(), "fxml/connectionScene.fxml");
    }

    void onVoteResponse(GameVoteResponse gameVoteResponse) {
        if (gameVoteResponse.getStatus() == MessageStatus.OK) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Info",
                    "Vote Accepted");
        } else {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    gameVoteResponse.getMessage());

            map1.setDisable(false);
            map2.setDisable(false);
            map3.setDisable(false);
            map4.setDisable(false);
        }
    }

    void onGameStart() {
        GameSceneController gameSceneController =
                GuiManager.setLayout(mainPane.getScene(), "fxml/gameScene.fxml");

        if (gameSceneController != null) {
            gameSceneController.setupGame(guiManager.getGameSerialized());
        }
    }

    void updateLobbyList(List<String> users) {
        ObservableList<Node> childrens = lobbyLabelsBox.getChildren();
        childrens.clear();

        for (String user : users) {
            Label lbl = new Label();
            lbl.setText(user);
            lbl.getStyleClass().add("playerRow");

            childrens.add(lbl);
        }
    }

    void onError(String error) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, error);
    }
}
