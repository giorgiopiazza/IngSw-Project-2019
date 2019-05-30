package view.gui;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Logger;

public class Gui extends Application {
    private Stage window;

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setMaximized(true);
        window.setFullScreen(true);
        window.setFullScreenExitHint("");
        window.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        window.setScene(new Scene(new Pane()));

        setMainMenuLayout();

        window.show();
    }

    private void setMainMenuLayout() {
        FXMLLoader loader = new FXMLLoader(Gui.class.getClassLoader().getResource("fxml/menuScene.fxml"));

        try {
            Pane pane = loader.load();
            Scene scene = window.getScene();
            scene.setRoot(pane);

            ImageView exitButton = (ImageView) scene.lookup("#exitButton");
            exitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.exit(0));

            ImageView startButton = (ImageView) scene.lookup("#startButton");
            startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> setConnectionLayout());
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }

    private void setConnectionLayout() {
        FXMLLoader loader = new FXMLLoader(Gui.class.getClassLoader().getResource("fxml/connectionScene.fxml"));

        try {
            Pane pane = loader.load();
            Scene scene = window.getScene();
            scene.setRoot(pane);

            ImageView backButton = (ImageView) scene.lookup("#backButton");
            backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> setMainMenuLayout());

            TextField portField = (TextField) scene.lookup("#portField");
            portField.setTextFormatter(new TextFormatter<String>(change -> {
                String input = change.getText();
                if (input.matches("[0-9]*")) {
                    return change;
                }

                return null;
            }));

            //usernameField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), true);
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }
}
