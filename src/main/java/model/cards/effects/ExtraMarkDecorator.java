package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import utility.CommandUtility;

import java.util.List;

public class ExtraMarkDecorator extends ExtraEffectDecorator {
    private final int[] markDistribution;
    private final TargetType targetType;

    public ExtraMarkDecorator(Effect effect, int[] markDistribution, TargetType targetType) {
        this.effect = effect;
        this.markDistribution = markDistribution;
        this.targetType = targetType;
    }

    /**
     * Method that spreads the marks of the effect to the targets.
     * A target can be {@code PLAYER}, {@code SQUARE} or {@code ROOM} and the mark
     * distribution depends on this.
     *
     * @param command the command that will be used to mark players.
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
                    Game.getInstance().getPlayerByID(targetsID.get(i)).getPlayerBoard().addMark(shooter, markDistribution[i]);
                }
                break;
            case SQUARE:
                List<PlayerPosition> squares = CommandUtility.getPositions(splitCommand, "-v");
                for (int i = 0; i < squares.size(); ++i) {
                    Player[] targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
                    for (Player marked : targetSquare) {
                        marked.getPlayerBoard().addMark(shooter, markDistribution[i]);
                    }
                }
                break;
            default:
                List<Player> targetRoom = Game.getInstance().getGameMap().getPlayersInRoom(CommandUtility.getRoomColor(splitCommand));
                for (Player marked : targetRoom) {
                    marked.getPlayerBoard().addMark(shooter, markDistribution[0]);
                }
        }
    }
}
