package view.gui;

import enumerations.Ammo;
import enumerations.PlayerColor;
import enumerations.RoomColor;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.GameSerialized;
import model.cards.WeaponCard;
import model.map.GameMap;
import model.map.SpawnSquare;
import model.player.UserPlayer;

import java.net.URL;
import java.util.*;

public class GameSceneController implements Initializable {
    private GuiManager guiManager;

    private List<ImageView> weaponSlotList;
    private Map<String, Ammo> weaponColor;

    @FXML
    Pane mainPane;
    @FXML
    ImageView map;
    @FXML
    ImageView powerupDeck;
    @FXML
    ImageView weaponDeck;
    @FXML
    ImageView blueWeapon0;
    @FXML
    ImageView blueWeapon1;
    @FXML
    ImageView blueWeapon2;
    @FXML
    ImageView redWeapon0;
    @FXML
    ImageView redWeapon1;
    @FXML
    ImageView redWeapon2;
    @FXML
    ImageView yellowWeapon0;
    @FXML
    ImageView yellowWeapon1;
    @FXML
    ImageView yellowWeapon2;
    @FXML
    VBox iconList;
    @FXML
    FlowPane weaponZoom;
    @FXML
    ImageView weaponZoomImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        guiManager = GuiManager.getInstance();
        guiManager.setGameSceneController(this);

        weaponSlotList = List.of(blueWeapon0, blueWeapon1, blueWeapon2, redWeapon0, redWeapon1, redWeapon2,
                yellowWeapon0, yellowWeapon1, yellowWeapon2);
    }

    void setupGame(GameSerialized gameSerialized) {
        GameMap gameMap = gameSerialized.getGameMap();

        map.setImage(new Image(gameMap.getImagePath()));

        setWeaponCards(gameMap);
        setPlayerIcons(gameSerialized);

        setTurnOwnerIcon();

        bindWeaponZoom();
    }

    private void setWeaponCards(GameMap gameMap) {
        List<WeaponCard> weaponCards;
        weaponColor = new HashMap<>();

        SpawnSquare spawnSquare =
                (SpawnSquare) gameMap.getSquare(gameMap.getSpawnSquare(RoomColor.BLUE));
        weaponCards = new ArrayList<>(Arrays.asList(spawnSquare.getWeapons()));

        spawnSquare =
                (SpawnSquare) gameMap.getSquare(gameMap.getSpawnSquare(RoomColor.RED));
        weaponCards.addAll(Arrays.asList(spawnSquare.getWeapons()));

        spawnSquare =
                (SpawnSquare) gameMap.getSquare(gameMap.getSpawnSquare(RoomColor.YELLOW));
        weaponCards.addAll(Arrays.asList(spawnSquare.getWeapons()));

        for (int i = 0; i < weaponSlotList.size(); ++i) {
            Image image = new Image(weaponCards.get(i).getImagePath());

            weaponSlotList.get(i).setImage(image);
            weaponColor.put(image.getUrl(), weaponCards.get(i).getCost()[0]);
        }
    }

    private void setPlayerIcons(GameSerialized gameSerialized) {
        ImageView imageView;

        if (gameSerialized.isBotPresent()) {
            imageView = new ImageView();
            imageView.setId(getIconIDFromColor(gameSerialized.getBot().getColor()));
            imageView.getProperties().put("username", "bot");

            iconList.getChildren().add(imageView);
        }

        for (int i = gameSerialized.getPlayers().size() - 1; i >= 0; --i) {
            UserPlayer player = gameSerialized.getPlayers().get(i);

            imageView = new ImageView();
            imageView.setId(getIconIDFromColor(player.getColor()));
            imageView.getProperties().put("username", player.getUsername());

            iconList.getChildren().add(imageView);
        }

    }

    private String getIconIDFromColor(PlayerColor playerColor) {
        switch (playerColor) {
            case BLUE:
                return "blueIcon";
            case YELLOW:
                return "yellowIcon";
            case GREEN:
                return "greenIcon";
            case GREY:
                return "greyIcon";
            case PURPLE:
                return "purpleIcon";
        }
        return null;
    }

    private void bindWeaponZoom() {
        for (ImageView weaponSlot : weaponSlotList) {
            weaponSlot.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showWeaponZoom);
        }

        weaponZoom.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> hideWeaponZoom());
    }

    private void setTurnOwnerIcon() {
        for (Node children : iconList.getChildren()) {
            children.getStyleClass().clear();

            if (children.getProperties().get("username").equals(GuiManager.getInstance().getTurnOwner())) {
                children.getStyleClass().add("turnOwner");
            } else {
                children.getStyleClass().add("notTurnOwner");
            }
        }
    }

    void onStateUpdate(GameSerialized gameSerialized) {
        setTurnOwnerIcon();
    }

    private void showWeaponZoom(Event event) {
        ImageView weaponTarget = (ImageView) event.getTarget();

        if (weaponTarget != null) {
            setBoardOpaque(0.3);

            weaponZoom.setVisible(true);
            weaponZoomImage.setImage(weaponTarget.getImage());

            Ammo color = weaponColor.get(weaponTarget.getImage().getUrl());
            if (color != null) {
                String className = null;

                switch (color) {
                    case BLUE:
                        className = "weaponZoomImageBlue";
                        break;
                    case RED:
                        className = "weaponZoomImageRed";
                        break;
                    case YELLOW:
                        className = "weaponZoomImageYellow";
                        break;
                }

                weaponZoomImage.getStyleClass().add(className);
            }
        }
    }

    private void hideWeaponZoom() {
        weaponZoom.setVisible(false);
        weaponZoomImage.setImage(null);
        weaponZoomImage.getStyleClass().clear();

        setBoardOpaque(1);
    }

    private void setBoardOpaque(double value) {
        map.opacityProperty().setValue(value);
        powerupDeck.opacityProperty().setValue(value);
        weaponDeck.opacityProperty().setValue(value);
        blueWeapon0.opacityProperty().setValue(value);
        blueWeapon1.opacityProperty().setValue(value);
        blueWeapon2.opacityProperty().setValue(value);
        redWeapon0.opacityProperty().setValue(value);
        redWeapon1.opacityProperty().setValue(value);
        redWeapon2.opacityProperty().setValue(value);
        yellowWeapon0.opacityProperty().setValue(value);
        yellowWeapon1.opacityProperty().setValue(value);
        yellowWeapon2.opacityProperty().setValue(value);
    }
}
