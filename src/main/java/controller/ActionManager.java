package controller;

import enumerations.PlayerBoardState;
import enumerations.PossibleAction;
import model.Game;
import model.player.UserPlayer;

import java.util.EnumSet;

public class ActionManager {
    /**
     * Method that sets the possible actions for a player whose state is FIRST_SPAWN
     *
     * @param isTerminatorPresent boolean that specifies if the terminator is present in the game
     */
    public static void setStartingPossibleActions(UserPlayer player, boolean isTerminatorPresent) {
        if (player.isFirstPlayer() && isTerminatorPresent) {
            player.setActions(EnumSet.of(PossibleAction.SPAWN_TERMINATOR, PossibleAction.CHOOSE_SPAWN));
        } else if (isTerminatorPresent) {
            player.setActions(EnumSet.of(PossibleAction.CHOOSE_SPAWN, PossibleAction.TERMINATOR_ACTION));
        } else {
            player.setActions(EnumSet.of(PossibleAction.CHOOSE_SPAWN));
        }
    }

    /**
     * Method that sets the possible actions a player has due to his state, when the game is in NORMAL state
     * If the game has the terminator, every player in his turn must always do also the terminator action
     */
    public static void setPossibleActions(UserPlayer player) {
        PlayerBoardState currentPlayerBoardState = player.getPlayerBoard().getBoardState();

        switch (currentPlayerBoardState) {
            case NORMAL:
                player.setActions(EnumSet.of(PossibleAction.MOVE, PossibleAction.MOVE_AND_PICK, PossibleAction.SHOOT));
                break;
            case FIRST_ADRENALINE:
                player.setActions(EnumSet.of(PossibleAction.MOVE, PossibleAction.ADRENALINE_PICK, PossibleAction.SHOOT));
                break;
            default:    // second adrenaline
                player.setActions(EnumSet.of(PossibleAction.MOVE, PossibleAction.ADRENALINE_PICK, PossibleAction.ADRENALINE_SHOOT));
        }

        if (Game.getInstance().isTerminatorPresent()) {
            player.addAction(PossibleAction.TERMINATOR_ACTION);
        }
    }

    /**
     * Method that sets the possible actions a player has due to his position in the round turn,
     * when the game is in FRENZY state
     * If the game has the terminator, every player in his turn must always do also the terminator action
     *
     * @param frenzyActivator the player who activated the final frenzy mode
     */
    public static void setFrenzyPossibleActions(UserPlayer player, UserPlayer frenzyActivator) {
        if (Game.getInstance().getDoubleActionFrenzyPlayers(frenzyActivator).contains(player)) {
            player.setActions(EnumSet.of(PossibleAction.FRENZY_MOVE, PossibleAction.FRENZY_PICK, PossibleAction.FRENZY_SHOOT));
        } else {
            player.setActions(EnumSet.of(PossibleAction.LIGHT_FRENZY_SHOOT, PossibleAction.LIGHT_FRENZY_PICK));
        }

        if (Game.getInstance().isTerminatorPresent()) {
            player.addAction(PossibleAction.TERMINATOR_ACTION);
        }
    }
}
