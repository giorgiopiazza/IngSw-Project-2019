package view.gui;

import enumerations.MessageStatus;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Game;
import network.client.Client;
import network.message.ConnectionResponse;
import utility.GameCostants;
import utility.ServerAddressValidator;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionSceneController implements Initializable {
    private final PseudoClass errorPseudo = PseudoClass.getPseudoClass("error");
    private GuiManager guiManager;

    @FXML
    private Pane mainPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField portField;
    @FXML
    private ImageView connectSocketButton;
    @FXML
    private ImageView connectRmiButton;
    @FXML
    private ImageView backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        guiManager = GuiManager.getInstance();
        guiManager.setConnectionSceneController(this);

        bindEvents();
        setInputFormat();
    }

    private void bindEvents() {
        connectSocketButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onConnectionButtonClick(0));
        connectRmiButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onConnectionButtonClick(1));
        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onBackButtonClick());
    }

    private void setInputFormat() {
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > Client.MAX_USERNAME_LENGTH) {
                usernameField.setText(newValue.substring(0, Client.MAX_USERNAME_LENGTH));
            }
        });

        addressField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > ServerAddressValidator.MAX_ADDRESS_LENGTH) {
                addressField.setText(newValue.substring(0, ServerAddressValidator.MAX_ADDRESS_LENGTH));
            }
        });

        portField.setTextFormatter(new TextFormatter<String>(change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }

            return null;
        }));

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > ServerAddressValidator.MAX_PORT_LENGTH) {
                portField.setText(newValue.substring(0, ServerAddressValidator.MAX_PORT_LENGTH));
            }
        });
    }

    private void onBackButtonClick() {
        GuiManager.setLayout(mainPane.getScene(), "fxml/menuScene.fxml");
    }

    private void onConnectionButtonClick(int connection) {
        final String username = usernameField.getText();
        final String address = addressField.getText();
        final String port = portField.getText();

        boolean isUsernameValid = !username.equals("") &&
                GameCostants.FORBIDDEN_USERNAME.stream().noneMatch(u -> u.equalsIgnoreCase(username));

        boolean isAddressValid = ServerAddressValidator.isAddressValid(address);

        boolean isPortValid = ServerAddressValidator.isPortValid(portField.getText());

        usernameField.pseudoClassStateChanged(errorPseudo, !isUsernameValid);
        addressField.pseudoClassStateChanged(errorPseudo, !isAddressValid);
        portField.pseudoClassStateChanged(errorPseudo, !isPortValid);

        if (isUsernameValid && isAddressValid && isPortValid) {
            backButton.setDisable(true);
            connectSocketButton.setDisable(true);
            connectRmiButton.setDisable(true);

            try {
                GuiManager.getInstance().createConnection(connection, username, address, Integer.parseInt(port));
            } catch (Exception e) {
                GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                        "Failed to establish a connection!");

                onBackButtonClick();
            }
        }
    }

    void onConnectionResponse(ConnectionResponse response) {
        if (response.getStatus() == MessageStatus.OK) {
            GuiManager.setLayout(mainPane.getScene(), "fxml/colorPickScene.fxml");
        } else {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, response.getMessage());

            guiManager.closeConnection();
            onBackButtonClick();
        }
    }
}
