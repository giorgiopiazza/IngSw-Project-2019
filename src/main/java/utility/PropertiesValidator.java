package utility;

import enumerations.Color;
import enumerations.Direction;
import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import exceptions.player.NoDirectionException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;

import java.util.*;

public class PropertiesValidator {

    private PropertiesValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Method that verifies if the startingPos can see all the other positions
     *
     * @param startingPos PLayerPosition from where you need to see
     * @param positions ArrayList of PlayerPositions you want to know if visible
     * @return true if the positions are visible, otherwise false
     */
    public static boolean areVisible(PlayerPosition startingPos, List<PlayerPosition> positions) {
        for (PlayerPosition position : positions) {
            if (!startingPos.canSee(position)) {
                // CARE false is returned even if just one target can not be seen by the shooter
                // use this method to understand if the target is not visible only for SINGLE TARGETS!
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies if the shooter and the targets of TargetType target
     * contained in the command are visible
     *
     * @param command String containing the command
     * @param target TargetType to verify visibility in command
     * @return true if the target is visible, otherwise false
     */
    public static boolean areVisible(String command, TargetType target, Boolean moveFirst) {
        String[] splitCommand = command.split(" ");
        PlayerPosition shooterPos;

        if(moveFirst) {
            shooterPos = CommandUtility.getPositions(command.split(" "), "-m").get(0);
        } else {
            shooterPos = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(splitCommand)).getPosition();
        }


        switch (target) {
            case PLAYER:
                List<Player> targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(splitCommand, "-t"));
                for (Player targetPlayer : targets) {
                    if (!shooterPos.canSee(targetPlayer.getPosition())) {
                        // CARE false is returned even if just one targetPlayer can not be seen by the shooter
                        // use this method to understand if the target is NOT visible only for SINGLE TARGETS!
                        return false;
                    }
                }
                break;
            case SQUARE:
                List<PlayerPosition> squaresPositions = CommandUtility.getPositions(splitCommand, "-v");
                for(PlayerPosition targetSquare : squaresPositions) {
                    if(!shooterPos.canSee(targetSquare)) {
                        // CARE also in this case false is returned if just one targetSquare can not be seen by the shooter
                        // use this method to understand if the target is NOT visible only for SINGLE TARGETS!
                        return false;
                    }
                }
                break;
            default: // ROOM
                Color roomColor = CommandUtility.getRoomColor(splitCommand);
                List<PlayerPosition> roomSquares = Game.getInstance().getGameMap().getRoom(roomColor);

                for(PlayerPosition targetSquareInRoom : roomSquares) {
                    // if a player can see a square of a room he can see also all other squares in the same room
                    if(shooterPos.canSee(targetSquareInRoom)) {
                        return true;
                    }
                }

                return false;
        }

        return true;
    }

    /**
     * Method that verifies if the positions are concatenatedVisible with the startingPos
     *
     * @param startingPos PLayerPosition beginning of the chain
     * @param positions the positions you want to see if are concatenatedVisible
     * @return true if all the positions are concatenated visible, otherwise false
     */
    public static boolean areConcatenatedVisible(PlayerPosition startingPos, List<PlayerPosition> positions) {
        PlayerPosition tempVisiblePos;

        if (positions.size() == 1) {
            return startingPos.canSee(positions.get(0));
        }

        tempVisiblePos = startingPos;
        for (int i = 0; i < positions.size(); ++i) {
            if (!tempVisiblePos.canSee(positions.get(i))) {
                return false;
            }
            tempVisiblePos = positions.get(i);
        }

        return true;
    }

    /**
     * Method that verifies if the targets are concatenated visible,
     * always working on the position of the target
     *
     * @param command String containing the command
     * @param target TargetType to verify concatenated visibility in command
     * @return true if targets are concatenatedVisible, otherwise false
     */
    public static boolean areConcatenatedVisible(String command, TargetType target) {
        String[] splitCommand = command.split(" ");
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(splitCommand));

        switch (target) {
            case PLAYER:
                List<Player> targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(splitCommand, "-t"));
                Player tempVisible;

                if (targets.size() == 1) {
                    return shooter.canSee(targets.get(0));
                } else {
                    tempVisible = shooter;
                    for (int i = 0; i < targets.size(); ++i) {
                        if (!tempVisible.canSee(targets.get(i))) {
                            return false;
                        }
                        tempVisible = targets.get(i);
                    }
                }
                return true;
            case SQUARE:
                List<PlayerPosition> squaresPositions = CommandUtility.getPositions(splitCommand, "-v");
                PlayerPosition tempVisibleSquare;

                if(squaresPositions.size() == 1) {
                    return shooter.getPosition().canSee(squaresPositions.get(0));
                } else {
                    tempVisibleSquare = shooter.getPosition();
                    for (int i = 0; i < squaresPositions.size(); ++i) {
                        if(!tempVisibleSquare.canSee(squaresPositions.get(i))) {
                            return false;
                        }
                        tempVisibleSquare = squaresPositions.get(i);
                    }
                }
                break;
            default:    // ROOM
                // rooms can never be concatenated visible
                throw new InvalidCommandException();

        }

