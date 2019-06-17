package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuSceneController implements Initializable {
    @FXML
    private Pane mainPane;
    @FXML
    private ImageView startButton;
    @FXML
    private ImageView exitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindEvents();
    }

    private void bindEvents() {
        startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onStartButtonClick());
        exitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.exit(0));
    }

    private void onStartButtonClick() {
        GuiManager.setLayout(mainPane.getScene(), "fxml/connectionScene.fxml");
    }
}
