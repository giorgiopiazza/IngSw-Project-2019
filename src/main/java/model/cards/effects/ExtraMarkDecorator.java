package model.cards.effects;

public class ExtraMarkDecorator extends ExtraEffectDecorator {
    private int[] extraMarkDistribution;

    public ExtraMarkDecorator(Effect effect, int[] extraMarkDistribution) {
        this.effect = effect;
        this.extraMarkDistribution = extraMarkDistribution;
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
