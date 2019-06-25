package view.gui;

import enumerations.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.GameSerialized;
import model.cards.WeaponCard;
import model.map.CardSquare;
import model.map.GameMap;
import model.map.SpawnSquare;
import model.map.Square;
import model.player.*;
import utility.GameCostants;

import java.util.*;

public class GameSceneController {
    private static final String USERNAME_PROPERTY = "username";
    private GuiManager guiManager;

    private List<ImageView> weaponSlotList;
    private List<ImageView> ammoTiles;
    private List<ImageView> playerFigures;
    private Map<String, Ammo> weaponColor;

    @FXML
    Pane mainPane;
    @FXML
    StackPane boardArea;
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
    VBox actionList;
    @FXML
    FlowPane zoomPanel;
    @FXML
    BorderPane infoPanel;

    @FXML
    private void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setGameSceneController(this);

        ammoTiles = new ArrayList<>();
        playerFigures = new ArrayList<>();
        weaponSlotList = List.of(blueWeapon0, blueWeapon1, blueWeapon2, redWeapon0, redWeapon1, redWeapon2,
                yellowWeapon0, yellowWeapon1, yellowWeapon2);
    }


    void setupGame(GameSerialized gameSerialized) {
        GameMap gameMap = gameSerialized.getGameMap();

        map.setImage(new Image(gameMap.getImagePath()));

        setPlayerIcons(gameSerialized);

        bindWeaponZoom();
        bindPanels();

        updateMap(gameSerialized);
    }

    private void bindPanels() {
        infoPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> hideInfoPanel());
        zoomPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> hideZoomPanel());
    }

    /**
     * Binds weapon zoom on card click
     */
    private void bindWeaponZoom() {
        for (ImageView weaponSlot : weaponSlotList) {
            weaponSlot.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showWeaponZoom);
        }
    }

    private void setPlayerIcons(GameSerialized gameSerialized) {
        ImageView imageView;

        for (UserPlayer player : gameSerialized.getPlayers()) {
            imageView = new ImageView();
            imageView.setId(getIconIDFromColor(player.getColor()));
            imageView.getProperties().put(USERNAME_PROPERTY, player.getUsername());

            iconList.getChildren().add(imageView);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showPlayerInfo);
        }

        if (gameSerialized.isBotPresent()) {
            imageView = new ImageView();
            imageView.setId(getIconIDFromColor(gameSerialized.getBot().getColor()));
            imageView.getProperties().put(USERNAME_PROPERTY, "bot");

            iconList.getChildren().add(imageView);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showPlayerInfo);
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
            default:
                return null;
        }
    }

    void setTurnOwnerIcon(String turnOwner) {
        for (Node children : iconList.getChildren()) {
            children.getStyleClass().clear();

            String iconOwner = (String) children.getProperties().get(USERNAME_PROPERTY);

            if (iconOwner.equals(turnOwner)) {
                children.getStyleClass().add("turnOwner");
            } else {
                children.getStyleClass().add("notTurnOwner");
            }
        }
    }

    void onStateUpdate(GameSerialized gameSerialized) {
        setTurnOwnerIcon(GuiManager.getInstance().getTurnOwner());
        updateMap(gameSerialized);
    }

    /**
     * Updates element on the map
     *
     * @param gameSerialized game update
     */
    private void updateMap(GameSerialized gameSerialized) {
        setWeaponCards(gameSerialized.getGameMap());
        setPlayersOnMap(gameSerialized.getGameMap().getMapID(), gameSerialized.getAllPlayers());
        setAmmoTiles(gameSerialized.getGameMap());
    }

    /**
     * Sets weapon cards on the map
     *
     * @param gameMap map of the game
     */
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
            Image image;

            if (weaponCards.get(i) != null) {
                image = new Image(weaponCards.get(i).getImagePath());
                weaponSlotList.get(i).setImage(image);
                weaponColor.put(image.getUrl(), weaponCards.get(i).getCost()[0]);
            } else {
                weaponSlotList.get(i).setImage(null);
            }

        }
    }

    /**
     * Sets ammo tiles on the map
     *
     * @param gameMap map of the game
     */
    private void setAmmoTiles(GameMap gameMap) {
        for (ImageView ammoTile : ammoTiles) {
            boardArea.getChildren().remove(ammoTile);
        }

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                if (square != null && square.getSquareType() == SquareType.TILE) {
                    CardSquare cardSquare = (CardSquare) square;

                    ImageView ammoTile = (cardSquare.isAmmoTilePresent()) ?
                            new ImageView(cardSquare.getAmmoTile().getImagePath()) : new ImageView();

                    ammoTile.setFitHeight(32);
                    ammoTile.setFitWidth(32);

                    StackPane.setAlignment(ammoTile, Pos.TOP_LEFT);
                    StackPane.setMargin(ammoTile, MapInsetsHelper.getAmmoTileInsets(gameMap.getMapID(), x, y));

                    boardArea.getChildren().add(ammoTile);
                    ammoTiles.add(ammoTile);
                }
            }
        }
    }

    /**
     * Sets players on the map
     *
     * @param mapID      id of the map
     * @param allPlayers list of players
     */
    private void setPlayersOnMap(int mapID, ArrayList<Player> allPlayers) {
        for (ImageView playerFigure : playerFigures) {
            boardArea.getChildren().remove(playerFigure);
        }

        for (int i = 0; i < allPlayers.size(); ++i) {
            Player player = allPlayers.get(i);

            if (player.getPosition() != null) {

                int count = 0;
                for (int j = i - 1; j >= 0; --j) {
                    if (allPlayers.get(j).getPosition().equals(player.getPosition())) {
                        ++count;
                    }
                }

                ImageView playerFigure = new ImageView(getColorFigurePath(player.getColor()));

                StackPane.setAlignment(playerFigure, Pos.TOP_LEFT);
                StackPane.setMargin(playerFigure, MapInsetsHelper.getPlayerInsets(mapID, player.getPosition().getCoordX(), player.getPosition().getCoordY(), count));

                boardArea.getChildren().add(playerFigure);
                playerFigures.add(playerFigure);
            }
        }
    }

    /**
     * Returns the path of the figure image based on color
     *
     * @param playerColor color of the player
     * @return path of the figure image
     */
    private String getColorFigurePath(PlayerColor playerColor) {
        switch (playerColor) {
            case BLUE:
                return "/img/players/blueFigure.png";
            case YELLOW:
                return "/img/players/yellowFigure.png";
            case GREEN:
                return "/img/players/greenFigure.png";
            case PURPLE:
                return "/img/players/purpleFigure.png";
            case GREY:
                return "/img/players/greyFigure.png";
            default:
                return "";
        }
    }

    /**
     * Shows the zoom on a weapon in the zoom panel
     *
     * @param event of the click on a weapon
     */
    private void showWeaponZoom(Event event) {
        ImageView weaponTarget = (ImageView) event.getTarget();

        if (weaponTarget != null) {
            setBoardOpaque(0.3);

            zoomPanel.toFront();
            ImageView weapon = new ImageView(weaponTarget.getImage());

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

                weapon.getStyleClass().add(className);

                zoomPanel.getChildren().add(weapon);
                zoomPanel.setVisible(true);
            }
        }
    }

    /**
     * Hides the zoom panel
     */
    private void hideZoomPanel() {
        zoomPanel.getChildren().clear();
        zoomPanel.setVisible(false);

        setBoardOpaque(1);
    }

    /**
     * Sets a opacity value for every element on the board
     *
     * @param value opacity value
     */
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

        for (ImageView ammotile : ammoTiles) {
            ammotile.opacityProperty().setValue(value);
        }

        for (ImageView playerFigure : playerFigures) {
            playerFigure.opacityProperty().setValue(value);
        }
    }

    void onError(String error) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Error", error);
    }

    /**
     * Empties the list of action buttons
     */
    void notYourTurn(String turnOwner) {
        actionList.getChildren().clear();
        setTurnOwnerIcon(turnOwner);
    }

    /**
     * Displays action buttons
     *
     * @param possibleActions possible actions
     */
    void displayAction(List<PossibleAction> possibleActions) {
        actionList.getChildren().clear();

        for (PossibleAction possibleAction : possibleActions) {
            ImageView imageView = new ImageView();
            imageView.setId(getActionIDFromPossibleAction(possibleAction));
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> guiManager.doAction(possibleAction));
            imageView.getStyleClass().add("button");

            actionList.getChildren().add(imageView);
        }
    }

    /**
     * Returns the CSS ID of the action based on the PossibleAction
     *
     * @param possibleAction possible action passed
     * @return the CSS ID
     */
    private String getActionIDFromPossibleAction(PossibleAction possibleAction) {
        switch (possibleAction) {
            case SPAWN_BOT:
            case RESPAWN_BOT:
                return "spawnBotAction";
            case CHOOSE_SPAWN:
            case CHOOSE_RESPAWN:
                return "playerSpawnAction";
            case POWER_UP:
                return "powerupAction";
            case GRENADE_USAGE:
                return "grenadeAction";
            case SCOPE_USAGE:
                return "scopeAction";
            case MOVE:
                return "moveAction";
            case MOVE_AND_PICK:
                return "movePickAction";
            case SHOOT:
                return "shootAction";
            case RELOAD:
                return "reloadAction";
            case ADRENALINE_PICK:
                return "adrenalinePickAction";
            case ADRENALINE_SHOOT:
                return "adrenalineShootAction";
            case FRENZY_MOVE:
                return "frenzyMoveAction";
            case FRENZY_PICK:
                return "frenzyPickAction";
            case FRENZY_SHOOT:
                return "frenzyShootAction";
            case LIGHT_FRENZY_PICK:
                return "lightFrenzyPickAction";
            case LIGHT_FRENZY_SHOOT:
                return "lightFrenzyShootAction";
            case BOT_ACTION:
                return "botAction";
            case PASS_TURN:
                return "passTurnAction";
            default:
                return null;
        }
    }

    /**
     * Shows the player info in the info panel
     *
     * @param event event of the click on a icon
     */
    private void showPlayerInfo(Event event) {
        ImageView playerIcon = (ImageView) event.getTarget();
        String username = (String) playerIcon.getProperties().get(USERNAME_PROPERTY);

        if (guiManager.getUsername().equals(username)) {
            showMyPlayerInfo(guiManager.getPlayer());
        } else if (username.equals(GameCostants.BOT_NAME)) {
            showBotPlayerInfo((Bot) guiManager.getPlayerByName(username));
        } else {
            showOthersPlayerInfo((UserPlayer) guiManager.getPlayerByName(username));
        }

        setBoardOpaque(0.3);
        infoPanel.toFront();
        infoPanel.setVisible(true);

    }

    private void showMyPlayerInfo(UserPlayer me) {
        setUsernamePlayerInfo(me.getUsername());
        addPlayerBoardToPlayerInfo(me);
        setDamages(me.getPlayerBoard());
        setAmmo(me);
    }

    private void showBotPlayerInfo(Bot bot) {
        setUsernamePlayerInfo(bot.getUsername());
        addPlayerBoardToPlayerInfo(bot);
        setDamages(bot.getPlayerBoard());
    }

    private void showOthersPlayerInfo(UserPlayer other) {
        setUsernamePlayerInfo(other.getUsername());
        addPlayerBoardToPlayerInfo(other);
        setDamages(other.getPlayerBoard());
        setAmmo(other);
    }

    private void setUsernamePlayerInfo(String username) {
        Label label = new Label(username);
        label.getStyleClass().add("infoTitle");

        VBox vBox = new VBox();
        vBox.getStyleClass().add("topInfoPanel");
        vBox.getChildren().add(label);

        infoPanel.setTop(vBox);
    }

    private void addPlayerBoardToPlayerInfo(Player player) {
        PlayerColor playerColor = player.getColor();
        PlayerBoard playerBoard = player.getPlayerBoard();

        AnchorPane anchorPane = new AnchorPane();

        ImageView playerBoardImageView = new ImageView(getPlayboardPath(playerColor, playerBoard));
        playerBoardImageView.setFitWidth(680);
        playerBoardImageView.setFitHeight(166);

        AnchorPane.setLeftAnchor(playerBoardImageView, 310.0);

        anchorPane.getChildren().add(playerBoardImageView);
        infoPanel.setCenter(anchorPane);
    }

    private String getPlayboardPath(PlayerColor playerColor, PlayerBoard playerBoard) {
        String suffix;

        if (playerBoard.isBoardFlipped()) {
            suffix = "back";
        } else if (guiManager.getGameClientState() == GameClientState.NORMAL) {
            suffix = "front";
        } else {
            suffix = "front_final";
        }

        return "/img/boards/" + playerColor.name().toLowerCase() + "_" + suffix + ".png";
    }

    private void setAmmo(UserPlayer player) {
        AmmoQuantity ammoQuantity = player.getPlayerBoard().getAmmo();

        for (int i = 0; i < ammoQuantity.getRedAmmo(); ++i) {
            ImageView redAmmo = new ImageView("/img/ammo/redAmmo.png");
            AnchorPane.setLeftAnchor(redAmmo, MapInsetsHelper.startingFirstAmmoInsets.getLeft() + i * MapInsetsHelper.AMMO_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(redAmmo, MapInsetsHelper.startingFirstAmmoInsets.getTop());
            ((AnchorPane) infoPanel.getCenter()).getChildren().add(redAmmo);
        }

        for (int i = 0; i < ammoQuantity.getYellowAmmo(); ++i) {
            ImageView yellowAmmo = new ImageView("/img/ammo/yellowAmmo.png");
            AnchorPane.setLeftAnchor(yellowAmmo, MapInsetsHelper.startingSecondAmmoInsets.getLeft() + i * MapInsetsHelper.AMMO_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(yellowAmmo, MapInsetsHelper.startingSecondAmmoInsets.getTop());
            ((AnchorPane) infoPanel.getCenter()).getChildren().add(yellowAmmo);
        }

        for (int i = 0; i < ammoQuantity.getBlueAmmo(); ++i) {
            ImageView blueAmmo = new ImageView("/img/ammo/blueAmmo.png");
            AnchorPane.setLeftAnchor(blueAmmo, MapInsetsHelper.startingThirdAmmoInsets.getLeft() + i * MapInsetsHelper.AMMO_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(blueAmmo, MapInsetsHelper.startingThirdAmmoInsets.getTop());
            ((AnchorPane) infoPanel.getCenter()).getChildren().add(blueAmmo);
        }
    }

    private void setDamages(PlayerBoard playerBoard) {
        List<String> damages =  playerBoard.getDamages();

        for (int i = 0; i < damages.size(); ++i) {
            String username = damages.get(i);
            PlayerColor damageDealerColor = guiManager.getPlayerByName(username).getColor();

            ImageView drop = new ImageView("/img/players/" + damageDealerColor.name().toLowerCase() + "Drop.png");
            AnchorPane.setLeftAnchor(drop, MapInsetsHelper.startingDamageInsets.getLeft() + i * MapInsetsHelper.DAMAGE_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(drop, MapInsetsHelper.startingDamageInsets.getTop());

            ((AnchorPane) infoPanel.getCenter()).getChildren().add(drop);
        }
    }

    /**
     * Hides the info panel
     */
    private void hideInfoPanel() {
        infoPanel.getChildren().clear();
        infoPanel.setVisible(false);

        setBoardOpaque(1);
    }
}
