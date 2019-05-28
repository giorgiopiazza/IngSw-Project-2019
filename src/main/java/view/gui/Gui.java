package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class Gui extends Application {
    public void start() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Stage window = stage;
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        FXMLLoader loader = new FXMLLoader(Gui.class.getClassLoader().getResource("fxml/menuScene.fxml"));
        Scene scene = new Scene(loader.load());

        window.setScene(scene);
        window.show();
    }
}
