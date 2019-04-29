package utility;

import enumerations.Color;
import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUtility {

    private CommandUtility() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns the position of the param in the array of string obtained by splitting
     * the original command on blanks
     *
     * @param splitCommand array in which the command is split
     * @param param        of which you want to know the position
     * @return the position of the param in the split command. If the param is missing -1 is returned
     */
    public static int getCommandParamPosition(String[] splitCommand, String param) {
        for (int i = 0; i < splitCommand.length; ++i) {
            if (splitCommand[i].equals(param)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the playerID from the command
     *
     * @param splitCommand array in which the command is split
     * @return the ID of the player. If the ID is missing -1 is returned
     */
    public static int getCommandUserID(String[] splitCommand) {
        int pos = getCommandParamPosition(splitCommand, "-pid");

        if (pos != -1) {
            try {
                return Integer.parseInt(splitCommand[pos + 1]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        throw new InvalidCommandException();
    }

    /**
     * Returns the effectID from the command
     *
     * @param splitCommand array in which the command is split
     * @return the ID of the effect
     */
    public static int getEffectID(String[] splitCommand) {
        int pos = getCommandParamPosition(splitCommand, "-e");

        if (pos != -1) {
            try {
                return Integer.parseInt(splitCommand[pos + 1]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        throw new InvalidCommandException();
    }

    /**
     * Returns the Color of the specified room in the command
     *
     * @param splitCommand array in which the command is split
     * @return the Color of the room
     */
    public static Color getRoomColor(String[] splitCommand) {
        int pos = getCommandParamPosition(splitCommand, "-x");

        if (pos != -1) {
            try {
                return Color.valueOf(splitCommand[pos + 1]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        throw new InvalidCommandException();
    }

    /**
     * Returns an ArrayList of the IDs given in the command
     *
     * @param splitCommand array in which the command is split
     * @param param        the starting part of the string you need to take the IDs
     * @return the ArrayList of indexes of the IDs after a - parameter
     */
    public static List<Integer> getAttributesID(String[] splitCommand, String param) {
        List<Integer> attributesID = new ArrayList<>();
        int pos = getCommandParamPosition(splitCommand, param);

        if (pos == -1) {
            return attributesID;
        }

        String[] commandString = splitCommand[pos + 1].split(",");

        for (String addingID : commandString) {
            try {
                attributesID.add(Integer.parseInt(addingID));
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        return attributesID;
    }

    /**
     * Returns an ArrayList of the players whose ID is contained in the List passed
     *
     * @param playersIDs the List of IDs you need the related players' reference
     * @return an ArrayList of players
     */
    public static List<Player> getPlayersByIDs(List<Integer> playersIDs) {
        if (playersIDs == null) throw new NullPointerException("Can not take any player from null");

        List<Player> players = new ArrayList<>();
        for (int playerID : playersIDs) {
            players.add(Game.getInstance().getPlayerByID(playerID));
        }

        return players;
    }

    public static List<PlayerPosition>  getTargetPositions(String[] splitCommand, TargetType targetType) {
        List<PlayerPosition> squares;
        List<Player> targets;

        switch (targetType) {
            case PLAYER:
                targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(splitCommand, "-t"));
                squares = new ArrayList<>();

                for (Player targetPlayer : targets) {
                    squares.add(targetPlayer.getPosition());
                }

                break;
            case SQUARE:
                squares = CommandUtility.getPositions(splitCommand, "-v");
                break;
            default:
                targets = Game.getInstance().getGameMap().getPlayersInRoom(CommandUtility.getRoomColor(splitCommand));
                squares = new ArrayList<>();

                for (Player targetPlayer : targets) {
                    squares.add(targetPlayer.getPosition());
                }
        }

        return squares;
    }

    /**
     * Returns an ArrayList with the target positions in the command
     *
     * @param splitCommand array in which the command is split
     * @param param        the starting part of the string you need to take the positions
     * @return the ArrayList of positions targets after a - parameter
     */
    public static List<PlayerPosition> getPositions(String[] splitCommand, String param) {
        List<PlayerPosition> positions = new ArrayList<>();
        int pos = getCommandParamPosition(splitCommand, param);

        if (pos == -1) {
            return positions;
        }

        String[] squaresTargeted = splitCommand[pos + 1].split(",");
        String[] cords;

        for (String square : squaresTargeted) {  // squares in the command must be passed in order as the damage distribution is
            try {
                PlayerPosition tempSquare = new PlayerPosition(0, 0);
                cords = square.split("_");
                tempSquare.setCoordX(Integer.parseInt(cords[0]));
                tempSquare.setCoordY(Integer.parseInt(cords[1]));
                positions.add(tempSquare);
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        return positions;
    }

    /**
     * Method used to return the value of a boolean parameter in the command
     *
     * @param splitCommand Array of string containing the command
     * @param param        the param we want to know his boolean value
     * @return the boolean value after the specified parameter
     */
    public static boolean getBoolParam(String[] splitCommand, String param) {
        int pos = getCommandParamPosition(splitCommand, param);

        if(pos == -1) {
            throw new InvalidCommandException();
        }

        return Boolean.parseBoolean(splitCommand[pos + 1]);
    }

    /**
     * Method used to return the index of the powerup the using player wants to use
     *
     * @param splitCommand Array pf String containing the command
     * @return an integer representing the index of the powerup held by the using player
     */
    public static int getPowerupIndex(String[] splitCommand) {
        int pos = getCommandParamPosition(splitCommand, "-ip");

        if(pos == -1) {
            throw new InvalidCommandException();
        }

        return Integer.parseInt(splitCommand[pos + 1]);
    }

    /**
     * Removes from the original command the parts that are not needed
     * for the validation of the target specified
     *
     * @param command String containing the original command
     * @param target  TargetType of the interested information
     * @return the String with no other information that the ones we need for the target
     */
    public static String getSubCommand(String command, TargetType target) {
        String[] splitCommand = command.split(" ");
        List<String> splitList = new ArrayList<>(Arrays.asList(splitCommand));

        switch (target) {
            case PLAYER:
                if (command.contains("-v")) {
                    int pos = getCommandParamPosition(splitCommand, "-v");
                    splitList.remove(pos);
                    splitList.remove(pos);
                }
                if (command.contains("-x")) {
                    int pos = getCommandParamPosition(splitCommand, "-x");
                    splitList.remove(pos);
                    splitList.remove(pos);
                }

                return String.join(" ", splitList);
            case SQUARE:
                if (command.contains("-t")) {
                    int pos = getCommandParamPosition(splitCommand, "-t");
                    splitList.remove(pos);
                    splitList.remove(pos);
                }
                if (command.contains("-x")) {
                    int pos = getCommandParamPosition(splitCommand, "-x");
                    splitList.remove(pos);
                    splitList.remove(pos);
                }

                return String.join(" ", splitList);
            default:
                if (command.contains("-t")) {
                    int pos = getCommandParamPosition(splitCommand, "-t");
                    splitList.remove(pos);
                    splitList.remove(pos);
                }
                if (command.contains("-v")) {
                    int pos = getCommandParamPosition(splitCommand, "-v");
                    splitList.remove(pos);
                    splitList.remove(pos);
                }

                return String.join(" ", splitList);
        }
    }
}
