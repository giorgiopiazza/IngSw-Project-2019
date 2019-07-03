package model.cards.effects;

import enumerations.TargetType;
import model.player.AmmoQuantity;
import model.player.PlayerPosition;
import network.message.EffectRequest;
import network.message.ShootRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponBaseEffect extends Effect {

    private static final long serialVersionUID = 6933102168404912677L;

    public WeaponBaseEffect(AmmoQuantity cost, HashMap<String, String> properties, TargetType[] targets, String description) {
        this.cost = cost;
        this.targets = targets;
        setProperties(properties);
        this.description = description;
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
        PlayerPosition shooterPosition = EffectValidator.checkAdrenalineMove(request);
        List<PlayerPosition> targetPositions = EffectValidator.getTargetPositions(request, targetType);



        // Command targets validation
        if (!EffectValidator.isTargetValid(request, properties, targetType))
            return false;

        // Player moves validation
        if (!EffectValidator.isMoveValid(request, properties))
            return false;

        // Simulates player movement before or while shooting
        if (request.isMoveSenderFirst()) {
            shooterPosition = request.getSenderMovePosition();
        } else if (request.isMoveInMiddle()) {
            return EffectValidator.isMoveInMiddleValid(request);
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