        return true;
    }

    /**
     * Method that verifies if the shooter is inLine with his targets
     *
     * @param command the String containing the command
     * @param target TargetType the shooter needs to be inLine with
     * @return true if the shooter is inLine with the target, otherwise false
     * @throws NoDirectionException in case the target is not valid to identify a direction
     */
    public static boolean areInLine(String command, TargetType target) throws NoDirectionException {
        String[] splitCommand = command.split(" ");
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(splitCommand));

        switch (target) {
            case PLAYER:
                List<Player> targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(splitCommand, "-t"));

                if (targets.size() == 1) {
                    try {
                        shooter.getPosition().getDirection(targets.get(0).getPosition());
                        return true;
                    } catch (NoDirectionException e) {
                        throw new NoDirectionException();
                    }
                }

                for (int i = 0; i < targets.size(); ++i) {
                    if (!shooter.getPosition().equals(targets.get(i).getPosition())) {
                        Direction directionChosen = shooter.getPosition().getDirection(targets.get(i).getPosition());
                        for (int j = i + 1; j < targets.size(); ++j) {
                            if (!shooter.getPosition().equals(targets.get(j).getPosition()) &&
                                    !directionChosen.equals(shooter.getPosition().getDirection(targets.get(j).getPosition()))) {
                                return false;
                            }
                        }
                        return true;
                    }
                }

                return true;
            case SQUARE:
                List<PlayerPosition> squares = CommandUtility.getPositions(splitCommand, "-v");

                if(squares.size() == 1) {
                    try {
                        shooter.getPosition().getDirection(squares.get(0));
                        return true;
                    } catch (NoDirectionException e) {
                        throw new NoDirectionException();
                    }
                }

                for(int i = 0; i < squares.size(); ++i) {
                    if(!shooter.getPosition().equals(squares.get(i))) {
                        Direction directionChosen = shooter.getPosition().getDirection(squares.get(i));
                        for(int j = i + 1; j < squares.size(); ++j) {
                            if(!shooter.getPosition().equals(squares.get(j)) &&
                                    !directionChosen.equals(shooter.getPosition().getDirection(squares.get(j)))) {
                                return false;
                            }
                        }
                        return true;
                    }
                }

                return true;
            default:    // ROOM
                throw new NoDirectionException();
        }
    }

    /**
     * Method that verifies if the ArrayList of position passed are all the same
     *
     * @param positions ArrayList containing the positions
     * @return true if positions are coincident, otherwise false
     */
    public static boolean inSamePosition(List<PlayerPosition> positions) {
        PlayerPosition testingPos = positions.get(0);

        for(int i = 1; i < positions.size(); ++i) {
            if(!testingPos.equals(positions.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method used to verify if a player can, in a number of moves move, reach the position specified
     *
     * @param playerID the ID of the moving player
     * @param position the position to verify her distance
     * @param move     integer representing the exact distance between the player and his moving position
     * @return true if the player can move to the position, otherwise false
     */
    public static boolean canMove(int playerID, PlayerPosition position, int move) {
        Player movingPlayer = Game.getInstance().getPlayerByID(playerID);

        return (movingPlayer.getPosition().distanceOf(position) == move);
    }

    /**
     * Method used to verify if a list of target players can move to their specified positions in a number
     * of moves move
     *
     * @param targetsID  the IDs of the targets to verify their movement
     * @param targetsPos the positions in which each target should move
     * @param move       integer representing the exact distance between a target player and his moving position
     * @return true if every target player can move to the position, otherwise false
     */
    public static boolean canMove(List<Integer> targetsID, List<PlayerPosition> targetsPos, int move) {
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        for (int i = 0; i < targetsPos.size(); ++i) {
            if (targets.get(i).getPosition().distanceOf(targetsPos.get(i)) != move) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method used to verify if a list of target players can move to their specified position in a number
     * of moves that is less than maxMove
     *
     * @param targetsID  the IDs of the targets to verify their movement
     * @param targetsPos the positions in which each target should move
     * @param maxMove    integer representing the maximum distance between a target player and his moving position
     * @return true if every target player can move to the position, otherwise false
     */
    public static boolean canMaxMove(List<Integer> targetsID, List<PlayerPosition> targetsPos, int maxMove) {
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        for (int i = 0; i < targetsPos.size(); ++i) {
            if (targets.get(i).getPosition().distanceOf(targetsPos.get(i)) > maxMove) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies that at the end of a shoot the shooter is in the
     * same position of his last target
     *
     * @param command String containing the command
     * @param movingTarget boolean true if the lastTarget moves, otherwise false
     * @return true if the shooter is in the same position of the last target, otherwise false
     */
    public static boolean lastTargetPos(String command, boolean movingTarget) {
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(command.split(" ")));

        if(movingTarget) {
            List<PlayerPosition> positions = CommandUtility.getPositions(command.split(" "), "-u");
            if(!shooter.getPosition().equals(positions.get(positions.size() - 1))) {
                return false;
            }
        } else {
            List<Integer> targetsID = CommandUtility.getAttributesID(command.split(" "), "-t");
            Player lastTarget = Game.getInstance().getPlayerByID(targetsID.get(targetsID.size() - 1));
            if(!shooter.samePosition(lastTarget)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies if each movement of each target is done directionally from the shooter
     *
     * @param command String containing the command
     * @return true if each targetPosition identifies a direction from the shooter's one
     */
    public static boolean isMovingDirectionally(String command) {
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(command.split(" ")));
        List<PlayerPosition> positions = CommandUtility.getPositions(command.split(" "), "-u");

        try {
            for (int i = 0; i < positions.size(); ++i) {
                shooter.getPosition().getDirection(positions.get(i));
            }
        } catch (NoDirectionException e) {
            return false;
        }

        return true;
    }

    /**
     * Method used before validating a subEffect. It temporally sets the effect properties only
     * with the ones that the specified target needs
     * At the end of this method the original Map of properties is modified, remember to set it back
     * to the first one if needed
     *
     * @param allProperties the Map containing all properties
     * @param targetOther the starting point of the Map from which we need to remove the properties
     */
    public static void setTempMap(Map<String, String> allProperties, TargetType targetOther) {
        // here a LinkedHashMap is always passed is the iterator working right even if static type is Map?
        boolean foundTarget = false;

        for (Map.Entry<String, String> entry : allProperties.entrySet()) {
            if(foundTarget && !entry.getValue().equals("stop")) {
                allProperties.remove(entry.getKey());
            }

            if(entry.getKey().equals(targetOther.toString())) {
                foundTarget = true;
                allProperties.remove(entry.getKey());
            }
        }

    }
}