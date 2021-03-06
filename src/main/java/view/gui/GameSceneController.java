package view.gui;

import enumerations.*;
import enumerations.Properties;
import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.actions.WeaponCardsNotFoundException;
import exceptions.utility.InvalidPropertiesException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.cards.effects.Effect;
import model.map.CardSquare;
import model.map.GameMap;
import model.map.SpawnSquare;
import model.map.Square;
import model.player.*;
import network.client.ClientGameManager;
import network.message.PowerupRequest;
import network.message.ReloadRequest;
import network.message.ShootRequest;
import utility.GameConstants;
import utility.MessageBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for the graphical interface of the game
 */
public class GameSceneController {
    private static final String USERNAME_PROPERTY = "username";

    private static final double OPAQUE = 0.2;
    private static final double NOT_OPAQUE = 1;

    private static final String CSS_CHECKBOX_IMAGE = "checkboxImage";
    private static final String CSS_BUTTON = "button";
    private static final String CSS_SQUARE_CLICK_BUTTON = "squareClickButton";
    private static final String CSS_SQUARE_OWNER_CLICK_BUTTON = "squareOwnerClickButton";
    private static final String CSS_EFFECT_DESC_BACKGROUND = "effectDescBackground";
    private static final String CSS_EFFECT_DESC = "effectDesc";

    private static final String NEXT_BUTTON_PATH = "/img/scenes/nextbutton.png";

    private static final double KILLSHOT_TRACK_SKULL_WIDTH = 20;
    private static final double KILLSHOT_TRACK_SKULL_HEIGHT = 28;

    private static final double PLAYER_BOARD_WIDTH = 680;
    private static final double PLAYER_BOARD_HEIGHT = 166;

    private static final double PLAYER_BOARD_SKULL_WIDTH = 25;
    private static final double PLAYER_BOARD_SKULL_HEIGHT = 38;

    private static final double WEAPON_CARD_WIDTH = 136;
    private static final double WEAPON_CARD_HEIGHT = 230;

    private static final double POWERUP_CARD_WIDTH = 128;
    private static final double POWERUP_CARD_HEIGHT = 200;

    private static final String BEFORE = "before";
    private static final String MIDDLE = "middle";
    private static final String AFTER = "after";

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
    Label pointLabel;
    @FXML
    FlowPane zoomPanel;
    @FXML
    BorderPane infoPanel;
    @FXML
    BorderPane actionPanel;

    private GuiManager guiManager;

    private List<ImageView> weaponSlotList;
    private List<ImageView> ammoTiles;
    private List<ImageView> killshotsImages;
    private List<ImageView> playerFigures;
    private Map<String, Ammo> weaponColor;

    private String infoPanelUsername = null;

    @FXML
    private void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setGameSceneController(this);

