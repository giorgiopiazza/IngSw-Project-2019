package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import utility.CommandUtility;

import java.util.List;

public class ExtraDamageDecorator extends ExtraEffectDecorator {
    private final int[] damageDistribution;
    private final TargetType targetType;

    public ExtraDamageDecorator(Effect effect, int[] extraDamageDistribution, TargetType targetType) {
        this.effect = effect;
        this.damageDistribution = extraDamageDistribution;
        this.targetType = targetType;
    }

    /**
     * Method that spreads the damages of the effect to all targets.
     * A target can be {@code PLAYER}, {@code SQUARE} or {@code ROOM} and the damage
     * distribution depends on this.
     *
     * @param command the command that will be used to damage players
     */
    @Override
    public void execute(String command) {
        effect.execute(command);

        List<Integer> targetsID;
        String[] splitCommand = command.split(" ");
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getCommandUserID(splitCommand));

        switch (targetType) {
            case PLAYER:
                targetsID = CommandUtility.getAttributesID(splitCommand, "-t");
                for (int i = 0; i < targetsID.size(); ++i) {
                    Game.getInstance().getPlayerByID(targetsID.get(i)).getPlayerBoard().addDamage(shooter, damageDistribution[i]);
                }
                break;
            case SQUARE:
                List<PlayerPosition> squares = CommandUtility.getPositions(splitCommand, "-v");
                for (int i = 0; i < squares.size(); ++i) {
                    Player[] targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
                    for (Player damaged : targetSquare) {
                        damaged.getPlayerBoard().addDamage(shooter, damageDistribution[i]);
                    }
                }
                break;
            default:
                List<Player> targetRoom = Game.getInstance().getGameMap().getPlayersInRoom(CommandUtility.getRoomColor(splitCommand));
                for (Player damaged : targetRoom) {
                    damaged.getPlayerBoard().addDamage(shooter, damageDistribution[0]);
                }
        }
    }
}
