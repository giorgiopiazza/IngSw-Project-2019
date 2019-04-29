package utility;

import enumerations.Properties;
import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import exceptions.utility.InvalidWeaponPropertiesException;
import model.player.PlayerPosition;

import java.util.List;
import java.util.Map;

public class CommandValidator {

    private CommandValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Method that verifies the conformity of the command with the target.
     * For example an effect whose target is a Player can not have a command that specifies a room as target
     *
     * @param command    String containing the command
     * @param targetType the target to verify
     * @return true if the command contains the right parameters for the target
     * @throws NullPointerException    if target is null
     * @throws InvalidCommandException if the command is invalid
     */
    private static boolean isTargetTypeValid(String command, TargetType targetType) {
        if (targetType == null) return false;

        switch (targetType) {
            case PLAYER:
                if (!command.contains("-t")) {
                    return false;
                }
                break;
            case SQUARE:
                if (!command.contains("-v")) {
                    return false;
                }
                break;
            default:
                if (!command.contains("-x")) {
                    return false;
                }
        }

        return true;
    }

    /**
     * Method that verifies if the effect can shoot to the number of targets
     * in the command
     *
     * @param command     the String containing the command
     * @param targetType  array of TargetType specifying the type of target the effects can shoot
     * @param number      int that states the number of targets that the effect can have
     * @param exactNumber boolean, if true the number of target is exactly number, if false number is the maximum
     * @return true if the number of targets is compatible with number
     * @throws NullPointerException if target is null
     */
    private static boolean isTargetNumValid(String command, TargetType targetType, int number, boolean exactNumber) {
        int targetNum;

        if (targetType == null) throw new NullPointerException();

        switch (targetType) {
            case PLAYER:
                List<Integer> targetsIDs = CommandUtility.getAttributesID(command.split(" "), "-t");

                targetNum = targetsIDs.size();
                break;
            case SQUARE:
                List<PlayerPosition> squares = CommandUtility.getPositions(command.split(" "), "-v");

                targetNum = squares.size();
                break;
            default:
                List<Integer> rooms = CommandUtility.getAttributesID(command.split(" "), "-x");

                targetNum = rooms.size();
        }

        return ((exactNumber && targetNum == number) ||
                (!exactNumber && targetNum <= number));
    }

    /**
     * Checks if command targets are valid based on effect properties
     *
     * @param command    String of command
     * @param properties Map of effect properties
     * @param targetType TargetType of the effect target
     * @return {@code true} if command targets are valid {@code false} otherwise
     */
    public static boolean isTargetValid(String command, Map<String, String> properties, TargetType targetType) {
        // TargetType validation
        if (!isTargetTypeValid(command, targetType)) {
            return false;
        }

        // Target number validation
        int targetNumber;
        boolean exactNumber;
        if (properties.containsKey(enumerations.Properties.TARGET_NUM.getJKey())) { // Exact target number
            targetNumber = Integer.parseInt(properties.get(enumerations.Properties.TARGET_NUM.getJKey()));
            exactNumber = true;
        } else if (properties.containsKey(enumerations.Properties.MAX_TARGET_NUM.getJKey())) { // Maximum target number
            targetNumber = Integer.parseInt(properties.get(Properties.MAX_TARGET_NUM.getJKey()));
            exactNumber = false;
        } else {
            throw new InvalidWeaponPropertiesException();
        }

        return isTargetNumValid(command, targetType, targetNumber, exactNumber);
    }

    /**
     * Checks if target moves before is congruent with the command
     *
     * @param command    String of command
     * @param properties Map of effect properties
     * @return {@code true} if target move before is valid {@code false} otherwise
     */
    public static boolean isMoveBeforeValid(String command, Map<String, String> properties) {
        return !(properties.containsKey(Properties.MOVE_TARGET_BEFORE.getJKey()) &&
                (!command.contains("-z") || (command.contains("-z") &&
                        (Boolean.parseBoolean(properties.get(Properties.MOVE_TARGET_BEFORE.getJKey())) &&
                                !CommandUtility.getBoolParam(command.split(" "), "-z")) ||
                        (!Boolean.parseBoolean(properties.get(Properties.MOVE_TARGET_BEFORE.getJKey())) &&
                                CommandUtility.getBoolParam(command.split(" "), "-z")))));
    }
}
