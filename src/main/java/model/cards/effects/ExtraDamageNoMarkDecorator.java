package model.cards.effects;

import model.Game;
import model.player.Player;
import network.message.EffectRequest;

import java.util.List;

public class ExtraDamageNoMarkDecorator extends ExtraEffectDecorator {
    private static final long serialVersionUID = -2582923298178193774L;
    private final int[] damageDistribution;

    public ExtraDamageNoMarkDecorator(Effect effect, int[] extraDamageDistribution) {
        this.effect = effect;
        this.description = effect.description;
        setProperties(effect.getProperties());
        this.targets = effect.targets;
        this.cost = effect.cost;
        this.damageDistribution = extraDamageDistribution;
    }

    /**
     * Method that spreads the damages of the effect to all targets without triggering the Marks.
     * A target can be {@code PLAYER}, {@code SQUARE} or {@code ROOM} and the damage
     * distribution depends on this.
     *
     * @param request of the effect
     */
    @Override
    public void execute(EffectRequest request) {
        effect.execute(request);

        List<String> targetsUsername;
        Player shooter = Game.getInstance().getUserPlayerByUsername(request.getSenderUsername());
        targetsUsername = request.getTargetPlayersUsername();

        for (int i = 0; i < targetsUsername.size(); ++i) {
            Game.getInstance().getUserPlayerByUsername(targetsUsername.get(i)).getPlayerBoard().addDamageNoMark(shooter, damageDistribution[i]);
        }
    }
}
