package view.cli;

import enumerations.*;
import model.GameSerialized;
import model.cards.Card;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.cards.effects.Effect;
import model.player.AmmoQuantity;
import model.player.Player;
import model.map.*;
import model.player.PlayerPoints;
import model.player.UserPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        for (Player player : gameSerialized.getAllPlayers()) {
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

        for (Player player : gameSerialized.getAllPlayers()) {
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
        List<Player> inGamePlayers = gameSerialized.getAllPlayers();

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

    private static String getPlayerDecoration(Square[] squareRow, GameSerialized gameSerialized, List<Player> inGamePlayers) {
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

    private static StringBuilder getMidPlayerDecoration(Square square, GameSerialized gameSerialized, List<Player> inGamePlayers) {
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
        } else {
            if (((CardSquare) square).isAmmoTilePresent())
                midCentre = "  AMMO   ";
            else {
                midCentre = "         ";
            }
        }

        return midCentre;
    }

    private static String getSquareBotDecoration(Square[] squareRow) {
        StringBuilder row = new StringBuilder();

        for (Square square : squareRow) {
            if (square != null) {
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
        } else if (square.getWest() == SquareAdjacency.SQUARE) {
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

    /**
     * Prints the weapons in a {@link UserPlayer UserPlayer's} hand
     *
     * @param out         printStream where to print
     * @param weaponCards Array of {@link WeaponCard weapons} to be printed
     */
    static void printWeapons(AdrenalinePrintStream out, WeaponCard[] weaponCards) {
        if (weaponCards.length == 0) {
            out.println("                    YOU HAVE NO WEAPONS                     ");
        } else {
            out.print(
                    getWeapontTopRow(weaponCards) +
                            addWeaponName(weaponCards) +
                            addWeaponGrabCost(weaponCards, 1, true) +
                            addWeaponGrabCost(weaponCards, 2, false) +
                            addBaseEffect(weaponCards) +
                            addWeaponEffectDescription(weaponCards, 0, 0, 33) +
                            addWeaponEffectDescription(weaponCards, 0, 33, 66) +
                            addWeaponEffectDescription(weaponCards, 0, 66, 99) +
                            addWeaponLineSeparator(weaponCards) +
                            addFirstEffect(weaponCards) +
                            addWeaponEffectDescription(weaponCards, 1, 0, 33) +
                            addWeaponEffectDescription(weaponCards, 1, 33, 66) +
                            addWeaponEffectDescription(weaponCards, 1, 66, 99) +
                            addWeaponLineSeparator(weaponCards) +
                            addSecondEffect(weaponCards) +
                            addWeaponEffectDescription(weaponCards, 2, 0, 33) +
                            addWeaponEffectDescription(weaponCards, 2, 33, 66) +
                            addWeaponEffectDescription(weaponCards, 2, 66, 99) +
                            addWeaponLineSeparator(weaponCards) +
                            addComboEffect(weaponCards) +
                            addWeaponEffectDescription(weaponCards, 3, 0, 33) +
                            getWeaponBotRow(weaponCards)

            );
        }
    }

    private static String getWeapontTopRow(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                out.append("╔═══════════════════════════════════╗     ");
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addWeaponLineSeparator(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                out.append("║                                   ║     ");
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addWeaponName(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < weapons.length; ++i) {
            if (weapons[i] != null) {
                out.append("║ ").append(getWeaponMainDetails(weapons[i])).append("[").append(i).append("]").append("  ║     ");
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String getWeaponMainDetails(WeaponCard weapon) {
        StringBuilder tempOut = new StringBuilder();
        String weaponName = weapon.getName();
        String weaponColor = AnsiCode.getTextColorCodeByName(weapon.getCost()[0].name(), true) + AnsiCode.TEXT_BLACK;

        if (weapon.getCost().length > 1) {
            tempOut.append(weaponColor).append("__").append(AnsiCode.RESET);
        } else {
            tempOut.append(weaponColor).append("  ").append(AnsiCode.RESET);
        }

        return tempOut.append(" ").append(weaponName).append(addMissingBlanks(weaponName)).toString();
    }

    private static StringBuilder addMissingBlanks(String weaponName) {
        StringBuilder tempOut = new StringBuilder();
        final int MAX_NAME_LENGTH = 26;
        int missingBlanks = MAX_NAME_LENGTH - weaponName.length();

        tempOut.append(" ".repeat(missingBlanks));

        return tempOut;
    }

    private static String addWeaponGrabCost(WeaponCard[] weapons, int printAmmo, boolean firstCost) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                out.append("║ ").append(addMissingCost(weapon.getCost(), printAmmo));

                if (firstCost) {
                    out.append(" ").append(getWeaponRechargeStatus(weapon)).append("                           ║     ");
                } else {
                    out.append("                                ║     ");
                }

            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String getWeaponRechargeStatus(WeaponCard weaponCard) {
        switch (weaponCard.status()) {
            case 0:
                return "CHAR";
            case 1:
                return "UNCH";
            default:
                return "SEMI";
        }
    }

    private static String addMissingCost(Ammo[] missingCost, int printAmmo) {
        StringBuilder tempOut = new StringBuilder();

        if (missingCost.length > printAmmo) {
            String weaponColor = AnsiCode.getTextColorCodeByName(missingCost[printAmmo].name(), true) + AnsiCode.TEXT_BLACK;
            if (printAmmo == 1 && missingCost.length > 2) {
                tempOut.append(weaponColor).append("__").append(AnsiCode.RESET);
            } else {
                tempOut.append(weaponColor).append("  ").append(AnsiCode.RESET);
            }
        } else {
            tempOut.append("  ");
        }

        return tempOut.toString();
    }

    private static String addBaseEffect(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                out.append("║         Base Effect: [0]          ║     ");
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String getEffectCost(AmmoQuantity ammoQuantity) {
        StringBuilder tempOut = new StringBuilder();

        tempOut         .append((AnsiCode.getTextColorCodeByName("Red", true) + "  " + AnsiCode.RESET + " ").repeat(ammoQuantity.getRedAmmo()))
                        .append((AnsiCode.getTextColorCodeByName("Blue", true) + "  " + AnsiCode.RESET + " ").repeat(ammoQuantity.getBlueAmmo()))
                        .append((AnsiCode.getTextColorCodeByName("Yellow", true) + "  " + AnsiCode.RESET + " ").repeat(ammoQuantity.getYellowAmmo()));
        if(ammoQuantity.getAmmoCount() == 0) {
            tempOut.append("        ");
        } else if(ammoQuantity.getAmmoCount() == 1){
            tempOut.append("     ");
        } else {
            tempOut.append("  ");
        }

        return tempOut.toString();
    }

    private static String addFirstEffect(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                if (!weapon.getSecondaryEffects().isEmpty()) {
                    out.append("║ ").append(getEffectCost(weapon.getSecondaryEffects().get(0).getCost())).append("First Effect: [1]         ║     ");
                } else {
                    out.append("║                                   ║     ");
                }
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addSecondEffect(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                if (weapon.getSecondaryEffects().size() > 1) {
                    out.append("║ ").append(getEffectCost(weapon.getSecondaryEffects().get(1).getCost())).append("Second Effect: [2]        ║     ");
                } else {
                    out.append("║                                   ║     ");
                }
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addComboEffect(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                if (weapon.getSecondaryEffects().size() > 2) {
                    out.append("║         Combo Effect: [3]         ║     ");
                } else {
                    out.append("║    NO COMBO WITH THESE EFFECTS    ║     ");
                }
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addWeaponEffectDescription(WeaponCard[] weapons, int effect, int startIndex, int finishIndex) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                String tempEffect;
                if (effect == 0) {
                    tempEffect = weapon.getBaseEffect().getDescription();
                } else if (weapon.getSecondaryEffects().size() >= effect) {
                    tempEffect = weapon.getSecondaryEffects().get(effect - 1).getDescription();
                } else {
                    tempEffect = "                                 ";
                }
                out.append("║ ").append(addEffectChunk(tempEffect, startIndex, finishIndex)).append(" ║     ");
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addEffectChunk(String description, int startIndex, int finishIndex) {
        if (description.length() <= finishIndex - 1) {
            return description;
        }

        return description.substring(startIndex, finishIndex);
    }

    private static String getWeaponBotRow(WeaponCard[] weapons) {
        StringBuilder out = new StringBuilder();

        for (WeaponCard weapon : weapons) {
            if (weapon != null) {
                out.append("╚═══════════════════════════════════╝     ");
            } else {
                out.append("                                          ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    /**
     * Prints the {@link PowerupCard Powerups} contained in an array
     *
     * @param out          printStream where to print
     * @param powerupCards array of {@link PowerupCard Powerups} to be printed
     */
    static void printPowerups(AdrenalinePrintStream out, PowerupCard[] powerupCards) {
        if (powerupCards.length == 0) {
            out.println("                    YOU HAVE NO POWERUPS                    ");
        } else {
            out.println(
                    getPowerupTopRow(powerupCards) +
                            addPowerupName(powerupCards) +
                            addPowerupLineSeparator(powerupCards) +
                            addPowerupEffect(powerupCards) +
                            addPowerupEffectDescription(powerupCards, 0, 24) +
                            addPowerupEffectDescription(powerupCards, 24, 48) +
                            addPowerupEffectDescription(powerupCards, 48, 72) +
                            addPowerupEffectDescription(powerupCards, 72, 96) +
                            addPowerupEffectDescription(powerupCards, 96, 120) +
                            addPowerupEffectDescription(powerupCards, 120, 144) +
                            addPowerupEffectDescription(powerupCards, 144, 168) +
                            addPowerupLineSeparator(powerupCards) +
                            addPowerupValue(powerupCards) +
                            addPowerupValue(powerupCards) +
                            getPowerupBotRow(powerupCards)
            );
        }
    }

    private static String getPowerupTopRow(PowerupCard[] powerups) {
        StringBuilder out = new StringBuilder();

        for (PowerupCard powerup : powerups) {
            if (powerup != null) {
                out.append("╔══════════════════════════╗     ");
            } else {
                out.append("                                 ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addPowerupName(PowerupCard[] powerups) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < powerups.length; ++i) {
            if (powerups[i] != null) {
                out.append("║ ").append(addFirstMissingBlanks(powerups[i].getName())).append(powerups[i].getName()).append(" [").append(i).append("]").append(addSecondMissingBlanks(powerups[i].getName())).append(" ║     ");
            } else {
                out.append("                                 ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static StringBuilder addFirstMissingBlanks(String powerupName) {
        StringBuilder tempOut = new StringBuilder();
        final int MAX_POWERUP_LENGTH = 22;
        int missingBlanks = (MAX_POWERUP_LENGTH - powerupName.length()) / 2;

        tempOut.append(" ".repeat(missingBlanks));
        return tempOut;
    }

    private static StringBuilder addSecondMissingBlanks(String powerupName) {
        StringBuilder tempOut = new StringBuilder();
        final int MAX_POWERUP_LENGTH = 18;
        int missingBlanks = (MAX_POWERUP_LENGTH - powerupName.length()) / 2;

        if (powerupName.length() % 2 != 0) ++missingBlanks;

        tempOut.append(" ".repeat(missingBlanks));
        return tempOut;
    }

    private static String addPowerupLineSeparator(PowerupCard[] powerups) {
        StringBuilder out = new StringBuilder();

        for (PowerupCard powerup : powerups) {
            if (powerup != null) {
                out.append("║                          ║     ");
            } else {
                out.append("                                 ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addPowerupEffect(PowerupCard[] powerups) {
        StringBuilder out = new StringBuilder();

        for (PowerupCard powerup : powerups) {
            if (powerup != null) {
                out.append("║         Effect:          ║     ");
            } else {
                out.append("                                 ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addPowerupEffectDescription(PowerupCard[] powerups, int startIndex, int finishIndex) {
        StringBuilder out = new StringBuilder();

        for (PowerupCard powerup : powerups) {
            if (powerup != null) {
                out.append("║ ").append(addEffectChunk(powerup.getBaseEffect().getDescription(), startIndex, finishIndex)).append(" ║     ");
            } else {
                out.append("                                 ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String addPowerupValue(PowerupCard[] powerups) {
        StringBuilder out = new StringBuilder();

        for (PowerupCard powerup : powerups) {
            if (powerup != null) {
                String powerupColor = AnsiCode.getTextColorCodeByName(powerup.getValue().name(), true) + AnsiCode.TEXT_BLACK;
                out.append("║           ").append(powerupColor).append("    ").append(AnsiCode.RESET).append("           ║     ");
            } else {
                out.append("                                 ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    private static String getPowerupBotRow(PowerupCard[] powerups) {
        StringBuilder out = new StringBuilder();

        for (PowerupCard powerup : powerups) {
            if (powerup != null) {
                out.append("╚══════════════════════════╝     ");
            } else {
                out.append("                                 ");
            }
        }

        out.append("\n");
        return out.toString();
    }

    static String toStringPowerUpCard(PowerupCard powerupCard) {
        String color = powerupCard.getValue().toString();
        String name = powerupCard.getName();

        return name + " " + color;
    }

    static void printAmmo(AdrenalinePrintStream out, AmmoQuantity ammo) {
        if (ammo.noAmmo()) {
            out.println("No ammo, POOR BOY");
        } else {
            out.print("Ammo: " +
                    printColorAmmos(ammo.getRedAmmo(), "Red") +
                    printColorAmmos(ammo.getBlueAmmo(), "Blue") +
                    printColorAmmos(ammo.getYellowAmmo(), "Yellow") + "\n"
            );
        }
    }

    /**
     * Prints the names of the players in the game colored with their chosen color
     *
     * @param out PrintStream where to print
     * @param playerList the List of {@link Player Players} to be printed
     */
    static void printUsername(AdrenalinePrintStream out, List<Player> playerList) {
        StringBuilder tempOut = new StringBuilder();

        tempOut.append("Player username: ");

        for (Player player : playerList) {
            tempOut.append(AnsiCode.getTextColorCodeByName(player.getColor().name(), false))
                    .append(player.getUsername())
                    .append(AnsiCode.RESET);

            if (!playerList.get(playerList.size() - 1).equals(player)) {
                tempOut.append(", ");
            }
        }

        out.println(tempOut);

    }

    /**
     * Prints the winners of the game
     *
     * @param out PrintStream where to print
     * @param allPoints the ArrayList containing all the {@link PlayerPoints PlayerPoints}
     */
    static void printWinners(AdrenalinePrintStream out, List<PlayerPoints> allPoints) {
        List<PlayerPoints> winners = allPoints.stream().filter(PlayerPoints::isWinner).collect(Collectors.toList());

        if(winners.size() == allPoints.size()) {
            out.println(
                    getTopWinnersDecoration(winners.size()) +
                            addEmptyLine(winners.size()) +
                            getWinnersTitle(winners.size(), "EVERYBODY WIN THIS GAME") +
                            addEmptyLine(winners.size()) +
                            getWinnersNames(winners) +
                            getWinnersPoints(winners) +
                            addEmptyLine(winners.size()) +
                            getBotWinnersDecoration(winners.size())
            );
        } else {
            int size = winners.size() > (allPoints.size() / 2) ? winners.size() : allPoints.size() - winners.size();

            out.println(
                    getTopWinnersDecoration(size) +
                            addEmptyLine(size) +
                            getWinnersTitle(size, "THE WINNERS OF THIS GAME ARE") +
                            addEmptyLine(size) +
                            getWinnersNames(size, winners) +
                            getWinnersPoints(size, winners) +
                            addEmptyLine(size) +
                            addSeparatorLine(size) +
                            addEmptyLine(size) +
                            getWinnersTitle(size, "POOR ORDERED LOOSERS ARE") +
                            addEmptyLine(size) +
                            getWinnersNames(size, allPoints.stream().filter(player -> !winners.contains(player))
                                                                     .sorted((player1, player2) -> {
                                                                         if(player1.getPoints() < player2.getPoints()) return 1;
                                                                         else if (player1.getPoints() > player2.getPoints()) return -1;
                                                                         else return 0;
                            })
                                                                     .collect(Collectors.toList()))
                                    +
                            getWinnersPoints(size, allPoints.stream().filter(player -> !winners.contains(player))
                                                                      .sorted((player1, player2) -> {
                                                                          if(player1.getPoints() < player2.getPoints()) return 1;
                                                                          else if (player1.getPoints() > player2.getPoints()) return -1;
                                                                          else return 0;
                            })
                                                                      .collect(Collectors.toList())) +
                            addEmptyLine(size) +
                            getBotWinnersDecoration(size)
            );
        }
    }

    private static String getTopWinnersDecoration(int winners) {
        return "╔═══════════════" + "═══════════════".repeat(winners - 2) + "═══════════════╗" + "\n";
    }

    private static String getBotWinnersDecoration(int winners) {
        return "╚═══════════════" + "═══════════════".repeat(winners - 2) + "═══════════════╝" + "\n";
    }

    private static String addSeparatorLine(int size) {
        return "╠═══════════════" + "═══════════════".repeat(size - 2) + "═══════════════╣" + "\n";
    }

    private static String addEmptyLine(int winners) {
        return "║" + "               ".repeat(winners) + "║"  + "\n";
    }

    private static String getWinnersTitle(int size, String winnerTitle) {
        final int decorationLength = 15 * size;
        int firstMissingBlanks;
        int secondMissingBlanks;

        int difference = decorationLength - winnerTitle.length();
        if (difference % 2 == 0) {
            firstMissingBlanks = secondMissingBlanks = difference / 2;
        } else {
            firstMissingBlanks = difference / 2 + 1;
            secondMissingBlanks = difference / 2;
        }

        return "║" + " ".repeat(firstMissingBlanks) + winnerTitle + " ".repeat(secondMissingBlanks) + "║" + "\n";
    }


    private static String getWinnersNames(List<PlayerPoints> winners) {
        StringBuilder tempOut = new StringBuilder();

        tempOut.append("║");

        for(PlayerPoints player : winners) {
            tempOut.append(addFirstWinnersMissingBlanks(player.getUserName())).append(player.getUserName()).append(addSecondWinnersMissingBlanks(player.getUserName()));
        }

        tempOut.append("║").append("\n");
        return tempOut.toString();
    }

    private static String getWinnersNames(int size, List<PlayerPoints> winners) {
        StringBuilder tempOut = new StringBuilder();

        tempOut.append("║");

        if(size == winners.size()) {
            for(PlayerPoints player : winners) {
                tempOut.append(addFirstWinnersMissingBlanks(player.getUserName())).append(player.getUserName()).append(addSecondWinnersMissingBlanks(player.getUserName()));
            }
        } else {
            tempOut.append(addFirstCenterBlanks(size, winners.size()));
            for(PlayerPoints player : winners) {
                tempOut.append(addFirstWinnersMissingBlanks(player.getUserName())).append(player.getUserName()).append(addSecondWinnersMissingBlanks(player.getUserName()));
            }
            tempOut.append(addSecondCenterBlanks(size, winners.size()));
        }

        tempOut.append("║");
        tempOut.append("\n");
        return tempOut.toString();
    }

    private static String addFirstWinnersMissingBlanks(String writingString) {
        final int decorationLength = 15;
        int difference = decorationLength - writingString.length();
        int missingBlanks;

        if(difference % 2 == 0) {
            missingBlanks = difference / 2;
        } else {
            missingBlanks = difference / 2 + 1;
        }
        return " ".repeat(missingBlanks);
    }

    private static String addSecondWinnersMissingBlanks(String writingString) {
        final int decorationLentgh = 15;
        int missingBlanks = (decorationLentgh - writingString.length()) / 2;

        return " ".repeat(missingBlanks);
    }

    private static String addFirstCenterBlanks(int size, int centeringPlayers) {
        int playersSize = centeringPlayers * 15;
        int difference = size * 15 - playersSize;

        int missingBlanks;

        if(difference % 2 == 0) {
            missingBlanks = difference / 2;
        } else {
            missingBlanks = difference / 2 + 1;
        }
        return " ".repeat(missingBlanks);
    }

    private static String addSecondCenterBlanks(int size, int centeringPlayers) {
        int playersSize = centeringPlayers * 15;
        int missingBlanks = (size * 15 - playersSize) / 2;

        return " ".repeat(missingBlanks);
    }

    private static String getWinnersPoints(List<PlayerPoints> winners) {
        StringBuilder tempOut = new StringBuilder();

        tempOut.append("║");

        for(PlayerPoints player : winners) {
            String tempPoints = "Points: " + player.getPoints();
            tempOut.append(addFirstWinnersMissingBlanks(tempPoints)).append(tempPoints).append(addSecondWinnersMissingBlanks(tempPoints));
        }

        tempOut.append("║").append("\n");
        return tempOut.toString();

    }

    private static String getWinnersPoints(int size, List<PlayerPoints> winners) {
        StringBuilder tempOut = new StringBuilder();

        tempOut.append("║");

        if(size == winners.size()) {
            for(PlayerPoints player : winners) {
                String tempPoints = "Points: " + player.getPoints();
                tempOut.append(addFirstWinnersMissingBlanks(tempPoints)).append(tempPoints).append(addSecondWinnersMissingBlanks(tempPoints));
            }
        } else {
            tempOut.append(addFirstCenterBlanks(size, winners.size()));
            for(PlayerPoints player : winners) {
                String tempPoints = "Points: " + player.getPoints();
                tempOut.append(addFirstWinnersMissingBlanks(tempPoints)).append(tempPoints).append(addSecondWinnersMissingBlanks(tempPoints));
            }
            tempOut.append(addSecondCenterBlanks(size, winners.size()));
        }

        tempOut.append("║").append("\n");
        return tempOut.toString();
    }

    private static String printColorAmmos(int ammoInt, String color) {
        StringBuilder tempOut = new StringBuilder();

        String ammoColor = AnsiCode.getTextColorCodeByName(color, true);
        String ammoString = (ammoColor + "  " + AnsiCode.RESET + " ").repeat(ammoInt);

        return tempOut.append(ammoString).append(" ").toString();
    }

    /**
     * Clears the console
     */
    static void clearConsole(AdrenalinePrintStream out) {
        out.print(AnsiCode.CLEAR_CONSOLE);
        out.flush();
    }
}
