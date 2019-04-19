package model.cards.effects;

import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import utility.CommandUtility;

import java.util.ArrayList;
import java.util.List;

public class ExtraDamageDecorator extends ExtraEffectDecorator {
    private final int[] damageDistribution;
    private final TargetType targetType;

    public ExtraDamageDecorator(Effect effect, int[] extraDamageDistribution, TargetType targetType) {
        this.effect = effect;
        this.damageDistribution = extraDamageDistribution;
        this.targetType = targetType;
    }

    @Override
    public void execute(String command) {
        List<Integer> targets = new ArrayList<>();
        effect.execute(command);

        String[] splitCommand = command.split(" ");
        Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(splitCommand));

        switch (targetType) {
            case PLAYER:
                if(command.contains("-t")) {
                    targets = CommandUtility.getAttributesID(splitCommand, "-t");
                    for (int i = 0; i < targets.size(); ++i) {
                        Game.getInstance().getPlayerByID(targets.get(i)).getPlayerBoard().addDamage(shooter, damageDistribution[i]);
                    }
                } else {
                    throw new InvalidCommandException();
                }
                break;
            case SQUARE:
                if(command.contains("-v")) {
                    List<PlayerPosition> squares = CommandUtility.getPositions(splitCommand, "-v");
                    for(int i = 0; i < squares.size(); ++i) {
                        Player[] target = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
                        for(Player damaged : target) {
                            damaged.getPlayerBoard().addDamage(shooter, damageDistribution[i]);
                        }
                    }
                }
            default:
                //TODO take targets from the given ROOM, add getPlayersInRoom in Map class

        }
        /*
        if (damageDistribution.length > 1) {
            IntStream.range(0, targets.size()).forEach(i -> targets.get(i).getPlayerBoard().addDamage(damageDealer, damageDistribution[i]));
        } else {
            IntStream.range(0, targets.size()).forEach(i -> targets.get(i).getPlayerBoard().addDamage(damageDealer, damageDistribution[0]));
        }*/
    }
}
