package view.gui;

import enumerations.MessageStatus;
import enumerations.PlayerColor;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import network.message.Response;
import utility.MessageBuilder;

import java.util.List;

/**
 * Controller that handles the color pick
 */
public class ColorPickSceneController {
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

    @FXML
    public void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setColorPickSceneController(this);

        sendColorRequest();
        bindEvents();
    }

    /**
     * Sends to the server the remaining colors available for picking
     */
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

    /**
     * Binds on click events
     */
    private void bindEvents() {
        yellowCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.YELLOW));
        blueCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.BLUE));
        greyCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.GREY));
        purpleCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.PURPLE));
        greenCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCardClick(PlayerColor.GREEN));

        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onBackButtonClick());
    }

    /**
     * Handles back button click
     */
    private void onBackButtonClick() {
        guiManager.closeConnection();
        GuiManager.setLayout(mainPane.getScene(), "fxml/connectionScene.fxml");
    }

    /**
     * Handles color card click
     *
     * @param playerColor color of the card
     */
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

    /**
     * Handles the color request response
     * @param availableColors list of available colors
     */
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

    /**
     * Handles the lobby join response
     * @param response response of the join request
     */
    void onLobbyJoinResponse(Response response) {
        if (response.getStatus() == MessageStatus.ERROR) {

            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    response.getMessage());

            onBackButtonClick();

        } else {
            LobbySceneController lobbySceneController = GuiManager.setLayout(mainPane.getScene(), "fxml/lobbyScene.fxml");

            if (lobbySceneController != null) {
                lobbySceneController.updateLobbyList();
            }
        }
    }

    /**
     * Handles the server disconnection
     */
    void onDisconnection() {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Disconnection", "You were disconnected from the server");
    }
}