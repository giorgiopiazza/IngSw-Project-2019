package model.cards.effects;

public class ExtraMoveDecorator extends ExtraEffectDecorator {
    public ExtraMoveDecorator(Effect effect) {
        this.effect = effect;
    }

    @Override
    public void execute(String command) {
        effect.execute(command);


        /*
        IntStream.range(0, targets.size()).forEach(i -> targets.get(i).changePosition(
                extraPositionDistribution[i].getCoordX(),
                extraPositionDistribution[i].getCoordY()
        ));
        */
    }
}
