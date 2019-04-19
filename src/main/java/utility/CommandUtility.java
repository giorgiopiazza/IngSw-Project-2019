package utility;

import exceptions.command.InvalidCommandException;

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

    public static List<Integer> getPowerupAmmoID(String[] splitCommand) {
        List<Integer> powerupsID = new ArrayList<>();
        int pos = getCommandParamPosition(splitCommand, "-a");

        if (pos == -1) {
            return powerupsID;
        }

        String[] commandPowerups = splitCommand[pos].split(",");

        for (String powerup : commandPowerups) {
            try {
                powerupsID.add(Integer.parseInt(powerup));
            } catch (NumberFormatException e) {
                throw new InvalidCommandException();
            }
        }

        return powerupsID;
    }
}
