package view.cli;

import enumerations.Ammo;
import enumerations.PlayerColor;
import enumerations.SquareAdjacency;
import enumerations.SquareType;
import model.Game;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.player.Player;
import model.map.*;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CliPrinter {

    private CliPrinter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prints the PlayerBoards to System.out
     *
     * @param out            PrintStream where to print
     * @param gameSerialized status of the game
     */
    static void printPlayerBoards(AdrenalinePrintStream out, GameSerialized gameSerialized) {
        for (Player player : gameSerialized.getPlayers()) {
            printPlayerBoard(out, player, gameSerialized);
        }
    }

    /**
     * Prints a single PlayerBoard
     *
     * @param out            PrintStream where to print
     * @param player         PlayerBoard owner
     * @param gameSerialized status of the game
     */
    private static void printPlayerBoard(AdrenalinePrintStream out, Player player, GameSerialized gameSerialized) {
        String markString = getMarksString(player, gameSerialized);
        String damageString = getDamageString(player, gameSerialized);
        String playerColor = getPlayerColorCode(player, gameSerialized, true) + AnsiCode.TEXT_BLACK;

        String playerBoard = getPlayerBoardRow(playerColor, "┌─────────────────────────────────────────────────────────────────────────────────────┐") +
                getPlayerBoardRow(playerColor, "│                                             ┌─────────────────────────────────────┐ │") +
                getPlayerBoardRow(playerColor, "│                                             │" + markString + " │ │") +
                getPlayerBoardRow(playerColor, "│                                             └─────────────────────────────────────┘ │") +
                getPlayerBoardRow(playerColor, "│ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ │") +
                getPlayerBoardRow(playerColor, "│ " + damageString + "│") +
                getPlayerBoardRow(playerColor, "│ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ │") +
                getPlayerBoardRow(playerColor, "└─────────────────────────────────────────────────────────────────────────────────────┘");

        out.println(playerBoard);
    }

    private static String getPlayerBoardRow(String playerColor, String content) {
        return playerColor + content + AnsiCode.RESET + "\n";
    }

    private static String getMarksString(Player player, GameSerialized gameSerialized) {
        Map<String, PlayerColor> playerColorMap = getPlayerColorMap(gameSerialized);

        String playerBackgroundColor = getPlayerColorCode(player, gameSerialized, true);
        List<String> markDealers = player.getPlayerBoard().getMarks();
        StringBuilder marksStringBuilder = new StringBuilder();
        int count = 0;

        for (String markDealer : markDealers) {
            String color = AnsiCode.getTextColorCodeByName(playerColorMap.get(markDealer).name(), true);
            marksStringBuilder.append(" ").append(color).append("  ").append(playerBackgroundColor);
            ++count;
        }

        for (; count < 12; ++count) {
            marksStringBuilder.append("   ");
        }

        return marksStringBuilder.toString();
    }

    private static String getDamageString(Player player, GameSerialized gameSerialized) {
        Map<String, PlayerColor> playerColorMap = getPlayerColorMap(gameSerialized);

        String playerBackgroundColor = getPlayerColorCode(player, gameSerialized, true);
        List<String> damageDealers = player.getPlayerBoard().getDamages();
        StringBuilder damageStringBuilder = new StringBuilder();
        int count = 0;

        for (String damageDealer : damageDealers) {
            String color = AnsiCode.getTextColorCodeByName(playerColorMap.get(damageDealer).name(), true);
            damageStringBuilder.append("│ ").append(color).append("  ").append(playerBackgroundColor).append(" │ ");
            ++count;
        }

        for (; count < 12; ++count) {
            damageStringBuilder.append("│    │ ");
        }

        return damageStringBuilder.toString();
    }

    /**
     * Generates a map with each player as key and respective PlayerColor as value
     *
     * @param gameSerialized status of the game
     * @return the colors map
     */
    private static Map<String, PlayerColor> getPlayerColorMap(GameSerialized gameSerialized) {
        Map<String, PlayerColor> playerColorMap = new HashMap<>();

        for (Player player : gameSerialized.getPlayers()) {
            playerColorMap.put(player.getUsername(), player.getColor());
        }

        return playerColorMap;
    }

    /**
     * From a player returns the color code of his color
     *
     * @param player         desired player
     * @param gameSerialized status of the game
     * @param background     {@code true} for background color, {@code false} for the text color
     * @return the Ansi Color Code of the player
     */
    private static String getPlayerColorCode(Player player, GameSerialized gameSerialized, boolean background) {
        Map<String, PlayerColor> playerColorMap = getPlayerColorMap(gameSerialized);

        return AnsiCode.getTextColorCodeByName(playerColorMap.get(player.getUsername()).name(), background);
    }

    /**
     * Prints the game map
     *
     * @param out            PrintStream where to print
     * @param gameSerialized status of the game
     */
    static void printMap(AdrenalinePrintStream out, GameSerialized gameSerialized) {
        GameMap map = gameSerialized.getGameMap();

        for (int i = 0; i < GameMap.MAX_ROWS; ++i) {
            printMapRow(out, map.getRooms()[i], gameSerialized);
        }
    }

    private static void printMapRow(AdrenalinePrintStream out, Square[] squareRow, GameSerialized gameSerialized) {
        ArrayList<Player> inGamePlayers = gameSerialized.getAllPlayers();

        out.print(
                        getSquareTopRow(squareRow) +
                        getSquareTopDecoration(squareRow) +

                        getSquareMidDecoration(squareRow) +
                        getPlayerDecoration(squareRow, gameSerialized, inGamePlayers) +
                        getSquareMidType(squareRow) +
                        getPlayerDecoration(squareRow, gameSerialized, inGamePlayers) +
                        getSquareMidDecoration(squareRow) +

                        getSquareBotDecoration(squareRow) +
                        getSquareBotRow(squareRow)
                  );
    }

    private static String getSquareTopRow(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
                row.append(roomColor).append(getTopLeft(square)).append(getTopMiddle(square)).append(getTopRight(square)).append(AnsiCode.RESET).append(" ");
            } else {
                row.append(AnsiCode.RESET).append("                ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static String getTopLeft(Square square) {
        String left;

        if (square.getNorth() == SquareAdjacency.SQUARE) {
            left = "╔    ";
        } else if (square.getNorth() == SquareAdjacency.DOOR) {
            left = "╔═══ ";
        } else {
            left = "╔════";
        }

        return left;
    }

    private static String getTopMiddle(Square square) {
        String middle;

        if (square.getNorth() == SquareAdjacency.WALL) {
            middle = "═════";
        } else {
            middle = "     ";
        }

        return middle;
    }

    private static String getTopRight(Square square) {
        String right;

        if (square.getNorth() == SquareAdjacency.SQUARE) {
            right = "    ╗";
        } else if (square.getNorth() == SquareAdjacency.DOOR) {
            right = " ═══╗";
        } else {
            right = "════╗";
        }

        return right;
    }

    private static String getSquareTopDecoration(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                row.append(getFirstTopDecoration(square)).append("             ").append(getSecondTopDecoration(square));
            } else {
                row.append(AnsiCode.RESET).append("                ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static StringBuilder getFirstTopDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();
        String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;

        if (square.getNorth() == SquareAdjacency.SQUARE && square.getWest() == SquareAdjacency.SQUARE) {
            tempRow.append(" ");
        } else if (square.getWest() == SquareAdjacency.SQUARE) {
            tempRow.append(roomColor).append("╝").append(AnsiCode.RESET);
        } else {
            tempRow.append(roomColor).append("║").append(AnsiCode.RESET);
        }

        return tempRow;
    }

    private static StringBuilder getSecondTopDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();
        String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;

        if (square.getNorth() == SquareAdjacency.SQUARE && square.getEast() == SquareAdjacency.SQUARE) {
            tempRow.append("  ");
        } else if (square.getEast() == SquareAdjacency.SQUARE) {
            tempRow.append(roomColor).append("╚═").append(AnsiCode.RESET);
        } else {
            tempRow.append(roomColor).append("║").append(AnsiCode.RESET).append(" ");
        }

        return tempRow;
    }

    private static String getSquareMidDecoration(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                row.append(getFirstMidDecoration(square)).append("             ").append(getSecondMidDecoration(square));
            } else {
                row.append(AnsiCode.RESET).append("                ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static StringBuilder getFirstMidDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();

        if (square.getWest() == SquareAdjacency.SQUARE) {
            tempRow.append(" ");
        } else {
            String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
            tempRow.append(roomColor).append("║").append(AnsiCode.RESET);
        }

        return tempRow;
    }

    private static StringBuilder getSecondMidDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();

        if (square.getEast() == SquareAdjacency.SQUARE) {
            tempRow.append("  ");
        } else {
            String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
            tempRow.append(roomColor).append("║").append(AnsiCode.RESET).append(" ");
        }

        return tempRow;
    }

    private static String getPlayerDecoration(Square[] squareRow, GameSerialized gameSerialized, ArrayList<Player> inGamePlayers) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                row.append(getLeftMidDecoration(square)).append(getMidPlayerDecoration(square, gameSerialized, inGamePlayers)).append(getRightMidDecoration(square));
            } else {
                row.append(AnsiCode.RESET).append("                ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static StringBuilder getLeftMidDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();

        if (square.getWest() == SquareAdjacency.WALL) {
            String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
            tempRow.append(roomColor).append("║").append(AnsiCode.RESET).append("  ");
        } else {
            tempRow.append("    ");
        }

        return tempRow;
    }

    private static StringBuilder getMidPlayerDecoration(Square square, GameSerialized gameSerialized, ArrayList<Player> inGamePlayers) {
        StringBuilder tempRow = new StringBuilder();
        ArrayList<Player> thisSquarePlayers = new ArrayList<>();

        for (Player player : inGamePlayers) {
            if (player.getPosition() != null && gameSerialized.getGameMap().getSquare(player.getPosition()).equals(square) && thisSquarePlayers.size() < 4) {
                thisSquarePlayers.add(player);
            }
        }

        for (int i = 0; i < 3; ++i) {
            if (i < thisSquarePlayers.size()) {
                PlayerColor tempPlayerColor = thisSquarePlayers.get(i).getColor();
                tempRow.append(AnsiCode.getTextColorCodeByName(tempPlayerColor.name(), true)).append("  ").append(AnsiCode.RESET).append(" ");
                inGamePlayers.remove(thisSquarePlayers.get(i));
            } else {
                tempRow.append("   ");
            }
        }

        return tempRow;
    }

    private static StringBuilder getRightMidDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();

        if (square.getEast() == SquareAdjacency.WALL) {
            String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
            tempRow.append("  ").append(roomColor).append("║").append(AnsiCode.RESET).append(" ");
        } else {
            tempRow.append("   ");
        }

        return tempRow;
    }

    private static String getSquareMidType(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                row.append(getLeftMidDecoration(square).append(getMidCentre(square)).append(getRightMidDecoration(square)));
            } else {
                row.append(AnsiCode.RESET).append("                ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static String getMidCentre(Square square) {
        String midCentre;

        if (square.getSquareType() == SquareType.SPAWN) {
            midCentre = "  SPAWN  ";
        } else { // TODO add isAmmoTilePresent as the AMMO identifier would disappear
            midCentre = "  AMMO   ";
        }

        return midCentre;
    }

    private static String getSquareBotDecoration(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for(Square square : squareRow) {
            if(square != null) {
                row.append(getFirstBotDecoration(square)).append("             ").append(getSecondBotDecoration(square));
            } else {
                row.append(AnsiCode.RESET).append("                ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static StringBuilder getFirstBotDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();
        String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;

        if (square.getSouth() == SquareAdjacency.SQUARE && square.getWest() == SquareAdjacency.SQUARE) {
            tempRow.append("  ");
        } else if(square.getWest() == SquareAdjacency.SQUARE) {
            tempRow.append(roomColor).append("╗").append(AnsiCode.RESET);
        } else {
            tempRow.append(roomColor).append("║").append(AnsiCode.RESET);
        }

        return tempRow;
    }

    private static StringBuilder getSecondBotDecoration(Square square) {
        StringBuilder tempRow = new StringBuilder();
        String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;

        if (square.getSouth() == SquareAdjacency.SQUARE && square.getEast() == SquareAdjacency.SQUARE) {
            tempRow.append(" ");
        } else if (square.getEast() == SquareAdjacency.SQUARE) {
            tempRow.append(roomColor).append("╔═").append(AnsiCode.RESET);
        } else {
            tempRow.append(roomColor).append("║").append(AnsiCode.RESET).append(" ");
        }

        return tempRow;
    }

    private static String getSquareBotRow(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
                row.append(roomColor).append(getBotLeft(square)).append(getBotMiddle(square)).append(getBotRight(square)).append(AnsiCode.RESET).append(" ");
            } else {
                row.append(AnsiCode.RESET).append("                ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static String getBotLeft(Square square) {
        String left;

        if (square.getSouth() == SquareAdjacency.SQUARE) {
            left = "╚    ";
        } else if (square.getSouth() == SquareAdjacency.DOOR) {
            left = "╚═══ ";
        } else {
            left = "╚════";
        }

        return left;
    }

    private static String getBotMiddle(Square square) {
        String middle;

        if (square.getSouth() == SquareAdjacency.WALL) {
            middle = "═════";
        } else {
            middle = "     ";
        }

        return middle;
    }

    private static String getBotRight(Square square) {
        String right;

        if (square.getSouth() == SquareAdjacency.SQUARE) {
            right = "    ╝";
        } else if (square.getSouth() == SquareAdjacency.DOOR) {
            right = " ═══╝";
        } else {
            right = "════╝";
        }

        return right;
    }

    public static String toStringPowerUpCard(PowerupCard powerupCard) {
        String color = powerupCard.getValue().toString();
        String name = powerupCard.getName();

        return name + " " + color;
    }
}
