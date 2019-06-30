package model.cards.effects;

import enumerations.Direction;
import enumerations.MessageContent;
import enumerations.Properties;
import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import exceptions.player.NoDirectionException;
import exceptions.player.SamePositionException;
import exceptions.utility.InvalidPropertiesException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.EffectRequest;
import network.message.ShootRequest;
import network.message.PowerupRequest;

import java.util.*;

class EffectValidator {

    private EffectValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Method that verifies if the shooterPosition can see all the other positions
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return true if the positions are visible, otherwise false
     */
    private static boolean areAllVisible(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        for (PlayerPosition position : targetPositions) {
            if (!shooterPosition.canSee(position)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies if the shooterPosition can't see all the other positions
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return true if the positions are invisible, otherwise false
     */
    private static boolean areAllInvisible(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        for (PlayerPosition position : targetPositions) {
            if (shooterPosition.canSee(position)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies if the starting position (shooterPosition) is concatenatedVisible
     * with all the others in the List. PlayerPositions are concatenated visible if and only if
     * each position can see the one next to her in the List
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return true if the positions are concatenatedVisible, otherwise false
     */
    private static boolean areConcatenatedVisible(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        PlayerPosition tempVisiblePos;

        if (targetPositions.size() == 1) {
            return shooterPosition.canSee(targetPositions.get(0));
        }

        tempVisiblePos = shooterPosition;
        for (PlayerPosition position : targetPositions) {
            if (!tempVisiblePos.canSee(position)) {
                return false;
            }
            tempVisiblePos = position;
        }

        return true;
    }

    /**
     * Method that verifies if the shooter is inLine with his targets
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return {@code true} if the shooter is inLine with the target {@code false} otherwise
     */
    private static boolean areInLine(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {

        if (targetPositions.size() == 1) {
            try {
                shooterPosition.getDirection(targetPositions.get(0));
                return true;
            } catch (NoDirectionException e) {
                return false;
            } catch (SamePositionException e) {
                return true;
            }
        }

        Direction directionChosen = null;

        for (PlayerPosition playerPosition : targetPositions) {
            try {
                if (!shooterPosition.equals(playerPosition)) {
                    if (directionChosen == null) {
                        directionChosen = shooterPosition.getDirection(playerPosition);
                    } else if (!directionChosen.equals(shooterPosition.getDirection(playerPosition))) {
                        return false;
                    }
                }
            } catch (NoDirectionException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies if the ArrayList of position passed are all the same
     *
     * @param positions ArrayList containing the positions
     * @return true if positions are coincident, otherwise false
     */
    private static boolean areInSamePosition(List<PlayerPosition> positions) {
        PlayerPosition testingPos = positions.get(0);

        for (int i = 1; i < positions.size(); ++i) {
            if (!testingPos.equals(positions.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method used to verify if a player can, in a number of moves move, reach the position specified
     *
     * @param senderUsername of the moving player
     * @param position       the position to verify her distance
     * @param move           integer representing the exact distance between the player and his moving position
     * @return true if the player can move to the position, otherwise false
     */
    private static boolean canMove(String senderUsername, PlayerPosition position, int move) {
        Player movingPlayer = Game.getInstance().getUserPlayerByUsername(senderUsername);

        return (movingPlayer.getPosition().distanceOf(position) > 0 && movingPlayer.getPosition().distanceOf(position) <= move);
    }

    /**
     * Method used to verify if a list of target players can move to their specified positions in a number
     * of moves move
     *
     * @param targetsUsername the usernames of the targets to verify their movement
     * @param targetsPos      the positions in which each target should move
     * @param move            integer representing the distance between a target player and his moving position
     * @param exactMove       boolean to verify the exact or maximum move distance (true -> exact)
     * @return true if every target player can move to the position, otherwise false
     */
    private static boolean canMove(List<String> targetsUsername, List<PlayerPosition> targetsPos, int move, boolean exactMove) {
        if (targetsUsername.isEmpty() || targetsPos.isEmpty() || (targetsUsername.size() != targetsPos.size())) {
            return false;
        }

        List<Player> targets = getPlayersByUsername(targetsUsername);

        for (int i = 0; i < targetsPos.size(); ++i) {
            int distance = targets.get(i).getPosition().distanceOf(targetsPos.get(i));
            if ((exactMove && distance != move) ||
                    (!exactMove && distance > move)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that verifies that at the end of a shoot the shooter is in the
     * same position of his last target
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions PlayerPosition of the targets
     * @return true if the shooter is in the same position of the last target, otherwise false
     */
    private static boolean lastTargetPos(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        return shooterPosition.equals(targetPositions.get(targetPositions.size() - 1));
    }

    /**
     * Method that verifies if each movement of each target is done directionally from the shooter
     *
     * @param request containing the effect request
     * @return true if each targetPosition identifies a direction from the shooter's one
     */
    private static boolean isMovingDirectionally(EffectRequest request) {
        Player shooter = Game.getInstance().getUserPlayerByUsername(request.getSenderUsername());
        List<PlayerPosition> positions = request.getTargetPlayersMovePositions();

        for (PlayerPosition position : positions) {
            try {
                if(request.getContent() == MessageContent.POWERUP_USAGE) {
                    Game.getInstance().getPlayerByName(request.getTargetPlayersUsername().get(0)).getPosition().getDirection(position);
                } else {
                    shooter.getPosition().getDirection(position);
                }

            } catch (NoDirectionException e) {
                return false;
            } catch (SamePositionException e) {
                // Same position is OK
            }
        }

        return true;
    }

    /**
     * Method that verifies if each target position is far enough from the shooter's one
     * Using the boolean attribute exactDistance the method splits in two different validations:
     * if true the distance passed must be the same for all positions, if false it is the minimum
     * distance from which the positions have to be from the shooter one
     *
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions List of PlayerPositions of the targets
     * @param targetType      TargetType of the target positions
     * @param distance        int specifying the distance
     * @param exactDistance   boolean to verify the exact or minimum distance (true -> exact)
     * @return true if all the target positions are distant enough from the shooting one, otherwise false
     */
    private static boolean isDistantEnough(PlayerPosition shooterPosition, List<PlayerPosition> targetPositions, TargetType targetType, int distance, boolean exactDistance) {
        int tempDist;

        if (targetType == null) return false;

        for (PlayerPosition position : targetPositions) {
            tempDist = shooterPosition.distanceOf(position);

            if (targetType == TargetType.PLAYER || targetType == TargetType.SQUARE) {
                if ((exactDistance && tempDist != distance) ||
                        (!exactDistance && tempDist < distance)) {
                    return false;
                }
            } else {    // this is ROOM!
                if ((Game.getInstance().getGameMap().getSquare(shooterPosition).getRoomColor() == Game.getInstance().getGameMap().getSquare(position).getRoomColor())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns a sub map of the properties with only the properties of a sub effect
     *
     * @param allProperties the Map containing all properties
     * @param targetType    the starting point of the Map from which we need to remove the properties
     * @return the sub map with the properties of a sub effect
     */
    static Map<String, String> getSubMap(Map<String, String> allProperties, TargetType targetType) {
        // here a LinkedHashMap is always passed is the iterator working right even if static type is Map?
        boolean foundTarget = false;

        Map<String, String> tempMap = new LinkedHashMap<>(allProperties);

        for (Map.Entry<String, String> entry : allProperties.entrySet()) {
            if (foundTarget && !entry.getValue().equals("stop")) {
                tempMap.remove(entry.getKey());
            }

            if (entry.getKey().equals(targetType.toString())) {
                foundTarget = true;
                tempMap.remove(entry.getKey());
            }
        }

        return tempMap;
    }

    /**
     * Checks if target moves are valid based on effect properties
     *
     * @param request    containing the effect request
     * @param properties Map of effect properties
     * @return {@code true} if target moves are valid {@code false} otherwise
     */
    static boolean isMoveValid(EffectRequest request, Map<String, String> properties) {
        // Player move validation
        if (properties.containsKey(Properties.MOVE.getJKey())) {
            PlayerPosition playerMovingPos = request.getSenderMovePosition();
            String senderUsername = request.getSenderUsername();
            int moveDistance = Integer.parseInt(properties.get(Properties.MOVE.getJKey()));

            if (playerMovingPos == null || !EffectValidator.canMove(senderUsername, playerMovingPos, moveDistance)) {
                return false;
            }
        }

        // Target move validation
        List<String> targetsUsername = request.getTargetPlayersUsername();
        List<PlayerPosition> movingPos = request.getTargetPlayersMovePositions();

        int moveDistance;

        if (properties.containsKey(Properties.MOVE_TARGET.getJKey())) {
            moveDistance = Integer.parseInt(properties.get(Properties.MOVE_TARGET.getJKey()));

            if (movingPos.isEmpty() || !EffectValidator.canMove(targetsUsername, movingPos, moveDistance, true)) {
                return false;
            }
        }

        if (properties.containsKey(Properties.MAX_MOVE_TARGET.getJKey())) {
            moveDistance = Integer.parseInt(properties.get(Properties.MAX_MOVE_TARGET.getJKey()));

            if (movingPos.isEmpty() || !EffectValidator.canMove(targetsUsername, movingPos, moveDistance, false)) {
                return false;
            }
        }

        // Target MoveInLine validation
        return !(properties.containsKey(Properties.MOVE_INLINE.getJKey()) && !EffectValidator.isMovingDirectionally(request));
    }

    /**
     * Checks if the Weapon acts correctly with a movement done in the middle of the same damaging action
     *
     * @param request containing the effect request
     * @return {@code true} if the movement is correct, otherwise {@code false}
     */
    static boolean isMoveInMiddleValid(EffectRequest request) {
        PlayerPosition shooterStartingPos = Game.getInstance().getPlayerByName(request.getSenderUsername()).getPosition();
        PlayerPosition shooterEndingPos = request.getSenderMovePosition();
        PlayerPosition startingTargetPos = Game.getInstance().getPlayerByName(request.getTargetPlayersUsername().get(0)).getPosition();
        PlayerPosition arrivingTargetPos = Game.getInstance().getPlayerByName(request.getTargetPlayersUsername().get(1)).getPosition();

        return shooterStartingPos.equals(startingTargetPos) && shooterEndingPos.equals(arrivingTargetPos);
    }

    /**
     * Method that verifies if the distance between the shooter and the target positions are valid due to
     * the properties found in the Map properties and the TargetType of the targets
     *
     * @param properties      Map containing the properties of the effect
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions List of PlayerPosition of the targets
     * @param targetType      TargetType of the targets
     * @return true if all the target positions are valid with the shooting one, otherwise false
     */
    static boolean isDistanceValid(Map<String, String> properties, PlayerPosition shooterPosition, List<PlayerPosition> targetPositions, TargetType targetType) {
        // Distance validation
        int distance;
        boolean exactDistance;

        if (properties.containsKey(Properties.DISTANCE.getJKey())) { // Exact target distance
            distance = Integer.parseInt(properties.get(Properties.DISTANCE.getJKey()));
            exactDistance = true;
        } else if (properties.containsKey(Properties.MIN_DISTANCE.getJKey())) { // Minimum target distance
            distance = Integer.parseInt(properties.get(Properties.MIN_DISTANCE.getJKey()));
            exactDistance = false;
        } else {
            return true;
        }

        return isDistantEnough(shooterPosition, targetPositions, targetType, distance, exactDistance);
    }

    /**
     * Method that verifies the poritioning properties of the effect: inLine and moveToLastTarget
     *
     * @param properties      Map containing the properties of the effect
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions List of PlayerPosition of the targets
     * @return true if both or only one of the two properties are verified, otherwise false
     */
    static boolean isPositioningValid(Map<String, String> properties, PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        return !((properties.containsKey(Properties.INLINE.getJKey()) && !areInLine(shooterPosition, targetPositions)) || // InLine targets validation
                (properties.containsKey(Properties.MOVE_TO_LAST_TARGET.getJKey()) && lastTargetPos(shooterPosition, targetPositions))); // Move to last target validation
    }

    /**
     * Method that verifies the visibility from the shooter position to the targets' ones
     * Both visibilities are validated in this method taking care that an effect can never
     * have them together in the same Map of properties
     *
     * @param properties      Map containing the properties of the effect
     * @param shooterPosition PlayerPosition of the shooter
     * @param targetPositions List of PlayerPosition of the targets
     * @return true if the visibility is verified for each target position, otherwise false
     */
    static boolean isVisibilityValid(Map<String, String> properties, PlayerPosition shooterPosition, List<PlayerPosition> targetPositions) {
        return !((properties.containsKey(Properties.SAME_POSITION.getJKey()) && !areInSamePosition(targetPositions)) || // Targets Same Position
                (properties.containsKey(Properties.VISIBLE.getJKey()) &&
                        (!Boolean.parseBoolean(properties.get(Properties.VISIBLE.getJKey())) &&
                                !areAllInvisible(shooterPosition, targetPositions) || // Visible property == false and at least one target is visible
                                        (Boolean.parseBoolean(properties.get(Properties.VISIBLE.getJKey())) &&
                                                !areAllVisible(shooterPosition, targetPositions)))) || // Visible property == true and at least one target is invisible
                (properties.containsKey(Properties.CONCATENATED_VISIBLE.getJKey()) && !areConcatenatedVisible(shooterPosition, targetPositions))); // Concatenated visibility
    }

    /**
     * Returns an ArrayList of the players whose username is contained in the List passed
     *
     * @param playersUsername the List of usernames you need the related players' reference
     * @return an ArrayList of players
     */
    static List<Player> getPlayersByUsername(List<String> playersUsername) {
        if (playersUsername == null) throw new NullPointerException("Can not take any player from null");

        List<Player> players = new ArrayList<>();
        for (String playerUsername : playersUsername) {
            players.add(Game.getInstance().getUserPlayerByUsername(playerUsername));
        }

        return players;
    }

    /**
     * Return a list of PlayerPosition from the effect request and the target type
     *
     * @param request    containing the effect request
     * @param targetType desired
     * @return an ArrayList of PlayerPositions
     */
    static List<PlayerPosition> getTargetPositions(EffectRequest request, TargetType targetType) {
        List<PlayerPosition> squares;
        List<Player> targets;

        switch (targetType) {
            case PLAYER:
                targets = getPlayersByUsername(request.getTargetPlayersUsername());
                squares = new ArrayList<>();

                for (Player targetPlayer : targets) {
                    squares.add(targetPlayer.getPosition());
                }

                break;
            case SQUARE:
                squares = request.getTargetPositions();
                break;
            default:
                targets = Game.getInstance().getGameMap().getPlayersInRoom(request.getTargetRoomColor());
                squares = new ArrayList<>();

                for (Player targetPlayer : targets) {
                    squares.add(targetPlayer.getPosition());
                }
        }

        return squares;
    }

    /**
     * Method that verifies the conformity of the command with the target.
     * For example an effect whose target is a Player can not have a command that specifies a room as target
     *
     * @param request    containing the effect request
     * @param targetType the target to verify
     * @return true if the command contains the right parameters for the target
     * @throws NullPointerException    if target is null
     * @throws InvalidCommandException if the command is invalid
     */
    private static boolean isTargetTypeValid(EffectRequest request, TargetType targetType) {
        if (targetType == null) return false;

        switch (targetType) {
            case PLAYER:
                if (request.getTargetPlayersUsername().isEmpty()) {
                    return false;
                } else {
                    if (hasAutoShoot(request)) {
                        return false;
                    }
                }
                break;
            case SQUARE:
                if (request.getTargetPositions().isEmpty()) {
                    return false;
                }
                break;
            default:
                if (request.getTargetRoomColor() == null) {
                    return false;
                }
        }

        return true;
    }

    private static boolean hasAutoShoot(EffectRequest request) {
        for (String username : request.getTargetPlayersUsername()) {
            if (username.equals(request.getSenderUsername())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method that verifies if the effect can shoot to the number of targets
     * in the command
     *
     * @param request     containing the effect request
     * @param targetType  array of TargetType specifying the type of target the effects can shoot
     * @param number      int that states the number of targets that the effect can have
     * @param exactNumber boolean, if true the number of target is exactly number, if false number is the maximum
     * @return true if the number of targets is compatible with number
     * @throws NullPointerException if target is null
     */
    private static boolean isTargetNumValid(EffectRequest request, TargetType targetType, int number, boolean exactNumber) {
        int targetNum;

        if (targetType == null) throw new NullPointerException();

        switch (targetType) {
            case PLAYER:
                List<String> targetUsernames = request.getTargetPlayersUsername();

                targetNum = targetUsernames.size();
                break;
            case SQUARE:
                List<PlayerPosition> squares = request.getTargetPositions();

                targetNum = squares.size();
                break;
            default:
                // ROOM: Already checked in isTargetTypeValid
                return true;
        }

        return ((exactNumber && targetNum == number) ||
                (!exactNumber && targetNum <= number));
    }

    /**
     * Checks if command targets are valid based on effect properties
     *
     * @param request    containing the effect request
     * @param properties Map of effect properties
     * @param targetType TargetType of the effect target
     * @return {@code true} if command targets are valid {@code false} otherwise
     */
    static boolean isTargetValid(EffectRequest request, Map<String, String> properties, TargetType targetType) {
        // TargetType validation
        if (!isTargetTypeValid(request, targetType)) {
            return false;
        }

        // Target number validation
        int targetNumber;
        boolean exactNumber;
        if (properties.containsKey(Properties.TARGET_NUM.getJKey())) { // Exact target number
            targetNumber = Integer.parseInt(properties.get(enumerations.Properties.TARGET_NUM.getJKey()));
            exactNumber = true;
        } else if (properties.containsKey(Properties.MAX_TARGET_NUM.getJKey())) { // Maximum target number
            targetNumber = Integer.parseInt(properties.get(Properties.MAX_TARGET_NUM.getJKey()));
            exactNumber = false;
        } else {
            throw new InvalidPropertiesException();
        }

        return isTargetNumValid(request, targetType, targetNumber, exactNumber);
    }

    /**
     * Checks if target moves before is congruent with the command
     *
     * @param request    containing the fire request
     * @param properties Map of effect properties
     * @return {@code true} if target move before is valid {@code false} otherwise
     */
    public static boolean isMoveBeforeValid(ShootRequest request, Map<String, String> properties) {
        return !(properties.containsKey(Properties.MOVE_TARGET_BEFORE.getJKey()) &&
                (Boolean.parseBoolean(properties.get(Properties.MOVE_TARGET_BEFORE.getJKey()))
                        != request.isMoveTargetsFirst()));
    }

    /**
     * Checks if the index of the powerup in the command is congruent with the player who
     * is using the powerup
     *
     * @param request containing the powerup request
     * @return {@code true} if the index is valid, otherwise {@code false}
     */
    static boolean isPowerupIndexValid(PowerupRequest request) {
        UserPlayer powerupUser = Game.getInstance().getUserPlayerByUsername(request.getSenderUsername());
        int powerupIndex = request.getPowerup().get(0);

        if (powerupIndex < 0 || powerupIndex > 3) {
            throw new InvalidCommandException();
        } else return powerupUser.getPowerups().length >= powerupIndex;
    }

    static boolean teleporterValidator(PowerupRequest request) {
        // TELEPORTER move is allowed only if the moving position is not the same
        if(Game.getInstance().getPlayerByName(request.getSenderUsername()).getPosition().equals(request.getSenderMovePosition())) {
            return false;
        }

        // then I always have to check for the powerupIndex
        return isPowerupIndexValid(request);
    }

    /**
     * Method that sets the correct {@link PlayerPosition PlayerPosition} for the shooter in case his
     * {@link model.actions.ShootAction ShootAction} is an adrenaline one
     *
     * @param request containing the {@link ShootRequest ShootRequest}
     * @return the correct {@link PlayerPosition PlayerPosition} for the {@link model.actions.ShootAction ShootAction}
     */
    static PlayerPosition checkAdrenalineMove(ShootRequest request) {
        if(request.getMoveBeforeShootPosition() != null) {
            return request.getMoveBeforeShootPosition();
        } else {
            return Game.getInstance().getUserPlayerByUsername(request.getSenderUsername()).getPosition();
        }
    }
}