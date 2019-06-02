package view.gui;

import enumerations.PlayerColor;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Game;
import network.client.Client;
import utility.ServerAddressValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Gui extends Application {
    private final PseudoClass errorPseudo = PseudoClass.getPseudoClass("error");

    private Stage window;

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setMaximized(true);
        window.setFullScreen(true);
        window.setFullScreenExitHint("");
        window.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        InputStream is = Gui.class.getClassLoader().getResourceAsStream("img/icon.png");
        if (is != null) {
            window.getIcons().add(new Image(is));
        }

        window.setScene(new Scene(new Pane()));

        setMainMenuLayout();
        //setLobbyLayout();

        window.show();
    }

    private void setMainMenuLayout() {
        if (setLayout("fxml/menuScene.fxml")) {
            Scene scene = window.getScene();

            ImageView exitButton = (ImageView) scene.lookup("#exitButton");
            exitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.exit(0));

            ImageView startButton = (ImageView) scene.lookup("#startButton");
            startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> setConnectionLayout());
        }

    }

    private void setConnectionLayout() {
        if (setLayout("fxml/connectionScene.fxml")) {
            Scene scene = window.getScene();

            ImageView backButton = (ImageView) scene.lookup("#backButton");
            backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> setMainMenuLayout());

            ImageView connectSocketButton = (ImageView) scene.lookup("#connectSocketButton");
            connectSocketButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryStartConnection(0));

            ImageView connectRmiButton = (ImageView) scene.lookup("#connectRmiButton");
            connectRmiButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryStartConnection(1));

            TextField usernameField = (TextField) scene.lookup("#usernameField");
            usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > Client.MAX_USERNAME_LENGTH) {
                    usernameField.setText(newValue.substring(0, Client.MAX_USERNAME_LENGTH));
                }
            });

            TextField addressField = (TextField) scene.lookup("#addressField");
            addressField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > ServerAddressValidator.MAX_ADDRESS_LENGTH) {
                    addressField.setText(newValue.substring(0, ServerAddressValidator.MAX_ADDRESS_LENGTH));
                }
            });

            TextField portField = (TextField) scene.lookup("#portField");
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
    }

    private void tryStartConnection(int connectionType) {
        Scene scene = window.getScene();

        TextField usernameField = (TextField) scene.lookup("#usernameField");
        TextField addressField = (TextField) scene.lookup("#addressField");
        TextField portField = (TextField) scene.lookup("#portField");

        boolean isUsernameValid = !usernameField.getText().equalsIgnoreCase(Game.GOD) &&
                !usernameField.getText().equalsIgnoreCase(Game.TERMINATOR_USERNAME) &&
                !usernameField.getText().equals("");

        boolean isAddressValid = ServerAddressValidator.isAddressValid(addressField.getText());

        boolean isPortValid = ServerAddressValidator.isPortValid(portField.getText());

        usernameField.pseudoClassStateChanged(errorPseudo, !isUsernameValid);
        addressField.pseudoClassStateChanged(errorPseudo, !isAddressValid);
        portField.pseudoClassStateChanged(errorPseudo, !isPortValid);

        if (isUsernameValid && isAddressValid && isPortValid) {
            ImageView backButton = (ImageView) scene.lookup("#backButton");
            ImageView connectSocketButton = (ImageView) scene.lookup("#connectSocketButton");
            ImageView connectRmiButton = (ImageView) scene.lookup("#connectRmiButton");

            backButton.setDisable(true);
            connectSocketButton.setDisable(true);
            connectRmiButton.setDisable(true);

            if (connectionType == 0) {
                // TODO SocketConnection
            } else {
                // TODO RmiConnection
            }

            ArrayList<PlayerColor> colorList = new ArrayList<>();
            colorList.add(PlayerColor.BLUE);
            colorList.add(PlayerColor.PURPLE);
            colorList.add(PlayerColor.YELLOW);

            setColorPickLayout(colorList);
        }
    }

    private void setColorPickLayout(ArrayList<PlayerColor> availableColors) {
        if (setLayout("fxml/colorPickScene.fxml")) {
            Scene scene = window.getScene();

            ImageView backButton = (ImageView) scene.lookup("#backButton");
            backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> setConnectionLayout());

            ImageView yellowCard = (ImageView) scene.lookup("#yellowCard");
            yellowCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryJoinLobby(PlayerColor.YELLOW));

            ImageView blueCard = (ImageView) scene.lookup("#blueCard");
            blueCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryJoinLobby(PlayerColor.BLUE));

            ImageView greyCard = (ImageView) scene.lookup("#greyCard");
            greyCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryJoinLobby(PlayerColor.GREY));

            ImageView purpleCard = (ImageView) scene.lookup("#purpleCard");
            purpleCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryJoinLobby(PlayerColor.PURPLE));

            ImageView greenCard = (ImageView) scene.lookup("#greenCard");
            greenCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryJoinLobby(PlayerColor.GREEN));

            if (!availableColors.contains(PlayerColor.YELLOW)) {
                yellowCard.setDisable(true);
            }

            if (!availableColors.contains(PlayerColor.BLUE)) {
                blueCard.setDisable(true);
            }

            if (!availableColors.contains(PlayerColor.GREY)) {
                greyCard.setDisable(true);
            }

            if (!availableColors.contains(PlayerColor.PURPLE)) {
                purpleCard.setDisable(true);
            }

            if (!availableColors.contains(PlayerColor.GREEN)) {
                greenCard.setDisable(true);
            }


        }
    }

    private void tryJoinLobby(PlayerColor playerColor) {
        Scene scene = window.getScene();

        scene.lookup("#yellowCard").setDisable(true);
        scene.lookup("#blueCard").setDisable(true);
        scene.lookup("#greyCard").setDisable(true);
        scene.lookup("#purpleCard").setDisable(true);
        scene.lookup("#greenCard").setDisable(true);

        // TODO Join Lobby Request

        setLobbyLayout();
    }

    private void setLobbyLayout() {
        if (setLayout("fxml/lobbyScene.fxml")) {
            Scene scene = window.getScene();
        }
    }

    private boolean setLayout(String path) {
        FXMLLoader loader = new FXMLLoader(Gui.class.getClassLoader().getResource(path));

        try {
            Pane pane = loader.load();
            window.getScene().setRoot(pane);
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
            return false;
        }
        return true;
    }

    private void showDialog(String title, String text) {
        FXMLLoader loader = new FXMLLoader(Gui.class.getClassLoader().getResource("fxml/dialogScene.fxml"));

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
        dialog.setResizable(false);

        dialogScene.lookup("#okButton").addEventHandler(MouseEvent.MOUSE_CLICKED, event -> dialog.close());

        ((Label)dialogScene.lookup("#dialogTitle")).setText(title);
        ((Label)dialogScene.lookup("#dialogText")).setText(text);

        dialog.showAndWait();
    }
}
