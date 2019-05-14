package view.cli;

import enumerations.PlayerColor;
import model.GameSerialized;
import model.player.Player;
import model.player.UserPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class CliPrinter {

    private CliPrinter() {
        throw new IllegalStateException("Utility class");
    }

    static void printPlayerBoards(GameSerialized gameSerialized) {
        for(UserPlayer player : gameSerialized.getPlayers()) {
            printPlayerBoard(player, gameSerialized);
        }

        if (gameSerialized.isTerminatorPresent()) {
            printPlayerBoard(gameSerialized.getTerminator(), gameSerialized);
        }
    }

    private static void printPlayerBoard(Player player, GameSerialized gameSerialized) {
        String markString = getMarksString(player, gameSerialized);
        String damageString = getDamageString(player, gameSerialized);
        String playerColor = getPlayerColorCode(player, gameSerialized, true) + AnsiCode.TEXT_BLACK;
        System.out.println(
                playerColor + "┌─────────────────────────────────────────────────────────────────────────────────────┐" + AnsiCode.RESET + "\n" +
                playerColor + "│                                             ┌─────────────────────────────────────┐ │" + AnsiCode.RESET + "\n" +
                playerColor + "│                                             │" + markString + " │ │" + AnsiCode.RESET + "\n" +
                playerColor + "│                                             └─────────────────────────────────────┘ │" + AnsiCode.RESET + "\n" +
                playerColor + "│ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ │" + AnsiCode.RESET + "\n" +
                playerColor + "│ " + damageString + "│" + AnsiCode.RESET + "\n" +
                playerColor + "│ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ └────┘ │" + AnsiCode.RESET + "\n" +
                playerColor + "└─────────────────────────────────────────────────────────────────────────────────────┘" + AnsiCode.RESET + "\n");
    }

    private static String getMarksString(Player player, GameSerialized gameSerialized) {
        Map<String, PlayerColor> playerColorMap = getPlayerColorMap(gameSerialized);

        String playerBackgroundColor = getPlayerColorCode(player, gameSerialized, true);
        List<String> markDealers = player.getPlayerBoard().getMarks();
        StringBuilder marksString = new StringBuilder();
        int count = 0;

        for (String markDealer : markDealers) {
            String color = AnsiCode.getTextColorCodeByName(playerColorMap.get(markDealer).name(), true);
            marksString.append(" ").append(color).append("  ").append(playerBackgroundColor);
            ++count;
        }

        for (; count < 12; ++count) {
            marksString.append("   ");
        }

        return marksString.toString();
    }

    private static String getDamageString(Player player, GameSerialized gameSerialized) {
        Map<String, PlayerColor> playerColorMap = getPlayerColorMap(gameSerialized);

        String playerBackgroundColor = getPlayerColorCode(player, gameSerialized, true);
        List<String> damageDealers = player.getPlayerBoard().getDamages();
        StringBuilder marksString = new StringBuilder();
        int count = 0;

        for (String damageDealer : damageDealers) {
            String color = AnsiCode.getTextColorCodeByName(playerColorMap.get(damageDealer).name(), true);
            marksString.append("│ ").append(color).append("  ").append(playerBackgroundColor).append(" │ ");
            ++count;
        }

        for (; count < 12; ++count) {
            marksString.append("│    │ ");
        }

        return marksString.toString();
    }

    private static Map<String, PlayerColor> getPlayerColorMap(GameSerialized gameSerialized) {
        Map<String, PlayerColor> playerColorMap = new HashMap<>();

        for(UserPlayer player : gameSerialized.getPlayers()) {
            playerColorMap.put(player.getUsername(), player.getColor());
        }

        return playerColorMap;
    }

    private static String getPlayerColorCode(Player player, GameSerialized gameSerialized, boolean background) {
        Map<String, PlayerColor> playerColorMap = getPlayerColorMap(gameSerialized);

        return AnsiCode.getTextColorCodeByName(playerColorMap.get(player.getUsername()).name(), background);
    }
}
