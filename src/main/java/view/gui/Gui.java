package view.gui;

import enumerations.PlayerColor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.player.PlayerPoints;
import network.message.WinnersResponse;

import java.io.InputStream;
import java.util.ArrayList;

public class Gui extends Application {
    @Override
    public void start(Stage stage) {
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        InputStream is = Gui.class.getClassLoader().getResourceAsStream("img/icon.png");
        if (is != null) {
            stage.getIcons().add(new Image(is));
        }

        stage.setScene(new Scene(new Pane()));

        //GuiManager.setLayout(stage.getScene(), "fxml/menuScene.fxml");
        EndGameSceneController endGameSceneController = GuiManager.setLayout(stage.getScene(), "fxml/endGameScene.fxml");

        ArrayList<PlayerPoints> playerPoints = new ArrayList<>();

        PlayerPoints p1 = new PlayerPoints("Pippo", PlayerColor.YELLOW, 60);
        p1.setWinner();
        playerPoints.add(p1);
        playerPoints.add(new PlayerPoints("Paperino", PlayerColor.GREEN, 24));
        playerPoints.add(new PlayerPoints("Topolino", PlayerColor.BLUE, 56));
        playerPoints.add(new PlayerPoints("Pluto", PlayerColor.PURPLE, 32));
        PlayerPoints p2 = new PlayerPoints("Tose", PlayerColor.GREY, 60);
        p2.setWinner();
        playerPoints.add(p2);

        endGameSceneController.setData(new WinnersResponse(playerPoints));
        stage.show();
    }

    @Override
    public void stop() {
        GuiManager.getInstance().closeConnection();
    }
}
