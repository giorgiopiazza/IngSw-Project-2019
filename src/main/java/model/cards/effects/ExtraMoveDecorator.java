package model.cards.effects;

import exceptions.cards.PositionDistributionException;
import model.player.Player;
import model.player.PlayerPosition;

/*
 ***************** LOGICA COSTRUTTORE E SETTER DESCRITTE IN model.effects.DamageEffect *****************
 */
public class ExtraMoveDecorator extends ExtraEffectDecorator {

    private PlayerPosition[] extraPositionDistribution;

    public ExtraMoveDecorator(Effect effect, PlayerPosition[] extraPositionDistribution) throws PositionDistributionException{
        this.effect = effect;
        if(effect.target.getRoom().isPresent()) {
            this.extraPositionDistribution = new PlayerPosition[0];
        } else {
            if(extraPositionDistribution.length != effect.target.getTargets().length) throw new PositionDistributionException();
            this.extraPositionDistribution = extraPositionDistribution;
        }

    }

    @Override
    public void execute(Player playerDealer) {
        effect.execute(playerDealer);
        for(int i = 0; i < this.effect.target.getTargets().length; ++i) {
            this.effect.target.getTargets()[i].changePosition(
                    extraPositionDistribution[i].getCoordX(),
                    extraPositionDistribution[i].getCoordY()
            );
        }
    }
}
