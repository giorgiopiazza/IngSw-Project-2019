package utility;

import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;

import java.util.List;

public class CommandValidator {

    /**
     * Method that verifies the conformity of the command with the target.
     * For example an effect whose target is a Player can not have a command that specifies a room as target
     *
     * @param command String containing the command
     * @param target the target to verify
     * @return true if the command contains the right parameters for the target
     */
    public static boolean targetValidate(String command, TargetType target) {
        switch (target) {
            case PLAYER:
                if(command.contains("-t")) {
                    if (command.contains("-v")) throw new InvalidCommandException();
                    if (command.contains("-x")) throw new InvalidCommandException();

                    return true;
                } else throw new InvalidCommandException();
            case SQUARE:
                if(command.contains("-v")) {
                    if (command.contains("-t")) throw new InvalidCommandException();
                    if (command.contains("-x")) throw new InvalidCommandException();

                    return true;
                } else throw new InvalidCommandException();
            default:
                if(command.contains("-x")) {
                    if (command.contains("-t")) throw new InvalidCommandException();
                    if (command.contains("-v")) throw new InvalidCommandException();

                    return true;
                } else throw new InvalidCommandException();
        }
    }

    /**
     * Method that verifies if the effect can shoot to the number of targets
     * in the command
     *
     * @param command the String containing the command
     * @param target array of TargetType specifying the type of target the effects can shoot
     * @param number int that states the number of targets that the effect can have
     * @param exactNumber boolean, if true the number of target is exactly number, if false number is the maximum
     * @return true if the number of targets is compatible with number
     */
    public static boolean targetNum(String command, TargetType target, int number, boolean exactNumber) {

        if(target == null) throw new NullPointerException();

        switch (target) {
            case PLAYER:
                List<Integer> targetsIDs = CommandUtility.getAttributesID(command.split(" "), "-t");

                if(exactNumber) {
                    return (targetsIDs.size() == number);
                } else {
                    return (targetsIDs.size() <= number);
                }
            case SQUARE:
                List<PlayerPosition> squares = CommandUtility.getPositions(command.split(" "), "-v");

                if(exactNumber) {
                    return (squares.size() == number);
                } else {
                    return (squares.size() <= number);
                }
            default:
                List<Integer> rooms = CommandUtility.getAttributesID(command.split(" "), "-x");

                if(exactNumber) {
                    return (rooms.size() == number);
                } else {
                    return (rooms.size() <= number);
                }
        }
            /*
            int targetsNum = targets.length;
            if(targetsNum == 2) {
                if((targets[0].equals(TargetType.PLAYER) && targets[1].equals(TargetType.SQUARE)) ||
                    targets[0].equals(TargetType.SQUARE) && targets[1].equals(TargetType.PLAYER)) {
                    if(command.contains("-x")) throw new InvalidCommandException();

                    List<Integer> targetsIDs = CommandUtility.getAttributesID(command.split(" "), "-t");
                    List<PlayerPosition> squares = CommandUtility.getPositions(command.split(" "), "-v");

                    if(exactNumber) {
                        return (targetsIDs.size() == number) && (squares.size() == number);
                    } else {
                        return (targetsIDs.size() <= number) && (squares.size() <= number);
                    }


                }

                if((targets[0].equals(TargetType.PLAYER) && targets[1].equals(TargetType.ROOM)) ||
                    targets[0].equals(TargetType.ROOM) && targets[1].equals(TargetType.PLAYER)) {
                    if(command.contains("-v")) throw new InvalidCommandException();

                    List<Integer> targetsIDs = CommandUtility.getAttributesID(command.split(" "), "-t");
                    List<Integer> rooms = CommandUtility.getAttributesID(command.split(" "), "-x");

                    if(exactNumber) {
                        return (targetsIDs.size() == number) && (rooms.size() == number);
                    } else {
                        return (targetsIDs.size() <= number) && (rooms.size() <= number);
                    }

                }

                if((targets[0].equals(TargetType.SQUARE) && targets[1].equals(TargetType.ROOM)) ||
                    targets[0].equals(TargetType.ROOM) && targets[1].equals(TargetType.SQUARE)) {
                    if(command.contains("-t")) throw new InvalidCommandException();

                    List<PlayerPosition> squares = CommandUtility.getPositions(command.split(" "), "-v");
                    List<Integer> rooms = CommandUtility.getAttributesID(command.split(" "), "-x");

                    if(!exactNumber) {
                        return (squares.size() <= number) && (rooms.size() <=number);
                    } else {
                        return (squares.size() == number) && (rooms.size() ==number);
                    }
                }

             */
    }

    public static boolean areFar(String command, TargetType target, int distance, boolean exactDistance) {
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(command.split("")));

        if(target == null) throw new NullPointerException();

        switch (target) {
            case PLAYER:
                List<Player> targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(command.split(" "), "-t"));

                for(int i = 0; i < targets.size(); ++i) {
                    /* TODO delete comment when distanceOf is ready for two positions
                    if(exactDistance) {
                        if(shooter.getPosition().distanceOf(targets.get(i).getPosition()) != distance) {
                            return false;
                        }
                    } else {
                        if(shooter.getPosition().distanceOf(targets.get(i).getPosition()) < distance) {
                            return false;
                        }
                    }
                     */
                }

                return true;
            case SQUARE:
                List<PlayerPosition> squaresPositions = CommandUtility.getPositions(command.split(" "), "-v");

                for(int i = 0; i < squaresPositions.size(); ++i) {
                    /* TODO delete comment when distanceOf is ready for two positions
                    if(exactDistance) {
                        if(shooter.getPosition().distanceOf(squaresPositions.get(i)) != distance) {
                            return false;
                        }
                    } else {
                        if(shooter.getPosition().distanceOf(squaresPositions.get(i)) < distance) {
                            return false;
                        }
                    }
                     */
                }

                return true;
            default:
                // rooms do not have a definition of distance
                throw new InvalidCommandException();
        }
    }
}
