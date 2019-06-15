package view.gui;

import enumerations.PlayerColor;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
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
import java.util.List;
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

        // setMainMenuLayout();
        setBoardLayout();

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
                !usernameField.getText().equalsIgnoreCase(Game.BOT) &&
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
            backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> closeConnection());

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

            ImageView backButton = (ImageView) scene.lookup("#backButton");
            backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> closeConnection());
        }
    }

    private void setBoardLayout() {
        if (setLayout("fxml/game.fxml")) {
            setGameMap();
            bindWeaponZoom();
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

    private void closeConnection() {
        // TODO Close Connection
        setConnectionLayout();
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
        dialog.setAlwaysOnTop(true);

        dialogScene.lookup("#okButton").addEventHandler(MouseEvent.MOUSE_CLICKED, event -> dialog.close());

        ((Label) dialogScene.lookup("#dialogTitle")).setText(title);
        ((Label) dialogScene.lookup("#dialogText")).setText(text);

        dialog.showAndWait();
    }

    private void setGameMap() {
        Scene scene = window.getScene();

        ImageView map = (ImageView) scene.lookup("#map");
        map.setImage(new Image("/img/maps/map1.png"));
    }

    private void bindWeaponZoom() {
        Scene scene = window.getScene();
        for (String color : List.of("blue", "yellow", "red")) {
            for (int i = 0; i < 3; ++i) {
                ImageView weapon = (ImageView) scene.lookup("#" + color + "Weapon" + i);
                weapon.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showWeaponZoom);
            }
        }

        FlowPane weaponZoomPane = (FlowPane) scene.lookup("#weaponZoom");
        weaponZoomPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> hideWeaponZoom());
    }

    private void showWeaponZoom(Event event) {
        Scene scene = window.getScene();

        ImageView weaponTarget = (ImageView) event.getTarget();

        if (weaponTarget != null) {
            setBoardOpaque(0.3);

            FlowPane flowPane = (FlowPane) scene.lookup("#weaponZoom");
            flowPane.setVisible(true);

            ImageView imageView = (ImageView) scene.lookup("#weaponZoomImage");
            imageView.setImage(weaponTarget.getImage());
        }
    }

    private void hideWeaponZoom() {
        Scene scene = window.getScene();

        FlowPane flowPane = (FlowPane) scene.lookup("#weaponZoom");
        flowPane.setVisible(false);

        ImageView imageView = (ImageView) scene.lookup("#weaponZoomImage");
        imageView.setImage(null);

        setBoardOpaque(1);
    }

    private void setBoardOpaque(double value) {
        Scene scene = window.getScene();
        ArrayList<String> elements = new ArrayList<>(
                List.of("map", "powerupDeck", "weaponDeck", "blueWeapon0", "blueWeapon1",
                        "blueWeapon2", "redWeapon0", "redWeapon1", "redWeapon2",
                        "yellowWeapon0", "yellowWeapon1", "yellowWeapon2"));

        for (String element : elements) {
            ImageView imageView = (ImageView) scene.lookup("#" + element);

            if (imageView != null) {
                imageView.opacityProperty().setValue(value);
            }
        }
    }
}