        ammoTiles = new ArrayList<>();
        playerFigures = new ArrayList<>();
        killshotsImages = new ArrayList<>();
        weaponSlotList = List.of(blueWeapon0, blueWeapon1, blueWeapon2, redWeapon0, redWeapon1, redWeapon2,
                yellowWeapon0, yellowWeapon1, yellowWeapon2);
    }

    /**
     * Setups the board and binds all the events
     *
     * @param gameSerialized state of the game at the time of the join
     */
    void setupGame(GameSerialized gameSerialized) {
        GameMap gameMap = gameSerialized.getGameMap();

        map.setImage(new Image(gameMap.getImagePath()));
        pointLabel.setText("Points: " + gameSerialized.getPoints());

        setPlayerIcons(gameSerialized);

        bindWeaponZoom();
        bindPanels();

        updateMap(gameSerialized);
    }

    /**
     * Binds click events on the panels
     */
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

    /**
     * Set the icons of the players on the left of the board
     *
     * @param gameSerialized status of the game at the time of the join
     */
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

    /**
     * Get the CSS ID of the icon from the PlayerColor
     *
     * @param playerColor PlayerColor corresponding to the desired ID color
     * @return the string of the CSS ID
     */
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

    /**
     * Enlight the turn owner icon and set opacity to the others
     *
     * @param turnOwner username of the player who owns the turn
     */
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

    /**
     * Updates the elements of the board
     */
    void onStateUpdate() {
        setTurnOwnerIcon(GuiManager.getInstance().getTurnOwner());
        updateMap(guiManager.getGameSerialized());

        pointLabel.setText("Points: " + guiManager.getGameSerialized().getPoints());

        if (infoPanelUsername != null) {
            showPlayerInfo(infoPanelUsername);
        }
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
        setKillshotTrack(gameSerialized);
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
        ammoTiles.clear();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                if (square != null && square.getSquareType() == SquareType.TILE) {
                    CardSquare cardSquare = (CardSquare) square;

                    ImageView ammoTile = (cardSquare.isAmmoTilePresent() && cardSquare.getAmmoTile() != null) ?
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
    private void setPlayersOnMap(int mapID, List<Player> allPlayers) {
        for (ImageView playerFigure : playerFigures) {
            boardArea.getChildren().remove(playerFigure);
        }

        playerFigures.clear();

        for (int i = 0; i < allPlayers.size(); ++i) {
            Player player = allPlayers.get(i);

            if (player.getPosition() != null) {

                int count = 0;
                for (int j = i - 1; j >= 0; --j) {
                    if (allPlayers.get(j).getPosition() != null && allPlayers.get(j).getPosition().equals(player.getPosition())) {
                        ++count;
                    }
                }

                ImageView playerFigure = new ImageView(getColorFigurePath(player.getColor()));

                StackPane.setAlignment(playerFigure, Pos.TOP_LEFT);
                StackPane.setMargin(playerFigure, MapInsetsHelper.getPlayerInsets(mapID, player.getPosition().getRow(), player.getPosition().getColumn(), count));

                boardArea.getChildren().add(playerFigure);
                playerFigures.add(playerFigure);
            }
        }
    }

    /**
     * Sets skulls and drops on the killshots track
     *
     * @param gameSerialized state of the game
     */
    private void setKillshotTrack(GameSerialized gameSerialized) {
        List<KillShot> killShots = new ArrayList<>(Arrays.asList(gameSerialized.getKillShotsTrack()));
        int killShotNum = gameSerialized.getKillShotNum();

        killShots.subList(killShotNum, killShots.size()).clear();

        for (ImageView killShot : killshotsImages) {
            boardArea.getChildren().remove(killShot);
        }

        killshotsImages.clear();

        killShots.addAll(gameSerialized.getFinalFrenzyKillShots());


        double startingLeftMargin = MapInsetsHelper.killShotTrackInsets.getLeft() + (8 - killShotNum) * MapInsetsHelper.KILLSHOT_TRACK_HORIZONTAL_OFFSET;
        double topMargin = MapInsetsHelper.killShotTrackInsets.getTop();

        for (int i = 0; i < killShots.size(); ++i) {
            KillShot killShot = killShots.get(i);
            double horizontalOffset = (killShotNum - i <= 0) ? MapInsetsHelper.KILLSHOT_TRACK_TINY_HORIZONTAL_OFFSET : MapInsetsHelper.KILLSHOT_TRACK_HORIZONTAL_OFFSET;

            if (killShot == null) {
                ImageView skull = new ImageView("/img/skull.png");
                skull.setFitWidth(KILLSHOT_TRACK_SKULL_WIDTH);
                skull.setFitHeight(KILLSHOT_TRACK_SKULL_HEIGHT);

                StackPane.setAlignment(skull, Pos.TOP_LEFT);
                StackPane.setMargin(skull, new Insets(topMargin, 0, 0, startingLeftMargin));

                boardArea.getChildren().add(skull);
                killshotsImages.add(skull);
            } else {
                PlayerColor playerColor = guiManager.getPlayerByName(killShot.getKiller()).getColor();
                String dropPath = getDropPath(playerColor);

                if (killShot.getPoints() > 1) {
                    addDropToKillshotTrack(dropPath, topMargin - MapInsetsHelper.KILLSHOT_TRACK_VERTICAL_OFFSET, startingLeftMargin);
                }

                addDropToKillshotTrack(dropPath, topMargin, startingLeftMargin);

                if (killShot.getPoints() > 2) {
                    addDropToKillshotTrack(dropPath, topMargin + MapInsetsHelper.KILLSHOT_TRACK_VERTICAL_OFFSET, startingLeftMargin);
                }
            }

            startingLeftMargin += horizontalOffset;
        }

    }

    /**
     * Adds a drop to the killshot track
     *
     * @param dropPath   path of the drop
     * @param topMargin  top margin
     * @param leftMargin left margin
     */
    private void addDropToKillshotTrack(String dropPath, double topMargin, double leftMargin) {
        ImageView drop = new ImageView(dropPath);
        StackPane.setAlignment(drop, Pos.TOP_LEFT);
        StackPane.setMargin(drop, new Insets(topMargin, 0, 0, leftMargin));

        boardArea.getChildren().add(drop);
        killshotsImages.add(drop);
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
            setBoardOpaque(OPAQUE);

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
                zoomPanel.toFront();
            }
        }
    }

    /**
     * Hides the zoom panel
     */
    private void hideZoomPanel() {
        zoomPanel.getChildren().clear();
        zoomPanel.setVisible(false);

        setBoardOpaque(NOT_OPAQUE);
        setTurnOwnerIcon(guiManager.getTurnOwner());
    }

    /**
     * Hides the action panel
     */
    private void hideActionPanel() {
        actionPanel.getChildren().clear();
        actionPanel.setVisible(false);

        setBoardOpaque(NOT_OPAQUE);
        setTurnOwnerIcon(guiManager.getTurnOwner());
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

        for (ImageView killshots : killshotsImages) {
            killshots.opacityProperty().setValue(value);
        }

        for (Node node : actionList.getChildren()) {
            node.opacityProperty().setValue(value);
        }

        for (Node node : iconList.getChildren()) {
            node.opacityProperty().setValue(value);
        }
    }

    /**
     * Called when an error occurs. Displays an alert with the error message
     *
     * @param error message of the error
     */
    void onError(String error) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, error);
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
            imageView.getStyleClass().add(CSS_BUTTON);

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
     * Shows the info of a player in the info panel
     *
     * @param username username of the player who you want to displays informations
     */
    private void showPlayerInfo(String username) {
        infoPanelUsername = username;

        if (guiManager.getUsername().equals(username)) {
            showMyPlayerInfo(guiManager.getPlayer());
        } else if (username.equals(GameConstants.BOT_NAME)) {
            showBotPlayerInfo((Bot) guiManager.getPlayerByName(username));
        } else {
            showOthersPlayerInfo((UserPlayer) guiManager.getPlayerByName(username));
        }

        setBoardOpaque(OPAQUE);
        infoPanel.setVisible(true);
        infoPanel.toFront();
    }

    /**
     * Shows the player info in the info panel
     *
     * @param event event of the click on a icon
     */
    private void showPlayerInfo(Event event) {
        ImageView playerIcon = (ImageView) event.getTarget();
        String username = (String) playerIcon.getProperties().get(USERNAME_PROPERTY);

        showPlayerInfo(username);
    }

    /**
     * Shows the player info of the client owner
     *
     * @param me UserPlayer of the client owner
     */
    private void showMyPlayerInfo(UserPlayer me) {
        setUsernamePlayerInfo(me.getUsername());

        addPlayerBoardToPlayerInfo(me);
        setDamages(me.getPlayerBoard());
        setMarks(me.getPlayerBoard());
        setAmmo(me);
        setPlayerboardSkulls(me.getPlayerBoard());

        setWeapons(Arrays.asList(me.getWeapons()), (guiManager.isBotPresent() &&
                me.getUsername().equals(guiManager.getTurnOwner())));
        setPowerups(guiManager.getPowerups());
    }

    /**
     * Shows the bot info
     *
     * @param bot bot
     */
    private void showBotPlayerInfo(Bot bot) {
        setUsernamePlayerInfo(bot.getUsername());

        addPlayerBoardToPlayerInfo(bot);
        setDamages(bot.getPlayerBoard());
        setMarks(bot.getPlayerBoard());
        setPlayerboardSkulls(bot.getPlayerBoard());
    }

    /**
     * Shows the player info of another player
     *
     * @param other UserPlayer who you wants to displays informations
     */
    private void showOthersPlayerInfo(UserPlayer other) {
        setUsernamePlayerInfo(other.getUsername());

        addPlayerBoardToPlayerInfo(other);
        setDamages(other.getPlayerBoard());
        setMarks(other.getPlayerBoard());
        setAmmo(other);
        setPlayerboardSkulls(other.getPlayerBoard());

        setWeapons(Arrays.asList(other.getWeapons()), (guiManager.isBotPresent() &&
                other.getUsername().equals(guiManager.getTurnOwner())));
    }

    /**
     * Sets the username in the info panel
     *
     * @param username username of the player
     */
    private void setUsernamePlayerInfo(String username) {
        Label label = new Label(username);
        label.getStyleClass().add("infoTitle");

        VBox vBox = new VBox();
        vBox.getStyleClass().add("topInfoPanel");
        vBox.getChildren().add(label);

        infoPanel.setTop(vBox);
    }

    /**
     * Adds the playerboard to the info panel
     *
     * @param player player who you want to display the information
     */
    private void addPlayerBoardToPlayerInfo(Player player) {
        PlayerColor playerColor = player.getColor();
        PlayerBoard playerBoard = player.getPlayerBoard();

        AnchorPane anchorPane = new AnchorPane();

        ImageView playerBoardImageView = new ImageView(getPlayerBoardPath(playerColor, playerBoard));
        playerBoardImageView.setFitWidth(PLAYER_BOARD_WIDTH);
        playerBoardImageView.setFitHeight(PLAYER_BOARD_HEIGHT);

        AnchorPane.setLeftAnchor(playerBoardImageView, MapInsetsHelper.playerBoardInsets.getLeft());

        anchorPane.getChildren().add(playerBoardImageView);
        infoPanel.setCenter(anchorPane);
    }

    /**
     * Returns the path of the player board from the playerColo and the playerboard status
     *
     * @param playerColor Color of the player
     * @param playerBoard player board of the player
     * @return the path of the player board
     */
    private String getPlayerBoardPath(PlayerColor playerColor, PlayerBoard playerBoard) {
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

    /**
     * Sets the ammo on the player board
     *
     * @param player player who you want to display the ammo
     */
    private void setAmmo(UserPlayer player) {
        AmmoQuantity ammoQuantity = player.getPlayerBoard().getAmmo();

        for (int i = 0; i < ammoQuantity.getRedAmmo(); ++i) {
            ImageView redAmmo = new ImageView("/img/ammo/redAmmo.png");
            AnchorPane.setLeftAnchor(redAmmo, MapInsetsHelper.firstAmmoInsets.getLeft() + i * MapInsetsHelper.AMMO_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(redAmmo, MapInsetsHelper.firstAmmoInsets.getTop());
            ((AnchorPane) infoPanel.getCenter()).getChildren().add(redAmmo);
        }

        for (int i = 0; i < ammoQuantity.getYellowAmmo(); ++i) {
            ImageView yellowAmmo = new ImageView("/img/ammo/yellowAmmo.png");
            AnchorPane.setLeftAnchor(yellowAmmo, MapInsetsHelper.secondAmmoInsets.getLeft() + i * MapInsetsHelper.AMMO_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(yellowAmmo, MapInsetsHelper.secondAmmoInsets.getTop());
            ((AnchorPane) infoPanel.getCenter()).getChildren().add(yellowAmmo);
        }

        for (int i = 0; i < ammoQuantity.getBlueAmmo(); ++i) {
            ImageView blueAmmo = new ImageView("/img/ammo/blueAmmo.png");
            AnchorPane.setLeftAnchor(blueAmmo, MapInsetsHelper.thirdAmmoInsets.getLeft() + i * MapInsetsHelper.AMMO_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(blueAmmo, MapInsetsHelper.thirdAmmoInsets.getTop());
            ((AnchorPane) infoPanel.getCenter()).getChildren().add(blueAmmo);
        }
    }

    /**
     * Sets the damages drop on the player board
     *
     * @param playerBoard player board who you want to displays damages drops
     */
    private void setDamages(PlayerBoard playerBoard) {
        List<String> damages = playerBoard.getDamages();

        for (int i = 0; i < damages.size(); ++i) {
            String username = damages.get(i);
            PlayerColor damageDealerColor = guiManager.getPlayerByName(username).getColor();

            ImageView drop = new ImageView(getDropPath(damageDealerColor));
            AnchorPane.setLeftAnchor(drop, MapInsetsHelper.damageInsets.getLeft() + i * MapInsetsHelper.DAMAGE_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(drop, MapInsetsHelper.damageInsets.getTop());

            ((AnchorPane) infoPanel.getCenter()).getChildren().add(drop);
        }
    }

    /**
     * Sets the marks drop on the player board
     *
     * @param playerBoard player board who you want to displays marks drops
     */
    private void setMarks(PlayerBoard playerBoard) {
        List<String> marks = playerBoard.getMarks();

        for (int i = 0; i < marks.size(); ++i) {
            String username = marks.get(i);
            PlayerColor markDealerColor = guiManager.getPlayerByName(username).getColor();

            ImageView drop = new ImageView(getDropPath(markDealerColor));
            AnchorPane.setLeftAnchor(drop, MapInsetsHelper.marksInsets.getLeft() + i * MapInsetsHelper.MARKS_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(drop, MapInsetsHelper.marksInsets.getTop());

            ((AnchorPane) infoPanel.getCenter()).getChildren().add(drop);
        }
    }

    /**
     * Sets the weapons card in the info panel
     *
     * @param weapons List of the weapons to display
     * @param botcard {@code true} if the bot card has to be displayed, {@code false} otherwise
     */
    private void setWeapons(List<WeaponCard> weapons, boolean botcard) {
        if (weapons.isEmpty()) {
            return;
        }

        HBox weaponHBox = new HBox();
        weaponHBox.getStyleClass().add("infoHBOX");
        weaponHBox.setAlignment(Pos.BASELINE_CENTER);
        weaponHBox.setSpacing(20);

        for (WeaponCard weapon : weapons) {
            ImageView weaponImage = new ImageView(weapon.getImagePath());

            weaponImage.setFitHeight(WEAPON_CARD_HEIGHT);
            weaponImage.setFitWidth(WEAPON_CARD_WIDTH);

            if (weapon.status() == 1) {
                ColorAdjust monochrome = new ColorAdjust();
                monochrome.setSaturation(-1);
                weaponImage.setEffect(monochrome);
            }

            weaponHBox.getChildren().add(weaponImage);
        }

        if (botcard) {
            ImageView weaponImage = new ImageView("/img/weapons/bot_front.png");

            weaponImage.setFitHeight(WEAPON_CARD_HEIGHT);
            weaponImage.setFitWidth(WEAPON_CARD_WIDTH);

            weaponHBox.getChildren().add(weaponImage);
        }

        AnchorPane.setTopAnchor(weaponHBox, MapInsetsHelper.weaponHBoxInsets.getTop());
        ((AnchorPane) infoPanel.getCenter()).getChildren().add(weaponHBox);
    }

    /**
     * Sets the powerups in the info panels
     *
     * @param powerups List of the powerups that needs to be set
     */
    private void setPowerups(List<PowerupCard> powerups) {
        if (powerups.isEmpty()) {
            return;
        }

        HBox powerupHBox = new HBox();
        powerupHBox.getStyleClass().add("infoHBOX");
        powerupHBox.setAlignment(Pos.BASELINE_CENTER);
        powerupHBox.setSpacing(20);

        for (PowerupCard powerup : powerups) {
            ImageView powerupImage = new ImageView(powerup.getImagePath());
            powerupImage.setFitWidth(POWERUP_CARD_WIDTH);
            powerupImage.setFitHeight(POWERUP_CARD_HEIGHT);
            powerupHBox.getChildren().add(powerupImage);
        }

        AnchorPane.setTopAnchor(powerupHBox, MapInsetsHelper.powerupsHBoxInsets.getTop());
        ((AnchorPane) infoPanel.getCenter()).getChildren().add(powerupHBox);
    }

    /**
     * Sets the skulls on the playerboard
     *
     * @param playerBoard board which has skulls on it
     */
    private void setPlayerboardSkulls(PlayerBoard playerBoard) {
        Insets startingInsets;
        if (playerBoard.isBoardFlipped()) {
            startingInsets = MapInsetsHelper.playerBoardFrenzySkullInsets;
        } else {
            startingInsets = MapInsetsHelper.playerBoardSkullInsets;
        }

        int skullNum = playerBoard.getSkulls();

        for (int i = 0; i < skullNum; ++i) {
            ImageView skull = new ImageView("/img/skull.png");
            skull.setFitWidth(PLAYER_BOARD_SKULL_WIDTH);
            skull.setFitHeight(PLAYER_BOARD_SKULL_HEIGHT);

            AnchorPane.setLeftAnchor(skull, startingInsets.getLeft() + i * MapInsetsHelper.PLAYER_BOARD_SKULL_HORIZONTAL_OFFSET);
            AnchorPane.setTopAnchor(skull, startingInsets.getTop());

            ((AnchorPane) infoPanel.getCenter()).getChildren().add(skull);
        }
    }

    /**
     * Returns the path of the drop image from the player color
     *
     * @param playerColor color of the desired drop
     * @return the path of the image
     */
    private String getDropPath(PlayerColor playerColor) {
        return "/img/players/" + playerColor.name().toLowerCase() + "Drop.png";
    }

    /**
     * Hides the info panel
     */
    private void hideInfoPanel() {
        infoPanel.getChildren().clear();
        infoPanel.setVisible(false);

        setBoardOpaque(NOT_OPAQUE);

        infoPanelUsername = null;

        setTurnOwnerIcon(guiManager.getTurnOwner());
    }

    /**
     * Sets the title of the action panel
     *
     * @param title title of the panel
     */
    private void setActionPanelTitle(String title) {
        Label label = new Label(title);
        label.getStyleClass().add("infoTitle");

        VBox vBox = new VBox();
        vBox.getStyleClass().add("topActionPanel");
        vBox.getChildren().add(label);

        actionPanel.setTop(vBox);
    }

    /**
     * Sets the bottom layout of the action panel
     */
    private void setActionPanelBottom() {
        HBox botHBox = new HBox();
        botHBox.setAlignment(Pos.BASELINE_CENTER);
        botHBox.setSpacing(20);

        ImageView backButton = new ImageView("/img/scenes/backbutton.png");
        backButton.getStyleClass().add(CSS_BUTTON);
        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> hideActionPanel());
        botHBox.getChildren().add(backButton);

        actionPanel.setBottom(botHBox);
    }

    /**
     * Handles the spawn action
     */
    void spawn() {
        List<PowerupCard> powerups = guiManager.getPowerups();

        setActionPanelTitle("Player Spawn");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        for (int i = 0; i < powerups.size(); i++) {
            final int powerupIndex = i;

            ImageView img = new ImageView(powerups.get(i).getImagePath());
            img.getStyleClass().add(CSS_BUTTON);
            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onClickPowerupSpawn(powerupIndex));
            hBox.getChildren().add(img);
        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the click on a spawning powerup
     *
     * @param powerupIndex index of the powerup clicked
     */
    private void onClickPowerupSpawn(int powerupIndex) {
        hideActionPanel();

        try {
            if (!guiManager.sendRequest(MessageBuilder.buildSpawnDiscardPowerupRequest(guiManager.getClientToken(),
                    guiManager.getPowerups(), guiManager.getSpawnPowerup(),
                    guiManager.getPowerups().get(powerupIndex), guiManager.getUsername()))) {
                GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
            }
        } catch (PowerupCardsNotFoundException e) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, e.getMessage());
        }
    }

    /**
     * Handles the move action
     *
     * @param title    Name of the move
     * @param distance max move distance
     */
    void move(String title, int distance) {
        setActionPanelTitle(title);
        GameMap gameMap = guiManager.getGameMap();
        PlayerPosition playerPosition = guiManager.getPlayer().getPosition();

        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                PlayerPosition tempPos = new PlayerPosition(x, y);

                if (square != null && tempPos.distanceOf(playerPosition, gameMap) <= distance && tempPos.distanceOf(playerPosition, gameMap) > 0) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(tempPos.equals(playerPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onMoveMapSlotClick(tempPos));

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the click on a map button for move
     *
     * @param playerPosition position of the button clicked
     */
    private void onMoveMapSlotClick(PlayerPosition playerPosition) {
        hideActionPanel();

        if (!guiManager.sendRequest(MessageBuilder.buildMoveRequest(guiManager.getClientToken(), guiManager.getPlayer(), playerPosition))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles the pass turn
     */
    void passTurn() {
        if (!guiManager.sendRequest(MessageBuilder.buildPassTurnRequest(guiManager.getClientToken(), guiManager.getPlayer()))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles the bot spawn
     *
     * @param respawn {@code true} if it a respawn, {@code false} otherwise
     */
    void spawnBot(boolean respawn) {
        setActionPanelTitle((respawn) ? "Respawn Bot" : "Spawn bot");
        GameMap gameMap = guiManager.getGameMap();

        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                PlayerPosition tempPos = new PlayerPosition(x, y);

                if (square != null && square.getSquareType() == SquareType.SPAWN) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(CSS_SQUARE_CLICK_BUTTON);

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onSpawnBotClick(tempPos));

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the click on a spawn bot location
     *
     * @param botSpawnPosition position of bot spawn
     */
    private void onSpawnBotClick(PlayerPosition botSpawnPosition) {
        hideActionPanel();

        if (!guiManager.sendRequest(MessageBuilder.buildBotSpawnRequest(guiManager.getPlayer(), guiManager.getClientToken(), guiManager.getGameMap().getSquare(botSpawnPosition)))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles the move and pick action
     *
     * @param title    title of the action
     * @param distance max move and pick distance
     */
    void moveAndPick(String title, int distance) {
        setActionPanelTitle(title);
        GameMap gameMap = guiManager.getGameMap();
        PlayerPosition playerPosition = guiManager.getPlayer().getPosition();

        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                PlayerPosition tempPos = new PlayerPosition(x, y);

                if (square != null && tempPos.distanceOf(playerPosition, gameMap) <= distance) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(tempPos.equals(playerPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

                    setMovePickSquareClickEvent(square, mapButton, tempPos);

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Sets the event handler for the click on a square of move and pick
     *
     * @param square    Square clicked
     * @param mapButton Button clicked
     * @param tempPos   Position of the square in te map
     */
    private void setMovePickSquareClickEvent(Square square, Button mapButton, PlayerPosition tempPos) {
        if (square.getSquareType() == SquareType.TILE) {
            mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onTilePickClick(tempPos));
        } else {
            mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onWeaponPickClick(tempPos));
        }
    }

    /**
     * Handles the click on an ammo tile
     *
     * @param pickPosition position of ammo pick
     */
    private void onTilePickClick(PlayerPosition pickPosition) {
        hideActionPanel();

        if (!guiManager.sendRequest(MessageBuilder.buildMovePickRequest(guiManager.getClientToken(), guiManager.getPlayer(), pickPosition))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles the click on a weapon square
     *
     * @param pickPosition position of weapon pick
     */
    private void onWeaponPickClick(final PlayerPosition pickPosition) {
        SpawnSquare weaponSquare = (SpawnSquare) guiManager.getGameMap().getSquare(pickPosition);
        List<WeaponCard> weaponCards = Arrays.asList(weaponSquare.getWeapons());

        if (weaponCards.isEmpty()) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, "Invalid move and pick action");
            return;
        }

        HBox hBox = setHorizontalLayout("Weapon Pick");

        for (int i = 0; i < weaponCards.size(); i++) {
            final int weaponIndex = i;
            WeaponCard weaponCard = weaponCards.get(i);
            if (weaponCard != null) {
                ImageView img = new ImageView(weaponCard.getImagePath());
                img.getStyleClass().add(CSS_BUTTON);
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onWeaponCardPickClick(pickPosition, weaponCards.get(weaponIndex)));
                hBox.getChildren().add(img);
            }
        }

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the click on the weapon card chosen
     *
     * @param pickPosition position of pick
     * @param weaponCard   weaponcard picked
     */
    private void onWeaponCardPickClick(final PlayerPosition pickPosition, final WeaponCard weaponCard) {
        ArrayList<Integer> paymentPowerups = new ArrayList<>();
        ArrayList<PowerupCard> powerupCards = new ArrayList<>(guiManager.getPowerups());

        if (guiManager.getPowerups().isEmpty()) {
            onCheckWeaponSwap(pickPosition, weaponCard, paymentPowerups);
        } else {
            actionPanel.getChildren().clear();

            setActionPanelTitle("Payment Powerups");

            setMultiplePowerupSelectLayout(powerupCards);

            setActionPanelBottom();

            HBox botHBox = (HBox) actionPanel.getBottom();
            ImageView nextButton = new ImageView(NEXT_BUTTON_PATH);
            nextButton.getStyleClass().add(CSS_BUTTON);

            nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onCheckWeaponSwap(pickPosition, weaponCard, getMultiplePowerupIndexes()));
            botHBox.getChildren().add(nextButton);

            setBoardOpaque(OPAQUE);
            actionPanel.setVisible(true);
            actionPanel.toFront();
        }
    }

    /**
     * Ask for a swap if the player has 3 weapons
     *
     * @param pickPosition    position of pick
     * @param weaponCard      card picked
     * @param paymentPowerups list of powerups used for payment
     */
    private void onCheckWeaponSwap(final PlayerPosition pickPosition, final WeaponCard weaponCard, final ArrayList<Integer> paymentPowerups) {
        if (guiManager.getPlayer().getWeapons().length < 3) {
            sendPickRequest(pickPosition, weaponCard, paymentPowerups, null);
        } else {
            List<WeaponCard> weaponCards = new ArrayList<>(Arrays.asList(guiManager.getPlayer().getWeapons()));

            actionPanel.getChildren().clear();

            setActionPanelTitle("Weapon Swap");

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.BASELINE_CENTER);
            hBox.setSpacing(20);
            vBox.getChildren().add(hBox);

            for (WeaponCard discardingWeap : weaponCards) {
                ImageView img = new ImageView(discardingWeap.getImagePath());
                img.getStyleClass().add(CSS_BUTTON);
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> sendPickRequest(pickPosition, weaponCard, paymentPowerups, discardingWeap));
                hBox.getChildren().add(img);
            }

            actionPanel.setCenter(vBox);

            setActionPanelBottom();

            setBoardOpaque(OPAQUE);
            actionPanel.setVisible(true);
            actionPanel.toFront();
        }
    }

    /**
     * Sends the pick request
     *
     * @param pickPosition     position of pick
     * @param weaponCard       card picked
     * @param paymentPowerups  list of powerups used for payment
     * @param discardingWeapon weapon choosed for discard
     */
    private void sendPickRequest(final PlayerPosition pickPosition, final WeaponCard weaponCard, final ArrayList<Integer> paymentPowerups, final WeaponCard discardingWeapon) {
        hideActionPanel();

        try {
            if (!guiManager.sendRequest(MessageBuilder.buildMovePickRequest(guiManager.getClientToken(), guiManager.getPlayer(), pickPosition, paymentPowerups, weaponCard, discardingWeapon))) {
                GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
            }
        } catch (
                PowerupCardsNotFoundException e) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, e.getMessage());
        }
    }

    /**
     * Handles shoot action
     */
    void shoot() {
        chooseShootWeapon(null, null);
    }

    /**
     * Handles move shoot actin
     *
     * @param title    title of the action
     * @param distance max distance of move
     * @param frenzy   {@code true} if it is a frenzy move pick, {@code false} otherwise
     */
    void moveShoot(String title, int distance, boolean frenzy) {
        setActionPanelTitle(title);
        GameMap gameMap = guiManager.getGameMap();
        PlayerPosition playerPosition = guiManager.getPlayer().getPosition();

        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                PlayerPosition tempPos = new PlayerPosition(x, y);

                if (square != null && tempPos.distanceOf(playerPosition, gameMap) <= distance && tempPos.distanceOf(playerPosition, gameMap) >= 0) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(tempPos.equals(playerPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> checkFrenzy(tempPos, frenzy));


                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Check next action based if it is an frenzy action or not
     *
     * @param tempPos position of move before
     * @param frenzy  {@code true} if it is a frenzy move pick, {@code false} otherwise
     */
    private void checkFrenzy(PlayerPosition tempPos, boolean frenzy) {
        if (frenzy) {
            reloadBeforeShoot(tempPos);
        } else {
            chooseShootWeapon(tempPos, null);
        }
    }

    /**
     * Sets the reload weapons before shoots
     *
     * @param moveBeforeShoot position of moving before shoot
     */
    private void reloadBeforeShoot(final PlayerPosition moveBeforeShoot) {
        actionPanel.getChildren().clear();

        List<WeaponCard> weapons = new ArrayList<>(Arrays.asList(guiManager.getPlayer().getWeapons()));

        setActionPanelTitle("Frenzy Reload");

        setReloadLayout(weapons);

        setActionPanelBottom();

        HBox botHBox = (HBox) actionPanel.getBottom();
        ImageView nextButton = new ImageView(NEXT_BUTTON_PATH);
        nextButton.getStyleClass().add(CSS_BUTTON);

        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> chooseShootWeapon(moveBeforeShoot, getReloadWeaponIndexes()));
        botHBox.getChildren().add(nextButton);

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the choose of the weapon for shooting
     *
     * @param moveBeforeShoot   position of the move before shoot
     * @param rechargingWeapons weapons that wants to be recharged
     */
    private void chooseShootWeapon(final PlayerPosition moveBeforeShoot, final ArrayList<Integer> rechargingWeapons) {
        List<WeaponCard> weaponCards = new ArrayList<>(Arrays.asList(guiManager.getPlayer().getWeapons()));

        if (weaponCards.isEmpty() || weaponCards.stream().noneMatch(w -> w.status() == 0)) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, "Invalid Shoot Action");
            return;
        }

        List<WeaponCard> shootCards = weaponCards.stream().filter(w -> w.status() == 0).collect(Collectors.toList());

        if (rechargingWeapons != null && !rechargingWeapons.isEmpty()) {
            for (Integer weaponIndex : rechargingWeapons) {
                if (weaponCards.get(weaponIndex) != null) {
                    shootCards.add(weaponCards.get(weaponIndex));
                }
            }
        }

        actionPanel.getChildren().clear();

        setActionPanelTitle("Shoot");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        for (WeaponCard weaponCard : shootCards) {
            ImageView img = new ImageView(weaponCard.getImagePath());
            img.getStyleClass().add(CSS_BUTTON);
            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> chooseWeaponEffect(moveBeforeShoot, rechargingWeapons, weaponCard));
            hBox.getChildren().add(img);
        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the choose of weapon effect
     *
     * @param moveBeforeShoot   position of the move before shoot
     * @param rechargingWeapons weapons that wants to be recharged
     * @param weaponCard        weapon chosen
     */
    private void chooseWeaponEffect(PlayerPosition moveBeforeShoot, ArrayList<Integer> rechargingWeapons, WeaponCard weaponCard) {
        actionPanel.getChildren().clear();

        setActionPanelTitle("Choose effect");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        List<Effect> weaponEffects = new ArrayList<>();
        weaponEffects.add(weaponCard.getBaseEffect());
        weaponEffects.addAll(weaponCard.getSecondaryEffects());

        for (int i = 0; i < weaponEffects.size(); ++i) {
            final int weaponEffectIndex = i;

            final Effect weaponEffect = weaponEffects.get(i);
            StackPane effectPane = new StackPane();
            effectPane.getStyleClass().add(CSS_EFFECT_DESC_BACKGROUND);
            effectPane.getStyleClass().add(CSS_BUTTON);
            effectPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int weaponIndex = Arrays.asList(guiManager.getPlayer().getWeapons()).indexOf(weaponCard);

                if (weaponIndex != -1) {
                    ShootRequest.ShootRequestBuilder shootRequestBuilder = new ShootRequest.ShootRequestBuilder(guiManager.getUsername(), guiManager.getClientToken(), weaponIndex, weaponEffectIndex)
                            .moveBeforeShootPosition(moveBeforeShoot);

                    if (rechargingWeapons != null) {
                        shootRequestBuilder.rechargingWeapons(rechargingWeapons);
                    }

                    askShootPaymentPowerups(shootRequestBuilder, weaponEffect);
                }
            });

            Label descLabel = new Label(weaponEffects.get(i).getDescription().strip());
            descLabel.getStyleClass().add(CSS_EFFECT_DESC);
            StackPane.setAlignment(descLabel, Pos.CENTER);
            effectPane.getChildren().add(descLabel);

            Ammo[] effectCost = weaponEffect.getCost().toArray();
            for (int j = 0; j < effectCost.length; j++) {
                ImageView ammoImage = new ImageView("/img/ammo/" + effectCost[j].name().toLowerCase() + "Ammo.png");
                Insets margin = new Insets(MapInsetsHelper.ammoEffectCostInsets.getTop(), 0, 0, MapInsetsHelper.ammoEffectCostInsets.getLeft() - j * MapInsetsHelper.AMMO_EFFECT_COST_HORIZONTAL_OFFSET);
                StackPane.setMargin(ammoImage, margin);
                StackPane.setAlignment(ammoImage, Pos.TOP_LEFT);

                effectPane.getChildren().add(ammoImage);
            }

            hBox.getChildren().add(effectPane);
        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Asks for payment powerups
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param weaponEffect        effect of the weapon
     */
    private void askShootPaymentPowerups(ShootRequest.ShootRequestBuilder shootRequestBuilder, Effect weaponEffect) {
        ArrayList<PowerupCard> powerupCards = new ArrayList<>(guiManager.getPowerups());

        if (powerupCards.isEmpty()) {
            buildShootRequest(shootRequestBuilder, List.of(weaponEffect.getTargets()), weaponEffect.getProperties());
            return;
        }

        actionPanel.getChildren().clear();

        setActionPanelTitle("Powerups Payment");

        setMultiplePowerupSelectLayout(powerupCards);

        setActionPanelBottom();

        HBox botHBox = (HBox) actionPanel.getBottom();
        ImageView nextButton = new ImageView(NEXT_BUTTON_PATH);
        nextButton.getStyleClass().add(CSS_BUTTON);

        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ArrayList<Integer> powerupIndexes = getMultiplePowerupIndexes();

            buildShootRequest(shootRequestBuilder.paymentPowerups(powerupIndexes), List.of(weaponEffect.getTargets()), weaponEffect.getProperties());
        });

        botHBox.getChildren().add(nextButton);

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Builds the shoot request based on effect properties
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     */
    private void buildShootRequest(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties) {
        if (!targets.isEmpty()) {
            switch (targets.get(0)) {
                case PLAYER:
                    askPlayerTargets(shootRequestBuilder, targets, properties);
                    break;
                case SQUARE:
                    onSquareTarget(shootRequestBuilder, targets, properties);
                    break;
                case ROOM:
                    askRoomTarget(shootRequestBuilder, targets, properties);
                    break;
            }

            return;
        }

        if (properties.containsKey(Properties.MOVE.getJKey())) {
            askSenderMove(shootRequestBuilder, properties);
        } else if (properties.containsKey(Properties.MOVE_TO_LAST_TARGET.getJKey())) {
            moveToLastTarget(shootRequestBuilder, properties);
        } else if (properties.containsKey(Properties.MOVE_TARGET.getJKey())) {
            askTargetMovePosition(shootRequestBuilder, properties, true, Integer.parseInt(properties.get(Properties.MOVE_TARGET.getJKey())), 0);
        } else if (properties.containsKey(Properties.MAX_MOVE_TARGET.getJKey())) {
            askTargetMovePosition(shootRequestBuilder, properties, false, Integer.parseInt(properties.get(Properties.MAX_MOVE_TARGET.getJKey())), 0);
        } else {
            sendShootRequest(shootRequestBuilder);
        }
    }

    /**
     * Asks for player targets
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     */
    private void askPlayerTargets(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties) {
        if (properties.containsKey(Properties.TARGET_NUM.getJKey())) {
            askTargets(shootRequestBuilder, targets, properties, Integer.parseInt(properties.get(Properties.TARGET_NUM.getJKey())), true);
        } else if (properties.containsKey(Properties.MAX_TARGET_NUM.getJKey())) {
            askTargets(shootRequestBuilder, targets, properties, Integer.parseInt(properties.get(Properties.MAX_TARGET_NUM.getJKey())), false);
        } else {
            throw new InvalidPropertiesException();
        }
    }

    /**
     * Asks for player targets
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     * @param numberOfTargets     number of targets
     * @param exact               if the number of target needs to be equal to numberOfTargets
     */
    private void askTargets(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties, int numberOfTargets, boolean exact) {
        actionPanel.getChildren().clear();

        ShootRequest tempRequest = shootRequestBuilder.build();

        if (tempRequest.getTargetPlayersUsername() == null) {
            shootRequestBuilder.targetPlayersUsernames(new ArrayList<>());
            tempRequest = shootRequestBuilder.build();
        }

        HBox hBox = setHorizontalLayout("Shoot Target #" + (tempRequest.getTargetPlayersUsername().size() + 1));

        List<Player> players = guiManager.getAllPlayers().stream().filter(p -> !p.getUsername().equals(guiManager.getUsername())).collect(Collectors.toList());

        for (Player player : players) {
            final String currentUsername = player.getUsername();

            ImageView img = new ImageView();
            img.setId(getIconIDFromColor(player.getColor()));
            img.getStyleClass().add(CSS_BUTTON);
            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ShootRequest currTempRequest = shootRequestBuilder.build();

                List<String> targetUsername = currTempRequest.getTargetPlayersUsername();
                targetUsername.add(currentUsername);

                if (targetUsername.size() == numberOfTargets) {
                    List<TargetType> newTargets = (targets.size() == 1) ? List.of() : targets.subList(1, targets.size());

                    buildShootRequest(shootRequestBuilder.targetPlayersUsernames(targetUsername), newTargets, properties);
                } else {
                    askTargets(shootRequestBuilder.targetPlayersUsernames(targetUsername), targets, properties, numberOfTargets, exact);
                }
            });

            hBox.getChildren().add(img);
        }

        setActionPanelBottom();

        if (!exact) {
            addNextButton(shootRequestBuilder, targets, properties);
        }

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles square targets
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     */
    private void onSquareTarget(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties) {
        if (properties.containsKey(Properties.SAME_POSITION.getJKey())) {
            ShootRequest tempReq = shootRequestBuilder.build();

            shootRequestBuilder.targetPositions(new ArrayList<>(List.of(guiManager.getPlayerByName(tempReq.getTargetPlayersUsername().get(0)).getPosition())));

            List<TargetType> newTargets = (targets.size() == 1) ? List.of() : targets.subList(1, targets.size());
            buildShootRequest(shootRequestBuilder, newTargets, properties);
        } else {
            askSquareTargets(shootRequestBuilder, targets, properties);
        }
    }

    /**
     * Asks for square targets
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     */
    private void askSquareTargets(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties) {
        if (properties.containsKey(Properties.TARGET_NUM.getJKey())) {
            askExactSquareTargets(shootRequestBuilder, targets, properties, Integer.parseInt(properties.get(Properties.TARGET_NUM.getJKey())));
        } else if (properties.containsKey(Properties.MAX_TARGET_NUM.getJKey())) {
            askMaxSquareTargets(shootRequestBuilder, targets, properties, Integer.parseInt(properties.get(Properties.MAX_TARGET_NUM.getJKey())));
        } else {
            throw new InvalidPropertiesException();
        }
    }

    /**
     * Asks for exact number square targets
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     * @param numberOfTargets     exact number of target
     */
    private void askExactSquareTargets(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties, int numberOfTargets) {
        actionPanel.getChildren().clear();

        ShootRequest tempRequest = shootRequestBuilder.build();
        if (tempRequest.getTargetPositions() == null) {
            shootRequestBuilder.targetPositions(new ArrayList<>());
            tempRequest = shootRequestBuilder.build();
        }

        setActionPanelTitle("Shoot Square Target #" + (tempRequest.getTargetPositions().size() + 1));

        GameMap gameMap = guiManager.getGameMap();

        PlayerPosition playerPosition = guiManager.getPlayer().getPosition();
        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                PlayerPosition tempPos = new PlayerPosition(x, y);
                Square square = gameMap.getSquare(x, y);

                if (square != null) {
                    Button mapButton = squareTarget(tempPos, playerPosition, shootRequestBuilder, targets, properties, numberOfTargets, true);

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Asks for max number square targets
     *
     * @param shootRequestBuilder builder of the shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     * @param numberOfTargets     exact number of target
     */
    private void askMaxSquareTargets(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties, int numberOfTargets) {
        actionPanel.getChildren().clear();

        ShootRequest tempRequest = shootRequestBuilder.build();
        if (tempRequest.getTargetPositions() == null) {
            shootRequestBuilder.targetPositions(new ArrayList<>());
            tempRequest = shootRequestBuilder.build();
        }

        PlayerPosition playerPosition = guiManager.getPlayer().getPosition();

        setActionPanelTitle("Shoot Square Target #" + (tempRequest.getTargetPositions().size() + 1));

        GameMap gameMap = guiManager.getGameMap();
        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                PlayerPosition tempPos = new PlayerPosition(x, y);
                Square square = gameMap.getSquare(x, y);

                if (square != null) {
                    Button mapButton = squareTarget(tempPos, playerPosition, shootRequestBuilder, targets, properties, numberOfTargets, false);
                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        addNextButton(shootRequestBuilder, targets, properties);

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Creates the button of square target
     *
     * @param tempPos             position of move
     * @param playerPosition      actual player position
     * @param shootRequestBuilder builder of shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     * @param numberOfTargets     number of targets
     * @param exact               {@code true} if is an exact number
     * @return the button created
     */
    private Button squareTarget(PlayerPosition tempPos, PlayerPosition playerPosition, ShootRequest.ShootRequestBuilder shootRequestBuilder,
                                List<TargetType> targets, Map<String, String> properties, int numberOfTargets, boolean exact) {
        Button mapButton = new Button();
        mapButton.getStyleClass().add(tempPos.equals(playerPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

        mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ShootRequest currTempRequest = shootRequestBuilder.build();

            List<PlayerPosition> targetPositions = currTempRequest.getTargetPositions();
            targetPositions.add(tempPos);

            if (targetPositions.size() == numberOfTargets) {
                List<TargetType> newTargets = (targets.size() == 1) ? List.of() : targets.subList(1, targets.size());

                buildShootRequest(shootRequestBuilder.targetPositions(targetPositions), newTargets, properties);
            } else {
                if (exact) {
                    askExactSquareTargets(shootRequestBuilder.targetPositions(targetPositions), targets, properties, numberOfTargets);
                } else {
                    askMaxSquareTargets(shootRequestBuilder.targetPositions(targetPositions), targets, properties, numberOfTargets);
                }
            }
        });

        return mapButton;
    }

    /**
     * Asks for room target
     *
     * @param shootRequestBuilder builder of shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     */
    private void askRoomTarget(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties) {
        actionPanel.getChildren().clear();

        setActionPanelTitle("Shoot Room Target");

        GameMap gameMap = guiManager.getGameMap();
        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);

                if (square != null) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(square.getRoomColor().name().toLowerCase() + "Square");

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        List<TargetType> newTargets = (targets.size() == 1) ? List.of() : targets.subList(1, targets.size());

                        buildShootRequest(shootRequestBuilder.targetRoomColor(square.getRoomColor()), newTargets, properties);
                    });

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Adds the next button to layout
     *
     * @param shootRequestBuilder builder of shoot request
     * @param targets             targets of the effect
     * @param properties          properties of the effect
     */
    private void addNextButton(ShootRequest.ShootRequestBuilder shootRequestBuilder, List<TargetType> targets, Map<String, String> properties) {
        HBox botHBox = (HBox) actionPanel.getBottom();
        ImageView nextButton = new ImageView(NEXT_BUTTON_PATH);
        nextButton.getStyleClass().add(CSS_BUTTON);

        List<TargetType> newTargets = (targets.size() == 1) ? List.of() : targets.subList(1, targets.size());

        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> buildShootRequest(shootRequestBuilder, newTargets, properties));
        botHBox.getChildren().add(nextButton);
    }

    /**
     * Handles move to last target
     *
     * @param shootRequestBuilder builder of shoot request
     * @param properties          properties of the effect
     */
    private void moveToLastTarget(ShootRequest.ShootRequestBuilder shootRequestBuilder, Map<String, String> properties) {
        ShootRequest tempReq = shootRequestBuilder.build();

        if (!tempReq.getTargetPlayersUsername().isEmpty()) {
            shootRequestBuilder.senderMovePosition(
                    guiManager.getPlayerByName(
                            tempReq.getTargetPlayersUsername().get(tempReq.getTargetPlayersUsername().size() - 1)
                    ).getPosition()
            );
        }

        Map<String, String> newProperties = new LinkedHashMap<>(properties);
        newProperties.remove(Properties.MOVE_TO_LAST_TARGET.getJKey());

        shootRequestBuilder.moveToLastTarget(true);
        buildShootRequest(shootRequestBuilder, List.of(), newProperties);
    }

    /**
     * Asks for sender move
     *
     * @param shootRequestBuilder builder of shoot request
     * @param properties          properties of the effect
     */
    private void askSenderMove(ShootRequest.ShootRequestBuilder shootRequestBuilder, Map<String, String> properties) {
        actionPanel.getChildren().clear();

        setActionPanelTitle("Shooter move");

        GameMap gameMap = guiManager.getGameMap();

        ShootRequest tempReq = shootRequestBuilder.build();
        PlayerPosition playerPosition;

        if (tempReq.getMoveBeforeShootPosition() != null) {
            playerPosition = tempReq.getMoveBeforeShootPosition();
        } else {
            playerPosition = guiManager.getPlayer().getPosition();
        }

        AnchorPane anchorPane = new AnchorPane();

        int distance = Integer.parseInt(properties.get(Properties.MOVE.getJKey()));

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                PlayerPosition tempPos = new PlayerPosition(x, y);
                Square square = gameMap.getSquare(x, y);

                if (square != null && tempPos.distanceOf(playerPosition, gameMap) <= distance && tempPos.distanceOf(playerPosition, gameMap) > 0) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(tempPos.equals(playerPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        shootRequestBuilder.senderMovePosition(tempPos);

                        askSenderMoveOrder(shootRequestBuilder, properties, properties.containsKey(Properties.MOVE_IN_MIDDLE.getJKey()));
                    });

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Asks for move order of the sender
     *
     * @param shootRequestBuilder builder of shoot request
     * @param properties          properties of the effect
     * @param middle              {@code true} if midlle is permitted
     */
    private void askSenderMoveOrder(ShootRequest.ShootRequestBuilder shootRequestBuilder, Map<String, String> properties, boolean middle) {
        actionPanel.getChildren().clear();

        setActionPanelTitle("Sender move order");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        List<String> moveOrder;

        if (middle) {
            moveOrder = new ArrayList<>(List.of(BEFORE, MIDDLE, AFTER));
        } else {
            moveOrder = new ArrayList<>(List.of(BEFORE, AFTER));
        }

        for (String move : moveOrder) {
            ImageView img = new ImageView("/img/scenes/" + move + "button.png");
            img.getStyleClass().add(CSS_BUTTON);

            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                switch (move) {
                    case BEFORE:
                        shootRequestBuilder.moveSenderFirst(true);
                        break;
                    case AFTER:
                        shootRequestBuilder.moveSenderFirst(false);
                        break;
                    case MIDDLE:
                        shootRequestBuilder.moveInMiddle(true);
                        break;
                    default:
                }

                Map<String, String> newProperties = new LinkedHashMap<>(properties);
                newProperties.remove(Properties.MOVE.getJKey());

                buildShootRequest(shootRequestBuilder, List.of(), newProperties);
            });

            hBox.getChildren().add(img);
        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Asks for target move position
     *
     * @param shootRequestBuilder builder of shoot request
     * @param properties          properties of the effect
     * @param exactMove           {@code true} if move is exact
     * @param distance            max distance
     * @param targetNum           number of target
     */
    private void askTargetMovePosition(ShootRequest.ShootRequestBuilder shootRequestBuilder, Map<String, String> properties, boolean exactMove, int distance, int targetNum) {
        actionPanel.getChildren().clear();

        ShootRequest tempRequest = shootRequestBuilder.build();

        if (tempRequest.getTargetPlayersMovePositions() == null) {
            shootRequestBuilder.targetPlayersMovePositions(new ArrayList<>());
            tempRequest = shootRequestBuilder.build();
        }

        setActionPanelTitle("Move of " + tempRequest.getTargetPlayersUsername().get(targetNum));

        GameMap gameMap = guiManager.getGameMap();

        PlayerPosition playerPosition = guiManager.getPlayerByName(tempRequest.getTargetPlayersUsername().get(targetNum)).getPosition();
        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                PlayerPosition tempPos = new PlayerPosition(x, y);
                Square square = gameMap.getSquare(x, y);

                if (square != null && ((exactMove && tempPos.distanceOf(playerPosition, gameMap) == distance) ||
                        (!exactMove && tempPos.distanceOf(playerPosition, gameMap) >= 0 && tempPos.distanceOf(playerPosition, gameMap) <= distance))) {
                    Button mapButton = targetMovePosition(tempPos, playerPosition, shootRequestBuilder, properties, exactMove, distance, targetNum);

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Creates the button of target move position
     *
     * @param tempPos             position of move
     * @param playerPosition      actual player position
     * @param shootRequestBuilder builder of shoot request
     * @param properties          properties of the effect
     * @param exactMove           {@code true} if is an exact move
     * @param distance            distance of move
     * @param targetNum           number of target
     * @return the button created
     */
    private Button targetMovePosition(PlayerPosition tempPos, PlayerPosition playerPosition, ShootRequest.ShootRequestBuilder shootRequestBuilder,
                                      Map<String, String> properties, boolean exactMove, int distance, int targetNum) {
        Button mapButton = new Button();
        mapButton.getStyleClass().add(tempPos.equals(playerPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

        mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ShootRequest currTempRequest = shootRequestBuilder.build();

            List<PlayerPosition> targetPlayersMovePositions = currTempRequest.getTargetPlayersMovePositions();
            targetPlayersMovePositions.add(tempPos);

            if (targetPlayersMovePositions.size() == currTempRequest.getTargetPlayersUsername().size()) {

                Map<String, String> newProperties = new LinkedHashMap<>(properties);

                if (exactMove) {
                    newProperties.remove(Properties.MOVE_TARGET.getJKey());
                } else {
                    newProperties.remove(Properties.MAX_MOVE_TARGET.getJKey());
                }

                targetMoveOrder(shootRequestBuilder.targetPlayersMovePositions(targetPlayersMovePositions), newProperties);
            } else {
                askTargetMovePosition(shootRequestBuilder.targetPlayersMovePositions(targetPlayersMovePositions), properties, exactMove, distance, targetNum + 1);
            }
        });

        return mapButton;
    }

    /**
     * Handles the order of move
     *
     * @param shootRequestBuilder builder of shoot request
     * @param properties          properties of the effect
     */
    private void targetMoveOrder(ShootRequest.ShootRequestBuilder shootRequestBuilder, Map<String, String> properties) {
        if (properties.containsKey(Properties.MOVE_TARGET_BEFORE.getJKey())) {
            buildShootRequest(shootRequestBuilder.moveTargetsFirst(Boolean.parseBoolean(properties.get(Properties.MOVE_TARGET_BEFORE.getJKey()))), List.of(), properties);
        } else {
            askTargetMoveOrder(shootRequestBuilder, properties);
        }
    }

    /**
     * Ask the order of move
     *
     * @param shootRequestBuilder builder of shoot request
     * @param properties          properties of the effect
     */
    private void askTargetMoveOrder(ShootRequest.ShootRequestBuilder shootRequestBuilder, Map<String, String> properties) {
        actionPanel.getChildren().clear();

        setActionPanelTitle("Targets move order");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        List<String> moveOrder = new ArrayList<>(List.of(BEFORE, AFTER));

        for (String move : moveOrder) {
            ImageView img = new ImageView("/img/scenes/" + move + "button.png");
            img.getStyleClass().add(CSS_BUTTON);

            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                switch (move) {
                    case BEFORE:
                        shootRequestBuilder.moveTargetsFirst(true);
                        break;
                    case AFTER:
                        shootRequestBuilder.moveTargetsFirst(false);
                        break;
                    default:
                }

                buildShootRequest(shootRequestBuilder, List.of(), properties);
            });

            hBox.getChildren().add(img);
        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Send a shoot request
     *
     * @param shootRequestBuilder builder of shoot request
     */
    private void sendShootRequest(ShootRequest.ShootRequestBuilder shootRequestBuilder) {
        hideActionPanel();

        if (!guiManager.sendRequest(MessageBuilder.buildShootRequest(shootRequestBuilder))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles powerup use
     */
    void powerup() {
        List<PowerupCard> powerupCards = guiManager.getPowerups();

        if (powerupCards.isEmpty()) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, "Invalid powerup action");
            return;
        }

        actionPanel.getChildren().clear();

        HBox hBox = setHorizontalLayout("Powerup Use");

        for (int i = 0; i < powerupCards.size(); i++) {
            if (powerupCards.get(i).getName().equals(GuiManager.TELEPORTER) || powerupCards.get(i).getName().equals(GuiManager.NEWTON)) {
                final int powerupIndex = i;

                ImageView img = new ImageView(powerupCards.get(i).getImagePath());
                img.getStyleClass().add(CSS_BUTTON);
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onPowerupUseClick(powerupIndex));
                hBox.getChildren().add(img);
            }
        }

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles tagback grenade request
     */
    void tagbackGrenade() {
        askMultiplePowerupUsage(ClientGameManager.TAGBACK_GRENADE);
    }

    /**
     * Handle targeting scope request
     */
    void targetingScope() {
        askMultiplePowerupUsage(ClientGameManager.TARGETING_SCOPE);
    }

    /**
     * Asks for multiple poweup usage
     *
     * @param powerupName name of the powerup
     */
    private void askMultiplePowerupUsage(String powerupName) {
        List<PowerupCard> powerupCards = new ArrayList<>(guiManager.getPowerups());

        actionPanel.getChildren().clear();

        setActionPanelTitle(powerupName);

        setMultiplePowerupSelectLayout(powerupCards.stream().filter(p -> p.getName().equals(powerupName)).collect(Collectors.toList()));

        setActionPanelBottom();

        HBox botHBox = (HBox) actionPanel.getBottom();
        ImageView nextButton = new ImageView(NEXT_BUTTON_PATH);
        nextButton.getStyleClass().add(CSS_BUTTON);

        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ArrayList<Integer> powerupIndexes = getMultiplePowerupIndexes();

            PowerupRequest.PowerupRequestBuilder powerupRequestBuilder =
                    new PowerupRequest.PowerupRequestBuilder(guiManager.getUsername(), guiManager.getClientToken(), powerupIndexes);

            if (powerupName.equals(ClientGameManager.TAGBACK_GRENADE)) {
                if (powerupIndexes.isEmpty()) {
                    hideActionPanel();
                    passTurn();
                } else {
                    sendPowerupRequest(powerupRequestBuilder);
                }
            } else {
                if (powerupIndexes.isEmpty()) {
                    sendPowerupRequest(powerupRequestBuilder);
                } else {
                    onScopeClick(powerupRequestBuilder);
                }
            }
        });

        botHBox.getChildren().add(nextButton);

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the click on a powerup
     *
     * @param powerupIndex index of the powerup
     */
    private void onPowerupUseClick(int powerupIndex) {
        PowerupRequest.PowerupRequestBuilder powerupRequestBuilder =
                new PowerupRequest.PowerupRequestBuilder(guiManager.getUsername(), guiManager.getClientToken(), new ArrayList<>(List.of(powerupIndex)));

        PowerupCard powerupCard = guiManager.getPowerups().get(powerupIndex);

        Effect baseEffect = powerupCard.getBaseEffect();
        Map<String, String> effectProperties = baseEffect.getProperties();


        if (effectProperties.containsKey(Properties.TP.getJKey())) {
            onTeleporterClick(powerupRequestBuilder);
        } else {
            onNewtonClick(powerupRequestBuilder);
        }
    }

    /**
     * Handles the click on a newton
     *
     * @param powerupRequestBuilder builder of powerup request
     */
    private void onNewtonClick(PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) {
        List<Player> players = guiManager.getAllPlayers();
        players = players.stream().filter(p -> p.getPosition() != null && !p.getUsername().equals(guiManager.getUsername())).collect(Collectors.toList());

        if (players.isEmpty()) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, "No suitable players to use Newton");
            return;
        }

        HBox hBox = setHorizontalLayout("Newton Target");

        for (Player player : players) {
            ImageView img = new ImageView();
            img.getStyleClass().add(CSS_BUTTON);
            img.setId(getIconIDFromColor(player.getColor()));
            img.getProperties().put(USERNAME_PROPERTY, player.getUsername());

            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ImageView imageView = (ImageView) event.getTarget();
                String targetUsername = (String) imageView.getProperties().get(USERNAME_PROPERTY);
                askNewtonMovePosition(powerupRequestBuilder.targetPlayersUsername(new ArrayList<>(List.of(targetUsername))));
            });

            hBox.getChildren().add(img);
        }

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Asks for newton move positions
     *
     * @param powerupRequestBuilder builder of powerup request
     */
    private void askNewtonMovePosition(PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) {
        PowerupRequest tempReq = powerupRequestBuilder.build();
        setActionPanelTitle("Newton " + tempReq.getTargetPlayersUsername().get(0) + " move");
        GameMap gameMap = guiManager.getGameMap();
        PlayerPosition playerPosition = guiManager.getPlayerByName(tempReq.getTargetPlayersUsername().get(0)).getPosition();

        AnchorPane anchorPane = new AnchorPane();

        List<PlayerPosition> directionalPositions = getDirectionalMove(gameMap, playerPosition, 2);

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                PlayerPosition tempPos = new PlayerPosition(x, y);

                if (square != null && directionalPositions.contains(tempPos)) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(CSS_SQUARE_CLICK_BUTTON);

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> sendPowerupRequest(powerupRequestBuilder.targetPlayersMovePositions(new ArrayList<>(List.of(tempPos)))));

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the click on a teleport
     *
     * @param powerupRequestBuilder builder of powerup request
     */
    private void onTeleporterClick(PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) {
        setActionPanelTitle("Teleporter");
        GameMap gameMap = guiManager.getGameMap();
        PlayerPosition playerPosition = guiManager.getPlayer().getPosition();

        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                PlayerPosition tempPos = new PlayerPosition(x, y);

                if (square != null && tempPos.distanceOf(playerPosition, gameMap) > 0) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(tempPos.equals(playerPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> sendPowerupRequest(powerupRequestBuilder.senderMovePosition(tempPos)));

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the click on a scope
     *
     * @param powerupRequestBuilder builder of powerup request
     */
    private void onScopeClick(PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) {
        actionPanel.getChildren().clear();

        PowerupRequest tempRequest = powerupRequestBuilder.build();

        if (tempRequest.getTargetPlayersUsername() == null) {
            powerupRequestBuilder.targetPlayersUsername(new ArrayList<>());
            tempRequest = powerupRequestBuilder.build();
        }

        setActionPanelTitle("Scope Target #" + (tempRequest.getTargetPlayersUsername().size() + 1));

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        for (Player player : guiManager.getAllPlayers()) {
            final String currentUsername = player.getUsername();
            if (!currentUsername.equals(guiManager.getUsername())) {
                ImageView img = new ImageView();
                img.setId(getIconIDFromColor(player.getColor()));
                img.getStyleClass().add(CSS_BUTTON);
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    PowerupRequest currTempRequest = powerupRequestBuilder.build();

                    List<String> targetUsernames = currTempRequest.getTargetPlayersUsername();
                    targetUsernames.add(currentUsername);

                    if (currTempRequest.getPowerup().size() == targetUsernames.size()) {
                        askScopePaymentPowerups(powerupRequestBuilder.targetPlayersUsername(targetUsernames));
                    } else {
                        onScopeClick(powerupRequestBuilder.targetPlayersUsername(targetUsernames));
                    }
                });

                hBox.getChildren().add(img);
            }
        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Asks for scope payments poweups
     *
     * @param powerupRequestBuilder powerup request builder
     */
    private void askScopePaymentPowerups(PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) {
        ArrayList<PowerupCard> powerupCards = new ArrayList<>(guiManager.getPowerups());

        PowerupRequest tempRequest = powerupRequestBuilder.build();
        List<Integer> powerupIndexes = tempRequest.getPowerup();

        for (int i = powerupIndexes.size() - 1; i >= 0; --i) {
            int index = powerupIndexes.get(i);
            powerupCards.remove(index);
        }

        if (powerupCards.isEmpty()) {
            askScopeAmmoColor(powerupRequestBuilder);
            return;
        }

        ImageView nextButton = setPaymentPowerupsLayout("Scope payment Powerups", powerupCards);

        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ArrayList<Integer> paymentPowerups = getMultiplePowerupIndexes();
            PowerupRequest currPowerupRequest = powerupRequestBuilder.build();

            if (currPowerupRequest.getPowerup().size() == paymentPowerups.size()) {
                sendPowerupRequest(powerupRequestBuilder.paymentPowerups(paymentPowerups));
            } else {
                askScopeAmmoColor(powerupRequestBuilder.paymentPowerups(paymentPowerups));
            }
        });

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Asks for ammo of scope paymeny
     *
     * @param powerupRequestBuilder builder of powerup request
     */
    private void askScopeAmmoColor(PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) {
        actionPanel.getChildren().clear();

        PowerupRequest tempRequest = powerupRequestBuilder.build();

        if (tempRequest.getAmmoColor() == null) {
            powerupRequestBuilder.ammoColor(new ArrayList<>());
            tempRequest = powerupRequestBuilder.build();
        }

        setActionPanelTitle("Scope Ammo Color #" + (tempRequest.getTargetPlayersUsername().size() + 1));

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        List<Ammo> ammoList = List.of(Ammo.RED, Ammo.BLUE, Ammo.YELLOW);

        for (Ammo ammo : ammoList) {
            ImageView img = new ImageView("/img/ammo/" + ammo.name().toLowerCase() + "Ammo.png");
            img.getStyleClass().add(CSS_BUTTON);

            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                PowerupRequest currTempRequest = powerupRequestBuilder.build();

                List<Ammo> ammoColorList = currTempRequest.getAmmoColor();
                ammoColorList.add(ammo);

                if (currTempRequest.getPowerup().size() == (ammoColorList.size() + currTempRequest.getPaymentPowerups().size())) {
                    sendPowerupRequest(powerupRequestBuilder.ammoColor(ammoColorList));
                } else {
                    askScopeAmmoColor(powerupRequestBuilder.ammoColor(ammoColorList));
                }
            });

            hBox.getChildren().add(img);

        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Sends powerup request
     **/
    private void sendPowerupRequest(PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) {
        hideActionPanel();

        if (!guiManager.sendRequest(MessageBuilder.buildPowerupRequest(powerupRequestBuilder))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles reload action
     */
    void reload() {
        actionPanel.getChildren().clear();

        List<WeaponCard> weapons = new ArrayList<>(Arrays.asList(guiManager.getPlayer().getWeapons()));

        setActionPanelTitle("Reload Weapons");

        setReloadLayout(weapons);

        setActionPanelBottom();

        HBox botHBox = (HBox) actionPanel.getBottom();
        ImageView nextButton = new ImageView(NEXT_BUTTON_PATH);
        nextButton.getStyleClass().add(CSS_BUTTON);

        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> reloadPaymentPowerups(getReloadWeaponIndexes()));
        botHBox.getChildren().add(nextButton);

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Sets the reload layout for picking weapons for reload
     *
     * @param weapons list of reloadable weapons
     */
    private void setReloadLayout(List<WeaponCard> weapons) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        for (WeaponCard weaponCard : weapons) {
            if (weaponCard.status() == 1) {
                CheckBox weapCheckbox = new CheckBox();
                weapCheckbox.getStyleClass().add(CSS_CHECKBOX_IMAGE);
                weapCheckbox.setStyle("-fx-graphic:  url('" + weaponCard.getImagePath() + "')");

                ColorAdjust monochrome = new ColorAdjust();
                monochrome.setSaturation(-1);
                weapCheckbox.setEffect(monochrome);

                weapCheckbox.setUserData(weaponCard);

                StackPane.setAlignment(weapCheckbox, Pos.CENTER);
                hBox.getChildren().add(weapCheckbox);
            }
        }

        actionPanel.setCenter(vBox);
    }

    /**
     * Gets the weapons selected indexes
     *
     * @return List of indexes of selected elements
     */
    private ArrayList<Integer> getReloadWeaponIndexes() {
        List<WeaponCard> weaponCards = new ArrayList<>(Arrays.asList(guiManager.getPlayer().getWeapons()));
        ArrayList<Integer> reloadWeapons = new ArrayList<>();

        VBox centerVBox = (VBox) actionPanel.getCenter();
        HBox centerHBox = (HBox) centerVBox.getChildren().get(0);

        for (Node check : centerHBox.getChildren()) {
            CheckBox childrenCheck = (CheckBox) check;

            if (childrenCheck.isSelected()) {
                WeaponCard weaponCard = (WeaponCard) childrenCheck.getUserData();

                int weapIndex = weaponCards.indexOf(weaponCard);

                if (weapIndex != -1) {
                    reloadWeapons.add(weapIndex);
                }
            }
        }

        return reloadWeapons;
    }

    /**
     * Asks for reload payment powerups
     *
     * @param reloadWeapons list of weapons to recharge
     */
    private void reloadPaymentPowerups(ArrayList<Integer> reloadWeapons) {
        ArrayList<PowerupCard> powerupCards = new ArrayList<>(guiManager.getPowerups());

        if (powerupCards.isEmpty()) {
            ReloadRequest reloadRequest;

            try {
                reloadRequest = MessageBuilder.buildReloadRequest(guiManager.getClientToken(), guiManager.getPlayer(), reloadWeapons);
            } catch (WeaponCardsNotFoundException e) {
                GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, e.getMessage());
                return;
            }

            hideActionPanel();

            if (!guiManager.sendRequest(reloadRequest)) {
                GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
            }

            return;
        }

        ImageView nextButton = setPaymentPowerupsLayout("Reload Payment Powerups", powerupCards);
        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ArrayList<Integer> paymentPowerups = getMultiplePowerupIndexes();

            hideActionPanel();
            ReloadRequest reloadRequest;

            try {
                if (paymentPowerups.isEmpty()) {
                    reloadRequest = MessageBuilder.buildReloadRequest(guiManager.getClientToken(), guiManager.getPlayer(), reloadWeapons);
                } else {
                    reloadRequest = MessageBuilder.buildReloadRequest(guiManager.getClientToken(), guiManager.getPlayer(), reloadWeapons, paymentPowerups);
                }
            } catch (WeaponCardsNotFoundException | PowerupCardsNotFoundException e) {
                GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, e.getMessage());
                return;
            }

            hideActionPanel();

            if (!guiManager.sendRequest(reloadRequest)) {
                GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
            }
        });

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Sets the layout for select multiple powerups
     *
     * @param powerupCards list of powerups selectable
     */
    private void setMultiplePowerupSelectLayout(List<PowerupCard> powerupCards) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        for (PowerupCard powerupCard : powerupCards) {
            CheckBox powCheckbox = new CheckBox();
            powCheckbox.getStyleClass().add(CSS_CHECKBOX_IMAGE);
            powCheckbox.setStyle("-fx-graphic:  url('" + powerupCard.getImagePath() + "')");
            powCheckbox.setUserData(powerupCard);

            StackPane.setAlignment(powCheckbox, Pos.CENTER);
            hBox.getChildren().add(powCheckbox);
        }

        actionPanel.setCenter(vBox);
    }

    /**
     * Gets all selected powerups from layout
     *
     * @return the list of selected powerups
     */
    private ArrayList<Integer> getMultiplePowerupIndexes() {
        List<PowerupCard> powerupCards = guiManager.getPowerups();
        ArrayList<Integer> paymentPowerups = new ArrayList<>();

        VBox centerVBox = (VBox) actionPanel.getCenter();
        HBox centerHBox = (HBox) centerVBox.getChildren().get(0);

        for (Node check : centerHBox.getChildren()) {
            CheckBox childrenCheck = (CheckBox) check;

            if (childrenCheck.isSelected()) {
                PowerupCard powerupCard = (PowerupCard) childrenCheck.getUserData();

                int powIndex = powerupCards.indexOf(powerupCard);

                if (powIndex != -1) {
                    paymentPowerups.add(powIndex);
                }
            }
        }

        return paymentPowerups;
    }

    /**
     * Sets the powerup payment layout
     *
     * @param title        title of the layout
     * @param powerupCards List of powerups for payment
     * @return the next button
     */
    private ImageView setPaymentPowerupsLayout(String title, List<PowerupCard> powerupCards) {
        actionPanel.getChildren().clear();

        setActionPanelTitle(title);

        setMultiplePowerupSelectLayout(powerupCards);

        setActionPanelBottom();

        HBox botHBox = (HBox) actionPanel.getBottom();
        ImageView nextButton = new ImageView(NEXT_BUTTON_PATH);
        nextButton.getStyleClass().add(CSS_BUTTON);

        botHBox.getChildren().add(nextButton);

        return nextButton;
    }

    /**
     * Handles the bot action
     */
    void botAction() {
        setActionPanelTitle("Bot Move");
        GameMap gameMap = guiManager.getGameMap();
        PlayerPosition botPosition = guiManager.getPlayerByName(GameConstants.BOT_NAME).getPosition();

        AnchorPane anchorPane = new AnchorPane();

        for (int y = 0; y < GameMap.MAX_COLUMNS; ++y) {
            for (int x = 0; x < GameMap.MAX_ROWS; ++x) {
                Square square = gameMap.getSquare(x, y);
                PlayerPosition tempPos = new PlayerPosition(x, y);

                if (square != null && tempPos.distanceOf(botPosition, gameMap) <= 1 && tempPos.distanceOf(botPosition, gameMap) >= 0) {
                    Button mapButton = new Button();
                    mapButton.getStyleClass().add(tempPos.equals(botPosition) ? CSS_SQUARE_OWNER_CLICK_BUTTON : CSS_SQUARE_CLICK_BUTTON);

                    mapButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleBotMove(tempPos));

                    AnchorPane.setLeftAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getLeft() + y * MapInsetsHelper.SQUARE_BUTTON_HORIZONTAL_OFFSET);
                    AnchorPane.setTopAnchor(mapButton, MapInsetsHelper.squareButtonInsets.getTop() + x * MapInsetsHelper.SQUARE_BUTTON_VERTICAL_OFFSET);

                    anchorPane.getChildren().add(mapButton);
                }
            }
        }

        actionPanel.setCenter(anchorPane);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Handles the move of the bot
     *
     * @param tempPos position of move
     */
    private void handleBotMove(PlayerPosition tempPos) {
        List<UserPlayer> players = guiManager.getPlayers().stream().filter(p -> !p.getUsername().equals(guiManager.getUsername())).collect(Collectors.toList());
        List<UserPlayer> visiblePlayers = new ArrayList<>();

        for (UserPlayer player : players) {
            if (player.getPosition() != null && tempPos.canSee(player.getPosition(), guiManager.getGameMap())) {
                visiblePlayers.add(player);
            }
        }

        if (visiblePlayers.isEmpty()) {
            sendBotAction(tempPos, null);
        } else {
            chooseBotTarget(tempPos, visiblePlayers);
        }
    }

    /**
     * Choose bot target
     *
     * @param movePosition   position of bot move
     * @param visiblePlayers list of player that bot can see
     */
    private void chooseBotTarget(PlayerPosition movePosition, List<UserPlayer> visiblePlayers) {
        actionPanel.getChildren().clear();

        setActionPanelTitle("Bot Target");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        for (UserPlayer player : visiblePlayers) {
            ImageView img = new ImageView();
            img.setId(getIconIDFromColor(player.getColor()));
            img.getStyleClass().add(CSS_BUTTON);

            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> sendBotAction(movePosition, player));

            hBox.getChildren().add(img);
        }

        actionPanel.setCenter(vBox);

        setActionPanelBottom();

        setBoardOpaque(OPAQUE);
        actionPanel.setVisible(true);
        actionPanel.toFront();
    }

    /**
     * Sends the bot action
     *
     * @param movePosition position of bot move
     * @param target       target of bot
     */
    private void sendBotAction(PlayerPosition movePosition, UserPlayer target) {
        hideActionPanel();

        if (!guiManager.sendRequest(MessageBuilder.buildUseTerminatorRequest(guiManager.getPlayer(), guiManager.getClientToken(), movePosition, target))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, GuiManager.SEND_ERROR);
        }
    }

    /**
     * Sets the horizontal layout for add center horizontal layout element
     *
     * @param title title of the layout
     * @return return the HBox
     */
    private HBox setHorizontalLayout(String title) {
        actionPanel.getChildren().clear();

        setActionPanelTitle(title);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        actionPanel.setCenter(vBox);

        return hBox;
    }

    /**
     * Gets the list of directional positions
     *
     * @param gameMap        map used
     * @param startingSquare starting square
     * @param distance       distance of the directional move
     * @return a list of directional positions
     */
    private List<PlayerPosition> getDirectionalMove(GameMap gameMap, PlayerPosition startingSquare, int distance) {
        List<PlayerPosition> returnPositions = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Square mySquare = gameMap.getSquare(startingSquare);
            PlayerPosition myPos = new PlayerPosition(startingSquare);

            for (int j = 0; j < distance; j++) {
                mySquare = directionalSwitch(mySquare, myPos, returnPositions, gameMap, i);
            }
        }

        return returnPositions;
    }

    /**
     * Explore a direction
     *
     * @param mySquare        square of start
     * @param myPos           position of start
     * @param returnPositions position of retun
     * @param gameMap         map of the game
     * @param i               direction
     */
    private Square directionalSwitch(Square mySquare, PlayerPosition myPos, List<PlayerPosition> returnPositions, GameMap gameMap, int i) {
        switch (i) {
            case 0:
                if (!mySquare.getNorth().equals(SquareAdjacency.WALL)) {
                    myPos.setRow(myPos.getRow() - 1);
                    returnPositions.add(new PlayerPosition(myPos));
                    mySquare = gameMap.getSquare(myPos);
                }
                break;

            case 1:
                if (!mySquare.getEast().equals(SquareAdjacency.WALL)) {
                    myPos.setColumn(myPos.getColumn() + 1);
                    returnPositions.add(new PlayerPosition(myPos));
                    mySquare = gameMap.getSquare(myPos);
                }
                break;

            case 2:
                if (!mySquare.getSouth().equals(SquareAdjacency.WALL)) {
                    myPos.setRow(myPos.getRow() + 1);
                    returnPositions.add(new PlayerPosition(myPos));
                    mySquare = gameMap.getSquare(myPos);
                }
                break;

            case 3:
                if (!mySquare.getWest().equals(SquareAdjacency.WALL)) {
                    myPos.setColumn(myPos.getColumn() - 1);
                    returnPositions.add(new PlayerPosition(myPos));
                    mySquare = gameMap.getSquare(myPos);
                }
                break;

            default:
        }

        return mySquare;
    }

    /**
     * Communicates the disconnection of a player
     *
     * @param player username of a player who disconnected
     */
    void onPlayerDisconnect(String player) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Disconnection", player + " disconnected from the server");
    }

    /**
     * Handles the game end
     *
     * @param players player of the leaderboard
     */
    void onGameEnd(List<PlayerPoints> players) {
        EndGameSceneController endGameSceneController = GuiManager.setLayout(mainPane.getScene(), "fxml/endGameScene.fxml");

        if (endGameSceneController != null) {
            endGameSceneController.setData(players);
        }
    }

    /**
     * Handles the disconnection
     */
    void onDisconnection() {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Disconnection", "You were disconnected from the server");
    }
}
