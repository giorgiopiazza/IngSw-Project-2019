package utility;

import enumerations.Color;
import exceptions.command.InvalidCommandException;
import model.player.PlayerPosition;

import java.util.ArrayList;
import java.util.List;

public class CommandUtility {
    /**
     * Returns the position of the param in the array of string obtained by splitting
     * the original command on blanks
     *
     * @param splitCommand array in which the command is split
     * @param param of which you want to know the position
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
    public static int getPlayerID(String[] splitCommand) {
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
                return Color.valueOf(splitCommand[pos +1]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        throw new InvalidCommandException();
    }

    /**
     * Returns an ArrayList of the powerups given in the command
     *
     * @param splitCommand array in which the command is split
     * @param param the starting part of the string you need to take the IDs
     * @return the ArrayList of indexes of the IDs after a - parameter
     */
    public static List<Integer> getAttributesID(String[] splitCommand, String param) {
        List<Integer> attributesID = new ArrayList<>();
        int pos = getCommandParamPosition(splitCommand, param);

        if (pos == -1) {
            return attributesID;
        }

        String[] commandPowerups = splitCommand[pos + 1].split(",");

        for (String powerup : commandPowerups) {
            try {
                attributesID.add(Integer.parseInt(powerup));
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        return attributesID;
    }

    /**
     * Returns an ArrayList with the target positions in the command
     *
     * @param splitCommand array in which the command is split
     * @param param the starting part of the string you need to take the positions
     * @return the ArrayList of positions targets after a - parameter
     */
    public static List<PlayerPosition> getPositions(String[] splitCommand, String param) {
        List<PlayerPosition> positions = new ArrayList<>();
        int pos = getCommandParamPosition(splitCommand, param);

        if(pos == -1) {
            return positions;
        }

        String[] squaresTargeted = splitCommand[pos + 1].split(",");
        String[] cords;

        for(String square : squaresTargeted) {  // squares in the command must be passed in order as the damage distribution is
            try {
                PlayerPosition tempSquare = new PlayerPosition(0,0);
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
}
