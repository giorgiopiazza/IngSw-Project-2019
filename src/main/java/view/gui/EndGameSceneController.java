package view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.player.PlayerPoints;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EndGameSceneController {
    @FXML
    ImageView exitButton;
    @FXML
    TableView<TableRow> table;
    @FXML
    TableColumn<TableRow, String> rankColumn;
    @FXML
    TableColumn<TableRow, ImageView> iconColumn;
    @FXML
    TableColumn<TableRow, String> playerColumn;
    @FXML
    TableColumn<TableRow, String> pointsColumn;

    @FXML
    public void initialize() {
        bindEvents();
        tableSetup();
    }

    private void bindEvents() {
        exitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.exit(0));
    }

    private void tableSetup() {
        rankColumn.setReorderable(false);
        rankColumn.setResizable(false);
        rankColumn.setSortable(false);
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));

        iconColumn.setReorderable(false);
        iconColumn.setResizable(false);
        iconColumn.setSortable(false);
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));

        playerColumn.setReorderable(false);
        playerColumn.setResizable(false);
        playerColumn.setSortable(false);
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        pointsColumn.setReorderable(false);
        pointsColumn.setResizable(false);
        pointsColumn.setSortable(false);
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

        table.setPlaceholder(new Label());
    }

    void setData(List<PlayerPoints> players) {
        List<PlayerPoints> winners = players.stream().filter(PlayerPoints::isWinner).collect(Collectors.toList());

        ArrayList<TableRow> tableRows = new ArrayList<>();

        tableRows.add(new TableRow("1", winners.get(0).getPlayerColor(), winners.get(0).getUserName(), Integer.toString(winners.get(0).getPoints())));

        if (winners.size() > 1) {
            for (PlayerPoints playerPoints : winners.subList(1, winners.size())) {
                tableRows.add(new TableRow("", playerPoints.getPlayerColor(), playerPoints.getUserName(), Integer.toString(playerPoints.getPoints())));
            }
        }

        players.removeAll(winners);

        players = players.stream()
                .sorted(Comparator.comparingInt(PlayerPoints::getPoints).reversed())
                .collect(Collectors.toList());

        int count = 2;

        for (PlayerPoints playerPoints : players) {
            tableRows.add(new TableRow(Integer.toString(count), playerPoints.getPlayerColor(), playerPoints.getUserName(), Integer.toString(playerPoints.getPoints())));
            count++;
        }

        table.getItems().addAll(tableRows);
    }
}
