package model.cards.effects;

import enumerations.TargetType;

public class ExtraMarkDecorator extends ExtraEffectDecorator {
    private final int[] extraMarkDistribution;
    private final TargetType targetType;

    public ExtraMarkDecorator(Effect effect, int[] extraMarkDistribution, TargetType targetType) {
        this.effect = effect;
        this.extraMarkDistribution = extraMarkDistribution;
        this.targetType = targetType;
    }

    @Override
    public void execute(String command) {
        effect.execute(command);

        /*
        if (extraMarkDistribution.length > 1) {
            IntStream.range(0, targets.size()).forEach(i -> targets.get(i).getPlayerBoard().addMark(markDealer, extraMarkDistribution[i]));
        } else {
            IntStream.range(0, targets.size()).forEach(i -> targets.get(i).getPlayerBoard().addMark(markDealer, extraMarkDistribution[0]));
        }*/
    }
}
