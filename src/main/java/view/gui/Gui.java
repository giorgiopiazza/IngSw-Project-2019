package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

        setMenuLayout();

        window.show();
    }

    private void setMenuLayout() {
        FXMLLoader loader = new FXMLLoader(Gui.class.getClassLoader().getResource("fxml/menuScene.fxml"));

        try {
            Scene scene = new Scene(loader.load());

            window.setScene(scene);
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
            window.getScene().setRoot(pane);

        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }
}
