package utility;

import model.Game;
import model.map.GameMap;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.*;
import network.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputValidator {
    private static final int MAX_POWERUPS = 3;

    private InputValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean validatePlayerUsername(List<UserPlayer> players, Message checkingMessage) {
        if (!checkBaseMessage(checkingMessage)) {
            return false;
        }

        for (UserPlayer player : players) {
            if (player.getUsername().equals(checkingMessage.getSenderUsername())) {
                return true;
            }
        }

        return false;
    }

    public static boolean validatePosition(PlayerPosition position) {
        if (position == null) {
            return false;
        }

        // check that the built position has a valid X coordinate
        if (position.getCoordX() < 0 || position.getCoordX() > GameMap.MAX_ROWS - 1) {
            return false;
        }

        // check that the built position has a valid Y coordinate
        if (position.getCoordY() < 0 || position.getCoordY() > GameMap.MAX_COLUMNS - 1) {
            return false;
        }

        if (Game.getInstance().getGameMap().getSquare(position) == null) {
            return false;
        }

        return Game.getInstance().getGameMap().getSquare(position) != null;
    }

    public static boolean validateInput(Message checkingMessage) {
        if (checkingMessage == null) return false;

        if (!checkBaseMessage(checkingMessage)) {
            return false;
        }

        try {
            switch (checkingMessage.getContent()) {
                case GET_IN_LOBBY:
                    return checkNullsInLobby((LobbyMessage) checkingMessage);
                case LOBBY_VOTE:
                    return checkNullsInVote((GameVoteMessage) checkingMessage);
                case BOT_SPAWN:
                    return checkNullsInBotSpawn((TerminatorSpawnRequest) checkingMessage);
                case DISCARD_POWERUP:
                    return checkIndexesInDiscard((DiscardPowerupRequest) checkingMessage);
                case POWERUP_USAGE:
                    return checkNullsInPowerup((PowerupRequest) checkingMessage);
                case BOT_ACTION:
                    return checkNullsInBotAction((UseTerminatorRequest) checkingMessage);
                case MOVE:
                    return checkNullsInMoveAction((MoveRequest) checkingMessage);
                case MOVE_PICK:
                    return checkNullsInPickAction((MovePickRequest) checkingMessage);
                case SHOOT:
                    return checkIndexesInShootAction((ShootRequest) checkingMessage);
                case RELOAD:
                    return checkIndexesInReloadAction((ReloadRequest) checkingMessage);
                default:    // always one of this kind of messages needs to be validated
                    return false;
            }
        } catch (ClassCastException e) {
            Server.LOGGER.severe("Invalid cast of a message from " + checkingMessage.getSenderUsername());
            return false;
        }

    }

    private static boolean checkBaseMessage(Message message) {
        if (message.getSenderUsername() == null) {
            return false;
        }

        if (message.getSenderUsername().equalsIgnoreCase(GameCostants.GOD_NAME) || message.getSenderUsername().equalsIgnoreCase(GameCostants.BOT_NAME)) {
            return false;
        }

        return message.getContent() != null;
    }

    private static boolean checkBaseEffect(EffectRequest effectRequest) {
        if (!effectRequest.getTargetPlayersUsername().isEmpty()) {
            for (String userName : effectRequest.getTargetPlayersUsername()) {
                if (userName == null) {
                    return false;
                }
            }

            if(!checkDuplicatesInArguments(effectRequest.getTargetPlayersUsername())) {
                return false;
            }
        }

        if (!effectRequest.getTargetPlayersMovePositions().isEmpty()) {
            for (PlayerPosition position : effectRequest.getTargetPlayersMovePositions()) {
                if (position == null) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean checkBaseAction(ActionRequest actionRequest) {
        if (!actionRequest.getPaymentPowerups().isEmpty()) {
            for (Integer index : actionRequest.getPaymentPowerups()) {
                if (index < 0 || index > MAX_POWERUPS) {
                    return false;
                }
            }

            return checkDuplicatesInArguments(actionRequest.getPaymentPowerups());
        }

        return true;
    }

    private static boolean checkNullsInLobby(LobbyMessage lobbyMessage) {
        return lobbyMessage.getChosenColor() != null;
    }

    private static boolean checkNullsInVote(GameVoteMessage gameVoteMessage) {
        return true;
    }

    private static boolean checkNullsInBotSpawn(TerminatorSpawnRequest terminatorSpawnRequest) {
        return terminatorSpawnRequest.getSpawnColor() != null;
    }

    private static boolean checkIndexesInDiscard(DiscardPowerupRequest discardPowerupRequest) {
        return discardPowerupRequest.getPowerup() > 0 && discardPowerupRequest.getPowerup() <= MAX_POWERUPS;
    }

    private static boolean checkNullsInPowerup(PowerupRequest powerupRequest) {
        if (!checkBaseEffect(powerupRequest)) {
            return false;
        }

        for (Integer index : powerupRequest.getPowerup()) {
            if (index < 0 || index > MAX_POWERUPS) {
                return false;
            }
        }

        return true;

    }

    private static boolean checkNullsInBotAction(UseTerminatorRequest useTerminatorRequest) {
        return useTerminatorRequest.getTargetPlayer() != null && useTerminatorRequest.getMovingPosition() != null;
    }

    private static boolean checkNullsInMoveAction(MoveRequest moveRequest) {
        if (!checkBaseAction(moveRequest)) {
            return false;
        }

        return moveRequest.getSenderMovePosition() != null;
    }

    private static boolean checkNullsInPickAction(MovePickRequest movePickRequest) {
        if (!checkBaseAction(movePickRequest)) {
            return false;
        }

        return movePickRequest.getSenderMovePosition() != null;
    }

    private static boolean checkIndexesInShootAction(ShootRequest shootRequest) {
        if (!checkBaseAction(shootRequest)) {
            return false;
        }

        if (shootRequest.getWeaponID() < 0 || shootRequest.getWeaponID() > 2) {
            return false;
        }

        if(!checkDuplicatesInArguments(shootRequest.getTargetPlayersUsername())) {
            return false;
        }

        return shootRequest.getEffect() > 0 && shootRequest.getEffect() < 4;
    }

    private static boolean checkIndexesInReloadAction(ReloadRequest reloadRequest) {
        if (!checkBaseAction(reloadRequest)) {
            return false;
        }

        if (!reloadRequest.getWeapons().isEmpty()) {
            for (Integer index : reloadRequest.getWeapons()) {
                if (index < 0 || index > 3) {
                    return false;
                }
            }

            return checkDuplicatesInArguments(reloadRequest.getWeapons());
        } else {
            return false;
        }
    }

    private static boolean checkDuplicatesInArguments(ArrayList<?> checkingList) {
        List<?> distinctList = checkingList.stream().distinct().collect(Collectors.toList());
        return distinctList.size() == checkingList.size();
    }

    public static boolean validateIndexes(ActionRequest actionRequest, UserPlayer turnOwner) {
        if(!actionRequest.getPaymentPowerups().isEmpty()) {
            if(actionRequest.getPaymentPowerups().size() > turnOwner.getPowerups().length) {
                return false;
            }

            for(Integer index : actionRequest.getPaymentPowerups()) {
                if(index > turnOwner.getPowerups().length - 1) {
                    return false;
                }
            }
        }

        return true;
    }
}
