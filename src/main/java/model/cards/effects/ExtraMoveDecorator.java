package model.cards.effects;

import enumerations.MoveTarget;

public class ExtraMoveDecorator extends ExtraEffectDecorator {
    private final MoveTarget moveTarget;

    public ExtraMoveDecorator(Effect effect, MoveTarget moveTarget) {
        this.effect = effect;
        this.moveTarget = moveTarget;
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
