package utility;

import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;

import java.util.ArrayList;
import java.util.List;

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
    public static boolean isTargetTypeValid(String command, TargetType targetType) {
        if (targetType == null) throw new NullPointerException();

        switch (targetType) {
            case PLAYER:
                if (!command.contains("-t")) {
                    throw new InvalidCommandException();
                }
                break;
            case SQUARE:
                if (!command.contains("-v")) {
                    throw new InvalidCommandException();
                }
                break;
            default:
                if (!command.contains("-x")) {
                    throw new InvalidCommandException();
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
    public static boolean isTargetNumValid(String command, TargetType targetType, int number, boolean exactNumber) {
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
     * Method used to verify if the specified target is far from the shooter the specified distance.
     * Using the boolean parameter we indicate that the distance has to be exactly the one specified
     * otherwise the minimum distance
     *
     * @param command       String containing the command
     * @param targetType    TargetType from which we need to verify the distance
     * @param distance      integer specifying the distance to verify
     * @param exactDistance boolean true if the distance is exact, otherwise the minimum one
     * @return true if the target's distance fits with the one specified, otherwise false
     * @throws NullPointerException    if target is null
     * @throws InvalidCommandException if the command is invalid
     */
    public static boolean isTargetDistanceValid(String command, TargetType targetType, int distance, boolean exactDistance) {
        int tempDist;
        List<PlayerPosition> squaresPositions;
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(command.split("")));

        if (targetType == null) throw new NullPointerException();

        switch (targetType) {
            case PLAYER:
                List<Player> targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(command.split(" "), "-t"));

                // Builds the ArrayList of targets PlayerPosition
                squaresPositions = new ArrayList<>();
                for (Player target : targets) {
                    squaresPositions.add(target.getPosition());
                }

                break;
            case SQUARE:
                squaresPositions = CommandUtility.getPositions(command.split(" "), "-v");
                break;
            default:
                // Rooms do not have a definition of distance
                throw new InvalidCommandException();
        }

        for (PlayerPosition position : squaresPositions) {
            tempDist = shooter.getPosition().distanceOf(position);

            if ((exactDistance && tempDist != distance) ||
                    (!exactDistance && tempDist < distance)) {
                return false;
            }
        }

        return true;
    }
}
