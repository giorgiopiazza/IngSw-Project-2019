package model.cards.effects;

import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
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

    @Override
    public void execute(String command) {
        effect.execute(command);

        List<Integer> targetsID;
        String[] splitCommand = command.split(" ");
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(splitCommand));

        switch (targetType) {
            case PLAYER:
                if(command.contains("-t")) {
                    targetsID = CommandUtility.getAttributesID(splitCommand, "-t");
                    for (int i = 0; i < targetsID.size(); ++i) {
                        Game.getInstance().getPlayerByID(targetsID.get(i)).getPlayerBoard().addMark(shooter, markDistribution[i]);
                    }
                } else {
                    throw new InvalidCommandException();
                }
                break;
            case SQUARE:
                if(command.contains("-v")) {
                    List<PlayerPosition> squares = CommandUtility.getPositions(splitCommand, "-v");
                    for(int i = 0; i < squares.size(); ++i) {
                        Player[] targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
                        for(Player marked : targetSquare) {
                            marked.getPlayerBoard().addMark(shooter, markDistribution[i]);
                        }
                    }
                }
                break;
            default:
                if(command.contains("-x")) {
                    List<Player> targetRoom = Game.getInstance().getGameMap().getPlayersInRoom(CommandUtility.getRoomColor(splitCommand));
                    for(Player marked : targetRoom) {
                        marked.getPlayerBoard().addMark(shooter, markDistribution[0]);
                    }
                }
        }
    }
}
