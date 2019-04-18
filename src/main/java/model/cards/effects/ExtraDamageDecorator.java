package model.cards.effects;

public class ExtraDamageDecorator extends ExtraEffectDecorator {
    private final int[] damageDistribution;

    public ExtraDamageDecorator(Effect effect, int[] extraDamageDistribution) {
        this.effect = effect;
        this.damageDistribution = extraDamageDistribution;
    }

    @Override
    public void execute(String command) {
        effect.execute(command);

        /*
        if (damageDistribution.length > 1) {
            IntStream.range(0, targets.size()).forEach(i -> targets.get(i).getPlayerBoard().addDamage(damageDealer, damageDistribution[i]));
        } else {
            IntStream.range(0, targets.size()).forEach(i -> targets.get(i).getPlayerBoard().addDamage(damageDealer, damageDistribution[0]));
        }*/
    }
}
