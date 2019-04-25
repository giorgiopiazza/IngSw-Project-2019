package utility;

import enumerations.Direction;
import enumerations.TargetType;
import exceptions.player.NoDirectionException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;

import java.util.*;

public class PropertiesValidator {

    /**
     * Method that returns true if all the targets are visible by the Player
     *
     * @param shooterID the ID of the shooting player you want to know if he sees his targets
     * @param targetsID the IDs of the targets you want to know if seen by the player
     * @return true if the shooter can see the targets, otherwise false
     */
    public static boolean areVisible(int shooterID, List<Integer> targetsID) {
        Player shooter = Game.getInstance().getPlayerByID(shooterID);
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        for (Player target : targets) {
            if (!shooter.canSee(target)) {
                // CARE false is returned even if just one target can not be seen by the shooter
                // use this method to understand if the target is not visible only for SINGLE TARGETS!
                return false;
            }
        }

        return true;
    }

    /**
     * Method that returns true if the Players passed are ConcatenatedVisible
     * Players are concatenated visible when the first one (in our need the shooter)
     * can see the second, the second the first and so on
     *
     * @param shooterID the ID of the shooting player you want to know if sees the first target
     * @param targetsID the IDs of the targets you want to know if are ConcatenatedVisible
     * @return true if targets are ConcatenatedVisible, otherwise false
     */
    public static boolean areConcatenatedVisible(int shooterID, List<Integer> targetsID) {
        Player shooter = Game.getInstance().getPlayerByID(shooterID);
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

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
    }

    /**
     * Method that returns true or false depending on the position of the targets.
     * True is returned if targets are inLine (a player in the same room of the shooter
     * is always inline with him), otherwise false
     *
     * @param shooterID the ID of the shooting player
     * @param targetsID the IDs of the targets you want to know if inLine with the shooter
     * @return true if targets are inLine, otherwise false
     * @throws NoDirectionException if the targets are not directionally orientated
     */
    public static boolean areInLine(int shooterID, List<Integer> targetsID) throws NoDirectionException {
        Player shooter = Game.getInstance().getPlayerByID(shooterID);
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        if (targetsID.size() == 1) {
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
                    if (!shooter.getPosition().equals(targets.get(i).getPosition()) &&
                            !directionChosen.equals(targets.get(j).getPosition())) {
                        return false;
                    }
                }
                return true;
            }
        }

        return true;
    }

    /**
     * Method that verifies if each target is distant from the shooter exactly the distance passed
     *
     * @param shooterID the ID of the shooting player
     * @param targetsID the IDs of the targets you want to know their distance from the shooter
     * @param distance  the distance to verify
     * @return true if each target is distant distance from the shooter, otherwise false
     */
    public static boolean areDistant(int shooterID, List<Integer> targetsID, int distance) {
        Player shooter = Game.getInstance().getPlayerByID(shooterID);
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        for (int i = 0; i < targets.size(); ++i) {
            /* TODO delete comment when distanceOf is ready for two positions
            if (shooter.getPosition().distanceOf(targets.get(i).getPosition()) != distance) {
                return false;
            }
             */
        }

        return true;
    }

    /**
     * Method that verifies if each target has a distance from the shooter that is higher than minDisance
     *
     * @param shooterID   the ID of the shooting player
     * @param targetsID   the IDs of the targets you want to know their distance from the shooter
     * @param minDistance the minDistance to verify
     * @return true if each target is distant from the shooter a disance that is higher than minDistance, otherwise false
     */
    public static boolean areMinDistant(int shooterID, List<Integer> targetsID, int minDistance) {
        Player shooter = Game.getInstance().getPlayerByID(shooterID);
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);

        for (int i = 0; i < targets.size(); ++i) {
            /* TODO delete comment when distanceOf is ready for two positions
            if (shooter.getPosition().distanceOf(targets.get(i).getPosition()) < minDistance) {
                return false;
            }
             */
        }

        return true;
    }

    /**
     * Method used to verify if the targets are all in the same position
     *
     * @param targetsID the IDs of the targets to verify
     * @return true if all the targets are in the same position, otherwise false
     */
    public static boolean inSamePosition(List<Integer> targetsID) {
        List<Player> targets = CommandUtility.getPlayersByIDs(targetsID);
        PlayerPosition testingPos = targets.get(0).getPosition();

        for (int i = 1; i < targets.size(); ++i) {
            if (!testingPos.equals(targets.get(i).getPosition())) {
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

        /* TODO delete comment when distanceOf is ready for two positions
        return (movingPlayer.getPosition().distanceOf(position) == move);
         */

        /* DELETE THIS */ return true; /* DELETE THIS */
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
            /* TODO delete comment when distanceOf is ready for two positions
            if (targets.get(i).getPosition().distanceOf(targetsPos.get(i)) != move) {
                return false;
            }
             */
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
            /* TODO delete comment when distanceOf is ready for two positions
            if (targets.get(i).getPosition().distanceOf(targetsPos.get(i)) > maxMove) {
                return false;
            }
             */
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