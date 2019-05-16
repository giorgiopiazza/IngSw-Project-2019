package view.cli;

import enumerations.PlayerColor;
import model.GameSerialized;
import model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CliPrinter {

    private CliPrinter() {
        throw new IllegalStateException("Utility class");
    }

    static void printPlayerBoards(AdrenalinePrintStream out, GameSerialized gameSerialized) {
        for(Player player : gameSerialized.getPlayers()) {

            printPlayerBoard(out, player, gameSerialized);
        }
    }

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

    private static Map<String, PlayerColor> getPlayerColorMap(GameSerialized gameSerialized) {
        Map<String, PlayerColor> playerColorMap = new HashMap<>();

        for(Player player : gameSerialized.getPlayers()) {
            playerColorMap.put(player.getUsername(), player.getColor());
        }

        return playerColorMap;
    }

    private static String getPlayerColorCode(Player player, GameSerialized gameSerialized, boolean background) {
        Map<String, PlayerColor> playerColorMap = getPlayerColorMap(gameSerialized);

        return AnsiCode.getTextColorCodeByName(playerColorMap.get(player.getUsername()).name(), background);
    }
}
