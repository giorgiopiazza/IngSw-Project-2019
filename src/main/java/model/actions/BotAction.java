package model.actions;

import exceptions.actions.InvalidActionException;
import model.Game;
import model.player.Bot;
import model.player.Player;
import model.player.PlayerPosition;
import model.player.UserPlayer;

/**
 * Implementation of the Bot Action considering the possible adrenaline states in which the bot can be
 */
public class BotAction implements Action {
    private static final int MAX_BOT_MOVE = 1;
    private static final int BOT_DAMAGE = 1;
    private static final int BOT_ADRENALINE_MARK = 1;
    private final Player bot = Game.getInstance().getBot();
    private UserPlayer actingPlayer;
    private Player targetPlayer;
    private PlayerPosition movingPos;
    private BotState botState;


    /**
     * Builds a bot action, used to execute the bot damage
     *
     * @param actingPlayer the player using the Bot Action
     * @param targetPlayer the target of the Bot Action
     * @param movingPos the {@link PlayerPosition Position} in which the Bot is going to move
     */
    public BotAction(UserPlayer actingPlayer, Player targetPlayer, PlayerPosition movingPos) {
        this.actingPlayer = actingPlayer;
        this.targetPlayer = targetPlayer;

        if (bot.getPosition().equals(movingPos)) {
            this.movingPos = bot.getPosition();
        } else {
            this.movingPos = movingPos;
        }

        if (bot.getPlayerBoard().getDamageCount() < 6) {
            this.botState = BotState.NORMAL;
        } else {
            this.botState = BotState.ADRENALINE;
        }
    }

    /**
     * Implementation of the validate for a Bot Action
     *
     * @return true if the Action is valid, otherwise false
     * @throws InvalidActionException in case the action is invalid due to input validation
     */
    @Override
    public boolean validate() throws InvalidActionException {
        if (actingPlayer.equals(targetPlayer)) {
            throw new InvalidActionException();
        }

        // check that the built position has a valid X coordinate
        if (movingPos.getRow() < 0 || movingPos.getRow() > 2) {
            throw new InvalidActionException();
        }

        // check that the built position has a valid Y coordinate
        if (movingPos.getColumn() < 0 || movingPos.getColumn() > 3) {
            throw new InvalidActionException();
        }

        int movingDistance = bot.getPosition().distanceOf(movingPos);

        return movingAndVisibilityValidation(movingDistance);
    }

    /**
     * Utility method used by the validate to check the combination of a bot action with the
     * move and the shoot
     *
     * @param movingDistance the moving distance between the bot and his moving position
     * @return true if the sub validation is verified, otherwise false
     * @throws InvalidActionException in case the action is invalid due to input validation
     */
    private boolean movingAndVisibilityValidation(int movingDistance) throws InvalidActionException{
        // move and Visibility validation
        if (movingDistance == 0) {
            if (targetPlayer == null) throw new InvalidActionException();

            return bot.canSee(targetPlayer);
        } else if (movingDistance == MAX_BOT_MOVE) {
            if (targetPlayer == null) {
                Bot temp = new Bot((Bot) bot);
                temp.setPosition(movingPos);

                if (movingPos.canSeeSomeone(temp, actingPlayer)) {
                    throw new InvalidActionException();
                } else {
                    return true;
                }
            }

            return movingPos.canSee(targetPlayer.getPosition());
        } else {
            throw new InvalidActionException();
        }
    }

    /**
     * Implementation of the execution of a Bot Action
     */
    @Override
    public void execute() {
        // first I move the bot
        bot.changePosition(movingPos.getRow(), movingPos.getColumn());

        // if the bot can not see anyone his action is ended
        if (targetPlayer == null) {
            return;
        }

        // then I shoot with the bot depending on it's state
        if (botState.equals(BotState.NORMAL)) {
            targetPlayer.getPlayerBoard().addDamage(bot, BOT_DAMAGE);
        } else {
            targetPlayer.getPlayerBoard().addDamage(bot, BOT_DAMAGE);
            targetPlayer.getPlayerBoard().addMark(bot, BOT_ADRENALINE_MARK);
        }
    }

    /**
     * Enumeration used to define the state of the Bot's playerboard as his normal or adrenaline
     * actions can be activated
     */
    enum BotState {
        NORMAL, ADRENALINE
    }

}
