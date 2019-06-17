package view.gui;

import enumerations.MessageStatus;
import enumerations.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import network.message.Response;
import utility.MessageBuilder;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ColorPickSceneController implements Initializable {
    private GuiManager guiManager;

    @FXML
    private Pane mainPane;
    @FXML
    private ImageView backButton;
    @FXML
    private ImageView yellowCard;
    @FXML
    private ImageView blueCard;
    @FXML
    private ImageView greyCard;
    @FXML
    private ImageView purpleCard;
    @FXML
    private ImageView greenCard;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        guiManager = GuiManager.getInstance();
        guiManager.setColorPickSceneController(this);

        sendColorRequest();
        bindEvents();
    }

    private void sendColorRequest() {
        yellowCard.setDisable(true);
        blueCard.setDisable(true);
        greyCard.setDisable(true);
        purpleCard.setDisable(true);
        greenCard.setDisable(true);

        if (!guiManager.sendRequest(MessageBuilder.buildColorRequest(guiManager.getClientToken(), guiManager.getUsername()))) {

            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    GuiManager.SEND_ERROR);

            guiManager.closeConnection();
            onBackButtonClick();
        }
    }

    private void bindEvents() {
        yellowCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.YELLOW));
        blueCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.BLUE));
        greyCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.GREY));
        purpleCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.PURPLE));
        greenCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.GREEN));

        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onBackButtonClick());
    }

    private void onBackButtonClick() {
        guiManager.closeConnection();
        GuiManager.setLayout(mainPane.getScene(), "fxml/connectionScene.fxml");
    }

    private void onCardClick(PlayerColor playerColor) {
        yellowCard.setDisable(true);
        blueCard.setDisable(true);
        greyCard.setDisable(true);
        purpleCard.setDisable(true);
        greenCard.setDisable(true);

        if (!guiManager.sendRequest(MessageBuilder.buildGetInLobbyMessage(guiManager.getClientToken(),
                guiManager.getUsername(), playerColor, false))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    GuiManager.SEND_ERROR);

            onBackButtonClick();
        }
    }

    void onColorResponse(List<PlayerColor> availableColors) {
        if (availableColors.isEmpty()) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    "The game is full!");

            onBackButtonClick();

        }

        yellowCard.setDisable(!availableColors.contains(PlayerColor.YELLOW));
        blueCard.setDisable(!availableColors.contains(PlayerColor.BLUE));
        greyCard.setDisable(!availableColors.contains(PlayerColor.GREY));
        purpleCard.setDisable(!availableColors.contains(PlayerColor.PURPLE));
        greenCard.setDisable(!availableColors.contains(PlayerColor.GREEN));
    }

    void onLobbyJoinResponse(Response response) {
        if (response.getStatus() == MessageStatus.ERROR) {

            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    response.getMessage());

            onBackButtonClick();

        } else {
            GuiManager.setLayout(mainPane.getScene(), "fxml/lobbyScene.fxml");
        }
    }
}