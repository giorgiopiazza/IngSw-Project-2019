package view.gui;

import enumerations.PlayerColor;
import javafx.scene.image.ImageView;

/**
 * This class represents a row of the table of EndGameSceneController
 */
public class TableRow {
    private String rank;
    private ImageView icon;
    private String username;
    private String points;

    TableRow(String rank, PlayerColor color, String username, String points) {
        this.rank = rank;
        this.icon = new ImageView("/img/players/" + color.toString().toLowerCase() + "Icon.png");
        this.icon.setFitHeight(68);
        this.icon.setFitWidth(65);
        this.username = username;
        this.points = points;
    }

    public String getRank() {
        return rank;
    }

    public ImageView getIcon() {
        return icon;
    }

    public String getUsername() {
        return username;
    }

    public String getPoints() {
        return points;
    }
}