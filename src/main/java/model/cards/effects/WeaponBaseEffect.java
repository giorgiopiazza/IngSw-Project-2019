package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.AmmoQuantity;
import model.player.PlayerPosition;
import network.message.EffectRequest;
import network.message.ShootRequest;
import utility.EffectValidator;

import java.util.List;
import java.util.Map;

public class WeaponBaseEffect extends Effect {
    private AmmoQuantity cost;

    public WeaponBaseEffect(AmmoQuantity cost, Map<String, String> properties, TargetType[] targets) {
        setCost(cost);
        setProperties(properties);
        setTargets(targets);
    }

    /**
     * Setter of the cost of an Effect
     *
     * @param cost the cost of the effect
     */
    public void setCost(AmmoQuantity cost) {
        this.cost = cost;
    }

    /**
     * @return the cost of the Effect
     */
    public AmmoQuantity getCost() {
        return this.cost;
    }

    @Override
    public void execute(EffectRequest request) {
        // Basic Effect does nothing
    }

    @Override
    public boolean validate(EffectRequest request) {
        ShootRequest shootRequest = (ShootRequest) request;

        if (getTargets().length > 1) { // This effect has subEffects
            for (TargetType targetType : getTargets()) { // Checks that every subEffect is valid
                if (!subValidate(shootRequest, EffectValidator.getSubMap(getProperties(), targetType), targetType)) {
                    return false;
                }
            }
            return true;
        } else { // Checks the effect normally
            return subValidate(shootRequest, getProperties(), getTargets()[0]);
        }
    }

    private boolean subValidate(ShootRequest request, Map<String, String> properties, TargetType targetType) {
        PlayerPosition shooterPosition = Game.getInstance().getUserPlayerByUsername(request.getSenderUsername()).getPosition();
        List<PlayerPosition> targetPositions = EffectValidator.getTargetPositions(request, targetType);

        // Command targets validation
        if (!EffectValidator.isTargetValid(request, properties, targetType))
            return false;

        // Player moves validation
        if (!EffectValidator.isMoveValid(request, properties))
            return false;

        // Simulates player movement before shooting
        if (request.isMoveSenderFirst()) {
            shooterPosition = request.getSenderMovePosition();
        }

        // Move before validation
        if (targetType == TargetType.PLAYER) {
            if (!EffectValidator.isMoveBeforeValid(request, properties)) {
                return false;
            } else if (request.isMoveTargetsFirst()) { // Simulates targets movements before shooting
                targetPositions = request.getTargetPlayersMovePositions();
            }
        }

        // Target distance validation
        if (!EffectValidator.isDistanceValid(properties, shooterPosition, targetPositions, targetType)) {
            return false;
        }

        // Target visibility validation
        if (!EffectValidator.isVisibilityValid(properties, shooterPosition, targetPositions)) {
            return false;
        }

        // Simulates player movement after shooting
        if (!request.isMoveSenderFirst() && request.getSenderMovePosition() != null) {
            shooterPosition = request.getSenderMovePosition();
        }

        // Simulates targets movements after shooting
        if (targetType == TargetType.PLAYER && request.isMoveTargetsFirst() && request.getTargetPlayersMovePositions() != null) {
            targetPositions = request.getTargetPlayersMovePositions();
        }

        // After move positioning validation
        return !(targetType == TargetType.PLAYER && !EffectValidator.isPositioningValid(properties, shooterPosition, targetPositions));
    }
}
