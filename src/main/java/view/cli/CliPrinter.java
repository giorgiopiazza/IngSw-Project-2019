package view.cli;

import enumerations.Ammo;
import enumerations.PlayerColor;
import enumerations.SquareAdjacency;
import enumerations.SquareType;
import model.GameSerialized;
import model.cards.PowerupCard;
import model.player.Player;
import model.map.*;

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
            printMapRow(out, map.getRooms()[i]);
        }
    }

    private static void printMapRow(AdrenalinePrintStream out, Square[] squareRow) {
        out.print(getSquareTopRow(squareRow) +
                getSquareMidRow(squareRow) +
                getSquareBotRow(squareRow));
    }

    private static String getSquareTopRow(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
                row.append(roomColor).append(getTopLeft(square)).append(getTopMiddle(square)).append(getTopRight(square)).append(AnsiCode.RESET);
            } else {
                row.append(AnsiCode.RESET).append("      ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static String getTopLeft(Square square) {
        String left;

        if (square.getWest() != SquareAdjacency.SQUARE && square.getNorth() != SquareAdjacency.SQUARE) {
            left = "┌";
        } else if (square.getWest() != SquareAdjacency.SQUARE && square.getNorth() == SquareAdjacency.SQUARE) {
            left = "│";
        } else if (square.getWest() == SquareAdjacency.SQUARE && square.getNorth() != SquareAdjacency.SQUARE) {
            left = "─";
        } else {
            left = " ";
        }

        return left;
    }

    private static String getTopMiddle(Square square) {
        String middle;

        if (square.getNorth() == SquareAdjacency.SQUARE) {
            middle = "    ";
        } else if (square.getNorth() == SquareAdjacency.DOOR) {
            middle = "─  ─";
        } else {
            middle = "────";
        }

        return middle;
    }

    private static String getTopRight(Square square) {
        String right;

        if (square.getEast() != SquareAdjacency.SQUARE && square.getNorth() != SquareAdjacency.SQUARE) {
            right = "┐";
        } else if (square.getEast() != SquareAdjacency.SQUARE && square.getNorth() == SquareAdjacency.SQUARE) {
            right = "│";
        } else if (square.getEast() == SquareAdjacency.SQUARE && square.getNorth() != SquareAdjacency.SQUARE) {
            right = "─";
        } else {
            right = " ";
        }

        return right;
    }

    private static String getSquareMidRow(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
                row.append(roomColor).append(getMidLeft(square)).append(getMidMiddle(square)).append(getMidRight(square)).append(AnsiCode.RESET);
            } else {
                row.append(AnsiCode.RESET).append("      ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static String getMidLeft(Square square) {
        String left;

        if (square.getWest() == SquareAdjacency.WALL) {
            left = "│";
        } else {
            left = " ";
        }

        return left;
    }

    private static String getMidMiddle(Square square) {
        String middle;

        if (square.getSquareType() == SquareType.SPAWN) {
            middle = " SP ";
        } else {
            middle = " AM ";
        }

        return middle;
    }

    private static String getMidRight(Square square) {
        String right;

        if (square.getEast() == SquareAdjacency.WALL) {
            right = "│";
        } else {
            right = " ";
        }

        return right;
    }

    private static String getSquareBotRow(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
                String roomColor = AnsiCode.getTextColorCodeByName(square.getRoomColor().name(), true) + AnsiCode.TEXT_BLACK;
                row.append(roomColor).append(getBotLeft(square)).append(getBotMiddle(square)).append(getBotRight(square)).append(AnsiCode.RESET);
            } else {
                row.append(AnsiCode.RESET).append("      ");
            }
        }

        row.append("\n");
        return row.toString();
    }

    private static String getBotLeft(Square square) {
        String left;

        if (square.getWest() != SquareAdjacency.SQUARE && square.getSouth() != SquareAdjacency.SQUARE) {
            left = "└";
        } else if (square.getWest() != SquareAdjacency.SQUARE && square.getSouth() == SquareAdjacency.SQUARE) {
            left = "│";
        } else if (square.getWest() == SquareAdjacency.SQUARE && square.getSouth() != SquareAdjacency.SQUARE) {
            left = "─";
        } else {
            left = " ";
        }

        return left;
    }

    private static String getBotMiddle(Square square) {
        String middle;

        if (square.getSouth() == SquareAdjacency.SQUARE) {
            middle = "    ";
        } else if (square.getSouth() == SquareAdjacency.DOOR) {
            middle = "─  ─";
        } else {
            middle = "────";
        }

        return middle;
    }

    private static String getBotRight(Square square) {
        String right;

        if (square.getEast() != SquareAdjacency.SQUARE && square.getSouth() != SquareAdjacency.SQUARE) {
            right = "┘";
        } else if (square.getEast() != SquareAdjacency.SQUARE && square.getSouth() == SquareAdjacency.SQUARE) {
            right = "│";
        } else if (square.getEast() == SquareAdjacency.SQUARE && square.getSouth() != SquareAdjacency.SQUARE) {
            right = "─";
        } else {
            right = " ";
        }

        return right;
    }

    public static String toStringPowerUpCard(PowerupCard powerupCard) {
        String color = powerupCard.getValue().toString();
        String name = powerupCard.getName();

        return name + " " + color;
    }
}
